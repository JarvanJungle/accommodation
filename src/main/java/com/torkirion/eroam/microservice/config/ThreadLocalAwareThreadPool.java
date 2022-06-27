package com.torkirion.eroam.microservice.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadLocalAwareThreadPool extends ThreadPoolExecutor
{
	private String _tenantId;
	
	public ThreadLocalAwareThreadPool(String tenantId, int nThreads)
	{
		super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		this._tenantId = tenantId;
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r)
	{
		log.debug("beforeExecute::tenantId=" + _tenantId);
		super.beforeExecute(t, r);
		TenantContext.setTenantId(_tenantId);
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t)
	{
		log.debug("afterExecute::clearing tenantId");
		TenantContext.clear();
		super.afterExecute(r, t);
	}
}