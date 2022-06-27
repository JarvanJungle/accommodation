package com.torkirion.eroam.microservice.config;

import java.sql.*;
import java.util.*;

import javax.persistence.EntityManagerFactory;
import javax.sql.*;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = { "com.torkirion.eroam.ims" }, entityManagerFactoryRef = "tenantEntityManagerFactory", transactionManagerRef = "tenantTransactionManager")
public class TenantPersistenceConfig
{
	private final ConfigurableListableBeanFactory beanFactory;

	private final JpaProperties jpaProperties;

	private final static String entityPackages = "com.torkirion.eroam.ims";

	@Autowired
	public TenantPersistenceConfig(ConfigurableListableBeanFactory beanFactory, JpaProperties jpaProperties)
	{
		this.beanFactory = beanFactory;
		this.jpaProperties = jpaProperties;
	}

	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean tenantEntityManagerFactory(@Qualifier("schemaBasedMultiTenantConnectionProvider") MultiTenantConnectionProvider connectionProvider,
			@Qualifier("currentTenantIdentifierResolver") CurrentTenantIdentifierResolver tenantResolver)
	{
		log.debug("tenantEntityManagerFactory::enter");
		LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
		emfBean.setPersistenceUnitName("tenant-persistence-unit");
		emfBean.setPackagesToScan(entityPackages);

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		emfBean.setJpaVendorAdapter(vendorAdapter);

		Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
		properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
		properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
		properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
		properties.remove(AvailableSettings.DEFAULT_SCHEMA);
		properties.put(AvailableSettings.MULTI_TENANT, MultiTenancyStrategy.SCHEMA);
		properties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
		properties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantResolver);
		emfBean.setJpaPropertyMap(properties);

		return emfBean;
	}

	@Primary
	@Bean
	public JpaTransactionManager tenantTransactionManager(@Qualifier("tenantEntityManagerFactory") EntityManagerFactory emf)
	{
		log.debug("tenantTransactionManager::enter");
		JpaTransactionManager tenantTransactionManager = new JpaTransactionManager();
		tenantTransactionManager.setEntityManagerFactory(emf);
		return tenantTransactionManager;
	}
}