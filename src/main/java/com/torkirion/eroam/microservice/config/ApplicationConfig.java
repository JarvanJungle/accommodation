package com.torkirion.eroam.microservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties(ApplicationConfig.class)
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@PropertySources({ @PropertySource(value = { "classpath:git.properties" }, ignoreResourceNotFound = true),
		@PropertySource(value = { "classpath:META-INF/build-info.properties" }, ignoreResourceNotFound = true),
		@PropertySource(value = { "classpath:application.properties" }, ignoreResourceNotFound = true) })
public class ApplicationConfig
{
	private final ApplicationConfig.Async async = new ApplicationConfig.Async();

	private final ApplicationConfig.Product product = new ApplicationConfig.Product();
	
	private final ApplicationConfig.TransferNameSearcher transferNameSearcher = new ApplicationConfig.TransferNameSearcher();
	
	private final ApplicationConfig.AccommodationNameSearcher accommodationNameSearcher = new ApplicationConfig.AccommodationNameSearcher();
	
	private final ApplicationConfig.IMS ims = new ApplicationConfig.IMS();
	
    @Getter
    @Setter
    public static class Product {
        private boolean all;
        private boolean accommodation;
        private boolean transfers;
        private boolean activities;
        private boolean ims;
    }

    @Getter
    @Setter
    public static class TransferNameSearcher {
        private int loadPageSize = 100000;
    }

    @Getter
    @Setter
    public static class AccommodationNameSearcher {
        private int loadPageSize = 100000;
    }

    @Getter
    @Setter
    public static class IMS {
        private String defaultTentant;
    }

	@Getter
	@Setter
	public static class Async
	{
		private int corePoolSize = 2;

		private int maxPoolSize = 50;

		private int queueCapacity = 10000;
	}
}
