package com.torkirion.eroam;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan({"com.torkirion.eroam","com.torkirion.eroam.microservice.accommodation.dto"})
@Slf4j
public class SpringBootWebApplication
{
	public static void main(String[] args)
	{
		SLF4JBridgeHandler.removeHandlersForRootLogger();
	    SLF4JBridgeHandler.install();
		SpringApplication.run(SpringBootWebApplication.class, args);
	}
}
