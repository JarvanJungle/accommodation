package com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds;

import java.time.LocalDate;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HotelbedsCache
{
	public static class RateComment
	{
		LocalDate dateFrom;

		LocalDate dateTo;

		String comment;
	}

	private static Map<String, Optional<String>> boards = new HashMap<>();

	private static Map<String, Optional<String>> promotions = new HashMap<>();

	private static Map<String, Optional<String>> facilityGroups = new HashMap<>();

	private static Map<String, Optional<String>> categoryType = new HashMap<>();

	private static Map<String, Set<RateComment>> rateComments = new HashMap<>();

	private static Map<String, Optional<String>> categoryDescription = new HashMap<>();

	private static Map<String, Optional<String>> chains = new HashMap<>();

	private static Map<String, Optional<String>> issueDescription = new HashMap<>();

	private static Map<String, Optional<String>> imageDescription = new HashMap<>();

	private static Map<String, Optional<String>> facilityDescription = new HashMap<>();

	private static Map<String, Optional<String>> segmentDescription = new HashMap<>();

	private static Map<String, Optional<String>> terminalDescription = new HashMap<>();

	private StaticRepo staticRepo;

	public void clear()
	{
		log.info("clear::clearing caches");
		boards.clear();
		promotions.clear();
		facilityGroups.clear();
		categoryType.clear();
		rateComments.clear();
		categoryDescription.clear();
		chains.clear();
		issueDescription.clear();
		imageDescription.clear();
		facilityDescription.clear();
		segmentDescription.clear();
		terminalDescription.clear();
	}
	
	public HotelbedsCache(StaticRepo staticRepo)
	{
		this.staticRepo = staticRepo;
	}

	public Optional<String> getChainDescription(String chainCode)
	{
		if (chainCode == null)
			return Optional.empty();
		
		if (chains.get(chainCode) != null)
		{
			return chains.get(chainCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("CategoriesRS", chainCode);
		if (l != null && l.size() > 0)
		{
			String description = l.get(0).getDescription();
			if (description != null)
			{
				Optional<String> c = Optional.of(description);
				chains.put(chainCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		chains.put(chainCode, c);
		return c;
	}

	public Optional<String> getCategoryDescription(String categoryCode)
	{
		if (categoryCode == null)
			return Optional.empty();

		if (categoryDescription.get(categoryCode) != null)
		{
			return categoryDescription.get(categoryCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("CategoriesRS", categoryCode);
		if (l != null && l.size() > 0)
		{
			String type = l.get(0).getDescription();
			if (type != null)
			{
				Optional<String> c = Optional.of(type);
				categoryDescription.put(categoryCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		categoryDescription.put(categoryCode, c);
		return c;
	}

	public Optional<String> getCategoryType(String categoryCode)
	{
		if (categoryCode == null)
			return Optional.empty();

		if (categoryType.get(categoryCode) != null)
		{
			return categoryType.get(categoryCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("CategoriesRS", categoryCode);
		if (l != null && l.size() > 0)
		{
			String type = l.get(0).getCgroup();
			if (type != null)
			{
				Optional<String> c = Optional.of(type);
				categoryType.put(categoryCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		categoryType.put(categoryCode, c);
		return c;
	}

	public Optional<String> getBoard(String boardCode)
	{
		if (boardCode == null)
			return Optional.empty();

		if (boards.get(boardCode) != null)
		{
			return boards.get(boardCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("BoardsRS", boardCode);
		if (l != null && l.size() > 0)
		{
			String type = l.get(0).getCgroup();
			if (type != null)
			{
				Optional<String> c = Optional.of(type);
				boards.put(boardCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		boards.put(boardCode, c);
		return c;
	}

	public Optional<String> getPromotion(String promotionCode)
	{
		if (promotionCode == null)
			return Optional.empty();

		if (promotions.get(promotionCode) != null)
		{
			return promotions.get(promotionCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("PromotionsRS", promotionCode);
		if (l != null && l.size() > 0)
		{
			String type = l.get(0).getCgroup();
			if (type != null)
			{
				Optional<String> c = Optional.of(type);
				promotions.put(promotionCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		promotions.put(promotionCode, c);
		return c;
	}

	public Optional<String> getIssueDescription(String issueType, String issueCode)
	{
		if (issueType == null || issueCode == null)
			return Optional.empty();

		String code = issueType + issueCode;
		if (issueDescription.get(code) != null)
		{
			return issueDescription.get(code);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("IssuesRS", code);
		if (l != null && l.size() > 0)
		{
			String val = l.get(0).getDescription();
			if (val != null)
			{
				Optional<String> c = Optional.of(val);
				issueDescription.put(code, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		issueDescription.put(code, c);
		return c;
	}

	public Optional<String> getImageDescription(String imageCode)
	{
		if (imageCode == null)
		{
			return Optional.empty();
		}

		if (imageDescription.get(imageCode) != null)
		{
			return imageDescription.get(imageCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("ImageTypesRS", imageCode);
		if (l != null && l.size() > 0)
		{
			String val = l.get(0).getDescription();
			if (val != null)
			{
				Optional<String> c = Optional.of(val);
				imageDescription.put(imageCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		imageDescription.put(imageCode, c);
		return c;
	}

	public Optional<String> getFacilityDescription(String facilityCode)
	{
		if (facilityCode == null)
			return Optional.empty();

		if (facilityDescription.get(facilityCode) != null)
		{
			return facilityDescription.get(facilityCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("FacilitiesRS", facilityCode);
		if (l != null && l.size() > 0)
		{
			String val = l.get(0).getDescription();
			if (val != null)
			{
				Optional<String> c = Optional.of(val);
				facilityDescription.put(facilityCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		facilityDescription.put(facilityCode, c);
		return c;
	}

	public Optional<String> getSegmentDescription(String segmentCode)
	{
		if (segmentCode == null)
			return Optional.empty();

		if (segmentDescription.get(segmentCode) != null)
		{
			return segmentDescription.get(segmentCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("SegmentsRS", segmentCode);
		if (l != null && l.size() > 0)
		{
			String val = l.get(0).getDescription();
			if (val != null)
			{
				Optional<String> c = Optional.of(val);
				segmentDescription.put(segmentCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		segmentDescription.put(segmentCode, c);
		return c;
	}

	public Optional<String> getTerminalDescription(String terminalCode)
	{
		if (terminalCode == null)
			return Optional.empty();

		if (terminalDescription.get(terminalCode) != null)
		{
			return terminalDescription.get(terminalCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("TerminalsRS", terminalCode);
		if (l != null && l.size() > 0)
		{
			String val = l.get(0).getDescription();
			if (val != null)
			{
				Optional<String> c = Optional.of(val);
				terminalDescription.put(terminalCode, c);
				return c;
			}
		}
		Optional<String> c = Optional.empty();
		terminalDescription.put(terminalCode, c);
		return c;

	}

	public Set<RateComment> getRateComments(String rateCommentCode)
	{
		if (rateCommentCode == null)
			return new HashSet<>();

		if (rateComments.get(rateCommentCode) != null)
		{
			return rateComments.get(rateCommentCode);
		}

		List<StaticData> l = staticRepo.findByStaticTypeAndCode("BoardsRS", rateCommentCode);
		if (l != null && l.size() > 0)
		{
			Set<RateComment> rcSet = new HashSet<>();
			for (StaticData sd : l)
			{
				if (sd.getDescription() != null)
				{
					RateComment r = new RateComment();
					r.dateFrom = sd.getDateFrom();
					r.dateTo = sd.getDateTo();
					r.comment = sd.getDescription();
					rcSet.add(r);
				}
			}
			rateComments.put(rateCommentCode, rcSet);
		}
		rateComments.put(rateCommentCode, new HashSet<>());
		return rateComments.get(rateCommentCode);
	}
}
