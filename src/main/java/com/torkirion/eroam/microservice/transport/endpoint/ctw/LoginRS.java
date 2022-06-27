package com.torkirion.eroam.microservice.transport.endpoint.ctw;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public class LoginRS 
{
	@Data
	public class User 
	{
		private String id;
		private String email;
		private String role;
	}
	private String code;
	private String message;
	private String token;
	private User user;
}
