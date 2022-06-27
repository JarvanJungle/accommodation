package com.torkirion.eroam.microservice.transport.endpoint.ctw;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public class LoginRQ 
{
	private String email;
	private String password;
}
