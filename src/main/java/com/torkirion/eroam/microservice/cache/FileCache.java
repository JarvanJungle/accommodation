package com.torkirion.eroam.microservice.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

import com.torkirion.eroam.microservice.accommodation.services.AccommodationSearchService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileCache<K,V> implements Cache<K,V>
{
	public FileCache(String pathPrefix, Integer maxSize, Integer retentionMinutes) throws Exception
	{
		tempDirWithPrefix = Files.createTempDirectory(pathPrefix);
		if ( log.isDebugEnabled())
			log.debug("::tempDirWithPrefix=" + tempDirWithPrefix);
		this.maxSize = maxSize;
		this.retentionMinutes = retentionMinutes;
	}

	private Integer maxSize = 10;
	private Integer retentionMinutes = 10;
	
	private Path tempDirWithPrefix;
	
	private Map<K, LocalDateTime> expiries = new HashMap<>();
	private Map<K, String> filenames = new HashMap<>();
	
	private void expire()
	{
		if ( log.isDebugEnabled())
			log.debug("expire::enter");
		LocalDateTime now = LocalDateTime.now();
		Set<K> keysToRemove = new HashSet<>();
		for ( java.util.Map.Entry<K, LocalDateTime> entry : expiries.entrySet() )
		{
			if ( entry.getValue().isBefore(now))
			{
				keysToRemove.add(entry.getKey());
			}
		}
		for ( K key : keysToRemove )
		{
			if ( log.isDebugEnabled())
				log.debug("expire::removing " + key.toString());
			remove(key);
		}
	}
	
	private void trim()
	{
		if ( log.isDebugEnabled())
			log.debug("trim::enter");
		while ( expiries.size() > maxSize)
		{
			// find the oldest
			LocalDateTime oldestValue = LocalDateTime.MAX;
			K oldestKey = null;
			for ( java.util.Map.Entry<K, LocalDateTime> entry : expiries.entrySet() )
			{
				if ( entry.getValue().isBefore(oldestValue))
				{
					oldestValue = entry.getValue();
					oldestKey = entry.getKey();
				}
			}
			if ( oldestKey != null )
			{
				if ( log.isDebugEnabled())
					log.debug("trim::removing " + oldestKey.toString());
				remove(oldestKey);
			}
		}
	}
	
	@Override
	public V get(K key)
	{
		if ( expiries.get(key) == null )
		{
			if ( log.isDebugEnabled())
				log.debug("get::expiries is null");
			return null;
		}
		File diskFile = new File(tempDirWithPrefix.toFile(), filenames.get(key));
		if ( log.isDebugEnabled())
			log.debug("get::reading key " + key.toString() + " from " + diskFile.toString());
		try ( ObjectInputStream ois = new ObjectInputStream(new FileInputStream(diskFile)))
		{
			@SuppressWarnings("unchecked")
			V value = (V) ois.readObject();
			return value;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<K, V> getAll(Set<? extends K> keys)
	{
		return null;
	}

	@Override
	public boolean containsKey(K key)
	{
		return expiries.get(key) != null;
	}

	@Override
	public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener)
	{
	}

	private static int flipper = 0;
	@Override
	public void put(K key, V value)
	{
		String filename = Long.toString(System.currentTimeMillis()) + Integer.toString(flipper++);
		filenames.put(key, filename);
		File diskFile = new File(tempDirWithPrefix.toFile(), filename);
		if ( log.isDebugEnabled())
			log.debug("put::adding key " + key.toString() + " to " + diskFile.toString());
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(diskFile)))
		{
			oos.writeObject(value);
			expiries.put(key, LocalDateTime.now().plusMinutes(retentionMinutes));
			
			if ( expiries.size() > maxSize)
				trim();
			else
				expire();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public V getAndPut(K key, V value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean putIfAbsent(K key, V value)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(K key)
	{
		if ( key == null || expiries.get(key) == null)
			return false;
		expiries.remove(key);
		File diskFile = new File(tempDirWithPrefix.toFile(), filenames.get(key));
		diskFile.delete();
		return true;
	}

	@Override
	public boolean remove(K key, V oldValue)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V getAndRemove(K key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean replace(K key, V value)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V getAndReplace(K key, V value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAll(Set<? extends K> keys)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheManager getCacheManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isClosed()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> clazz)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public Iterator<Entry<K, V>> iterator()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
