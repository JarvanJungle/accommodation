package com.torkirion.eroam.microservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableSwagger2
@Slf4j
//public class SwaggerConfig extends WebMvcConfigurationSupport {
public class SwaggerConfig
{
	@Autowired
	private ApplicationConfig applicationConfig;

	@Bean
	public Docket productApi()
	{
		log.debug("productApi::applicationConfig=" + applicationConfig);
		log.debug("productApi::applicationConfig.getProduct=" + applicationConfig.getProduct());
		log.debug("productApi::applicationConfig.getProduct.isAll=" + applicationConfig.getProduct().isAll());
		log.debug("productApi::applicationConfig.getProduct.isAccommodation=" + applicationConfig.getProduct().isAccommodation());
		log.debug("productApi::applicationConfig.getProduct.isTransfers=" + applicationConfig.getProduct().isTransfers());
		log.debug("productApi::applicationConfig.getProduct.isActivities=" + applicationConfig.getProduct().isActivities());
		log.debug("productApi::applicationConfig.getProduct.isIms=" + applicationConfig.getProduct().isIms());

		ApiSelectorBuilder apiSelectorBuilder = new Docket(DocumentationType.SWAGGER_2).select();
		if (applicationConfig.getProduct().isAll())
		{
			apiSelectorBuilder = apiSelectorBuilder.apis(RequestHandlerSelectors.any());
		}
		else if (applicationConfig.getProduct().isAccommodation())
		{
			apiSelectorBuilder = apiSelectorBuilder.apis(RequestHandlerSelectors.basePackage("com.torkirion.eroam.microservice.accommodation.controllers"));
		}
		else if (applicationConfig.getProduct().isTransfers())
		{
			apiSelectorBuilder = apiSelectorBuilder.apis(RequestHandlerSelectors.basePackage("com.torkirion.eroam.microservice.transfers.controllers"));
		}
		else if (applicationConfig.getProduct().isActivities())
		{
			apiSelectorBuilder = apiSelectorBuilder.apis(RequestHandlerSelectors.basePackage("com.torkirion.eroam.microservice.activities.controllers"));
		}
		else if (applicationConfig.getProduct().isIms())
		{
			apiSelectorBuilder = apiSelectorBuilder.apis(RequestHandlerSelectors.basePackage("com.torkirion.eroam.microservice.ims.controllers"));
		}
		return apiSelectorBuilder.paths(PathSelectors.any()).build().apiInfo(metaData());
	}

	private ApiInfo metaData()
	{
		return new ApiInfoBuilder().title("EROAM SERVICE API").description("\"Consolidated eRoam Services\"").version("1.0.0")
				.build();
	}
}
