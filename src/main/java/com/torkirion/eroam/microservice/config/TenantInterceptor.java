package com.torkirion.eroam.microservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TenantInterceptor implements WebRequestInterceptor
{
	@Autowired
	ApplicationConfig applicationConfig;
	
	@Override
	public void preHandle(WebRequest request) throws Exception
	{
		log.debug("preHandle::enter with request header " + request.getHeader("X-imsclient"));
		String tenantId = null;
		if (request.getHeader("X-imsclient") != null)
		{
			tenantId = request.getHeader("X-imsclient");
		}
		else
		{
			if ( applicationConfig.getIms().getDefaultTentant() != null && applicationConfig.getIms().getDefaultTentant().length() > 0 )
			{
				tenantId = applicationConfig.getIms().getDefaultTentant();
				log.debug("preHandle::set to default " + tenantId);
			}
		}
		TenantContext.setTenantId(tenantId);
	}

	@Override
	public void postHandle(WebRequest request, ModelMap model) throws Exception
	{
		TenantContext.clear();
	}

	@Override
	public void afterCompletion(WebRequest request, Exception ex) throws Exception
	{
	}

}