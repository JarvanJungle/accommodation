package com.torkirion.eroam.microservice.config;

import java.sql.*;
import java.util.*;

import javax.persistence.EntityManagerFactory;
import javax.sql.*;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@EnableJpaRepositories(basePackages = { "com.torkirion.eroam.microservice" }, entityManagerFactoryRef = "masterEntityManagerFactory", transactionManagerRef = "masterTransactionManager")
public class MasterPersistenceConfig
{
	private final ConfigurableListableBeanFactory beanFactory;

	private final JpaProperties jpaProperties;

	private static final String entityPackages = "com.torkirion.eroam.microservice";

	@Autowired
	public MasterPersistenceConfig(ConfigurableListableBeanFactory beanFactory, JpaProperties jpaProperties)
	{
		this.beanFactory = beanFactory;
		this.jpaProperties = jpaProperties;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(DataSource dataSource)
	{
		log.debug("masterEntityManagerFactory::enter");
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

		em.setPersistenceUnitName("master-persistence-unit");
		em.setPackagesToScan(entityPackages);
		em.setDataSource(dataSource);

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);

		Map<String, Object> properties = new HashMap<>(this.jpaProperties.getProperties());
		properties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
		properties.put(AvailableSettings.IMPLICIT_NAMING_STRATEGY, "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
		properties.put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(this.beanFactory));
		em.setJpaPropertyMap(properties);

		return em;
	}

	@Bean
	public JpaTransactionManager masterTransactionManager(@Qualifier("masterEntityManagerFactory") EntityManagerFactory emf)
	{
		log.debug("masterTransactionManager::enter");
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}
}