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
public class EHCacheWrapper<K,V> implements Cache<K,V>
{
	private org.ehcache.Cache<K,V> ehCache = null;
	
	public EHCacheWrapper(org.ehcache.Cache<K,V> ehCache) throws Exception
	{
		this.ehCache = ehCache;
	}

	@Override
	public V get(K key)
	{
		return ehCache.get(key);
	}

	@Override
	public Map<K, V> getAll(Set<? extends K> keys)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsKey(K key)
	{
		return ehCache.containsKey(key);
	}

	@Override
	public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(K key, V value)
	{
		ehCache.put(key, value);
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
		boolean exists = containsKey(key); 
		ehCache.remove(key);
		return exists;
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
