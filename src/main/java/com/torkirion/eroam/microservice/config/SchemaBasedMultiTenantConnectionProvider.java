package com.torkirion.eroam.microservice.config;

import java.sql.*;

import javax.sql.*;


import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchemaBasedMultiTenantConnectionProvider implements MultiTenantConnectionProvider
{
	private final transient DataSource datasource;

	@Autowired
	public SchemaBasedMultiTenantConnectionProvider(DataSource datasource)
	{
		this.datasource = datasource;
	}

	@Override
	public Connection getAnyConnection() throws SQLException
	{
		return datasource.getConnection();
	}

	@Override
	public void releaseAnyConnection(Connection connection) throws SQLException
	{
		connection.close();
	}

	@Override
	public Connection getConnection(String tenantIdentifier) throws SQLException
	{
		log.debug("Get connection for tenant {}", tenantIdentifier);
		String tenantSchema = tenantIdentifier;
		final Connection connection = getAnyConnection();
		connection.setSchema(tenantSchema);
		return connection;
	}

	@Override
	public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException
	{
		log.debug("Release connection for tenant {}", tenantIdentifier);
		connection.setSchema(null);
		releaseAnyConnection(connection);
	}

	@Override
	public boolean supportsAggressiveRelease()
	{
		return false;
	}

	@Override
	public boolean isUnwrappableAs(Class unwrapType)
	{
		return MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
	}

	@Override
	public <T> T unwrap(Class<T> unwrapType)
	{
		if (MultiTenantConnectionProvider.class.isAssignableFrom(unwrapType))
		{
			return (T) this;
		}
		else
		{
			throw new UnknownUnwrapTypeException(unwrapType);
		}
	}
}