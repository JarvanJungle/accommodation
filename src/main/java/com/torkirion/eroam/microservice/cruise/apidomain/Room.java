package com.torkirion.eroam.microservice.cruise.apidomain;

import java.io.Serializable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Room implements Serializable
{
	private String roomName;
}
