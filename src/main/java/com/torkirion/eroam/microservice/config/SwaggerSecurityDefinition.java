package com.torkirion.eroam.microservice.config;

import io.swagger.annotations.SecurityDefinition;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(securityDefinition = @SecurityDefinition(apiKeyAuthDefinitions = {}))
public interface SwaggerSecurityDefinition {
}
