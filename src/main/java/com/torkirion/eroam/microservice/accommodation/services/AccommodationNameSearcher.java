package com.torkirion.eroam.microservice.accommodation.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.*;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.rcdata.EstablishmentData;
import com.torkirion.eroam.microservice.config.ApplicationConfig;
import com.torkirion.eroam.microservice.transfers.apidomain.EndpointType;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.AirportTerminalData;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.AirportTerminalRepo;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccommodationNameSearcher
{
	@Autowired
	private AccommodationRCRepo accommodationRCRepo;

	@Autowired
	private ApplicationConfig applicationConfig;

	private static IndexSearcher searcher;

	private static final int MAX_RESULTS = 30;

	@Data
	@AllArgsConstructor
	public static class NameMatch
	{
		private String code;

		private String name;
	}

	@Async
	public void prime()
	{
		log.debug("prime::enter");
		if (applicationConfig.getProduct().isIms())
		{
			long startTime = System.currentTimeMillis();
			try
			{
				File baseDir = new File(System.getProperty("java.io.tmpdir") + "/accommodationNameSearcher");
				Directory directory = FSDirectory.open(baseDir);
				log.debug("prime::saving to " + directory.toString());
				Version matchVersion = Version.LUCENE_47;
				org.apache.lucene.analysis.util.CharArraySet stopWords = new org.apache.lucene.analysis.util.CharArraySet(matchVersion, 1, true);
				Analyzer analyzer = new StandardAnalyzer(matchVersion, stopWords);
				IndexWriterConfig config = new IndexWriterConfig(matchVersion, analyzer);
				IndexWriter writer = new IndexWriter(directory, config);
				writer.deleteAll();
				log.debug("prime::created fresh writer");

				int loadPageSize = applicationConfig.getAccommodationNameSearcher().getLoadPageSize();
				log.debug("prime::loadPageSize=" + loadPageSize);

				Pageable pageable = PageRequest.of(0, loadPageSize);

				int counter = 0;
				Slice<AccommodationRCRepo.CodeAndName> codeAndNamesSlice = null;
				do
				{
					codeAndNamesSlice = accommodationRCRepo.findCodeAndName(pageable);
					List<AccommodationRCRepo.CodeAndName> codeAndNames = codeAndNamesSlice.getContent();

					for (AccommodationRCRepo.CodeAndName codeAndName : codeAndNames)
					{
						if (counter++ % 10000 == 0)
							log.debug("prime::loading record " + counter);
						String searchName = StringUtils.trimToEmpty(codeAndName.getAccommodationName());
						if (hotelAllowedInNameSearch(codeAndName.getCode()))
						{

							Document document = new Document();
							document.add(new TextField("name", searchName, Store.YES));
							document.add(new StringField("code", codeAndName.getCode(), Store.YES));
							// document.add(new StringField("country", country, Store.YES));
							writer.addDocument(document);
						}
					}
					pageable = codeAndNamesSlice.nextPageable();
				}
				while (codeAndNamesSlice.hasNext());
				log.info("prime::loaded " + counter + " hotel names index in " + (System.currentTimeMillis() - startTime) + " millis");

				writer.close();

				IndexReader reader = DirectoryReader.open(directory);
				searcher = new IndexSearcher(reader);
			}
			catch (Throwable e)
			{
				log.error("prime::failed to prime:" + e.toString(), e);
			}
		}
		log.debug("prime::finished");
	}

	public List<NameMatch> getMatches(String term)
	{
		log.debug("getMatches::enter on term " + term);
		long startTime = System.currentTimeMillis();

		term = term.toLowerCase();

		try
		{
			List<NameMatch> results = new ArrayList<>();
			Set<String> dupCheck = new HashSet<>();
			if (results.size() < MAX_RESULTS)
			{
				List<NameMatch> l = getBooleanTermMatches(term, dupCheck);
				results.addAll(l);
			} /*
				 * if ( results.size() <= MAX_RESULTS) { List<String> l = getRegexTermMatches(term, dupCheck); results.addAll(l);
				 * dupCheck.addAll(l); }
				 */
			if (results.size() < MAX_RESULTS)
			{
				List<NameMatch> l = getMultiPhraseMatches(term, dupCheck);
				results.addAll(l);
			}
			/*
			 * if ( results.size() <= MAX_RESULTS) { List<String> l = getFuzzyMatches(term, dupCheck); results.addAll(l);
			 * dupCheck.addAll(l); }
			 */
			log.debug("getMatches::loaded " + results.size() + " entries in " + (System.currentTimeMillis() - startTime) + " millis");
			return results;
		}
		catch (Exception e)
		{
			log.info("getMatches::failed with " + e.toString(), e);
		}

		return null;
	}

	private static String[] IGNORE_LIST = { "@", "-", "&" };

	protected List<NameMatch> getBooleanTermMatches(String term, Set<String> dupCheck)
	{
		log.debug("getBooleanTermMatches::enter on term " + term);
		long startTime = System.currentTimeMillis();

		try
		{
			List<NameMatch> results = new ArrayList<>();
			BooleanQuery query = new BooleanQuery();
			String split[] = StringUtils.split(term);
			terms: for (int i = 0; i < split.length; i++)
			{
				for (int j = 0; j < IGNORE_LIST.length; j++)
				{
					if (IGNORE_LIST[j].equals(split[i]))
						continue terms;
				}
				TermQuery q = new TermQuery(new Term("name", split[i]));
				if (StandardAnalyzer.STOP_WORDS_SET.contains(split[i]))
				{
					query.add(q, Occur.SHOULD);
					log.debug("getBooleanTermMatches::adding " + q + " as SHOULD");
				}
				else
				{
					query.add(q, Occur.MUST);
					log.debug("getBooleanTermMatches::adding " + q + " as MUST");
				}
			}

			TopDocs docs = searcher.search(query, 60);
			ScoreDoc[] hits = docs.scoreDocs;
			for (int i = 0; i < hits.length; ++i)
			{
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				log.debug("getBooleanTermMatches::found document[" + i + " as " + d);
				String name = d.get("name");
				String code = d.get("code");
				if (!dupCheck.contains(name))
				{
					results.add(new NameMatch(code, name));
					dupCheck.add(name);
					if (results.size() >= MAX_RESULTS)
						break;
				}
			}

			if (results.size() < MAX_RESULTS)
			{
				log.debug("getBooleanTermMatches::trying partial words");
				query = new BooleanQuery();
				split = StringUtils.split(term);
				terms: for (int i = 0; i < split.length; i++)
				{
					for (int j = 0; j < IGNORE_LIST.length; j++)
					{
						if (IGNORE_LIST[j].equals(split[i]))
							continue terms;
					}
					WildcardQuery q = new WildcardQuery(new Term("name", split[i] + "*"));
					if (StandardAnalyzer.STOP_WORDS_SET.contains(split[i]))
					{
						query.add(q, Occur.SHOULD);
						log.debug("getBooleanTermMatches::adding " + q + " as SHOULD");
					}
					else
					{
						query.add(q, Occur.MUST);
						log.debug("getBooleanTermMatches::adding " + q + " as MUST");
					}
				}

				docs = searcher.search(query, 60);
				hits = docs.scoreDocs;
				for (int i = 0; i < hits.length; ++i)
				{
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);
					log.debug("getBooleanTermMatches::found document[" + i + " as " + d);
					String name = d.get("name");
					String code = d.get("code");
					if (!dupCheck.contains(name))
					{
						results.add(new NameMatch(code, name));
						dupCheck.add(name);
						if (results.size() >= MAX_RESULTS)
							break;
					}
				}
			}
			log.debug("getBooleanTermMatches::loaded " + results.size() + " entries in " + (System.currentTimeMillis() - startTime) + " millis");
			return results;
		}
		catch (Exception e)
		{
			log.info("getBooleanTermMatches::failed with " + e.toString(), e);
		}

		return null;
	}

	protected List<NameMatch> getMultiPhraseMatches(String term, Set<String> dupCheck)
	{
		log.debug("getMultiPhraseMatches::enter on term " + term);
		long startTime = System.currentTimeMillis();

		try
		{
			List<NameMatch> results = new ArrayList<>();
			MultiPhraseQuery query = new MultiPhraseQuery();
			String split[] = StringUtils.split(term);
			for (int i = 0; i < split.length; i++)
			{
				Term t = new Term("name", split[i]);
				query.add(t);
			}
			log.debug("getMultiPhraseMatches::query=" + query);

			TopDocs docs = searcher.search(query, 60);
			ScoreDoc[] hits = docs.scoreDocs;
			for (int i = 0; i < hits.length; ++i)
			{
				int docId = hits[i].doc;
				Document d = searcher.doc(docId);
				log.debug("getMultiPhraseMatches::found document[" + i + " as " + d);
				String name = d.get("name");
				String code = d.get("code");
				if (!dupCheck.contains(name))
				{
					results.add(new NameMatch(code, name));
					dupCheck.add(name);
					if (results.size() >= MAX_RESULTS)
						break;
				}
			}

			log.debug("getMultiPhraseMatches::loaded " + results.size() + " entries in " + (System.currentTimeMillis() - startTime) + " millis");
			return results;
		}
		catch (Exception e)
		{
			log.info("getMultiPhraseMatches::failed with " + e.toString(), e);
		}

		return null;
	}

	private static boolean hotelAllowedInNameSearch(String code)
	{
		return true;
	}

}
