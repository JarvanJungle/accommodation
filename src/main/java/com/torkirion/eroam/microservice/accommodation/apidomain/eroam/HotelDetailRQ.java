package com.torkirion.eroam.microservice.accommodation.apidomain.eroam;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class HotelDetailRQ extends HotelBaseRQ
{
	@ApiModelProperty(notes = "The hotel ID")
	private String hotel_id;
}
