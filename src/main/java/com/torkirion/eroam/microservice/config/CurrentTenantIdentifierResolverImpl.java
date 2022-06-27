package com.torkirion.eroam.microservice.config;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.torkirion.eroam.microservice.accommodation.controllers.AccommodationController;

import lombok.extern.slf4j.Slf4j;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Slf4j
@Component("currentTenantIdentifierResolver")
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver
{
	@Override
	public String resolveCurrentTenantIdentifier()
	{
		String tenantId = TenantContext.getTenantId();
		if (!StringUtils.isEmpty(tenantId))
		{
			return tenantId;
		}
		else
		{
			return "public";
		}
	}

	@Override
	public boolean validateExistingCurrentSessions()
	{
		return true;
	}
}