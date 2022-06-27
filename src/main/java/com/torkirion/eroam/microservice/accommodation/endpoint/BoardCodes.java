package com.torkirion.eroam.microservice.accommodation.endpoint;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationResult;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationBookRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationCancelRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRateCheckRS;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRetrieveRQ;
import com.torkirion.eroam.microservice.accommodation.apidomain.AccommodationRetrieveRS;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.RateCheckRQDTO;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription;

public class BoardCodes
{
	public static enum StandardBoardCoes
	{
		RO, SC, B1, B2, BB, BH, BR, HB, FB, TL, AI, AL, AS, CB, AB, DB, CE, CO
	}

	public static String mapBoardDescription(String boardCode)
	{
		if (boardCode == null || boardCode.length() < 2)
		{
			return "Other";
		}
		String twoChars = boardCode.substring(0, 2);
		switch (twoChars)
		{
			case "RO":
				return "Room Only";
			case "SC":
				return "Self Catering";
			case "B1":
				return "Breakfast for 1 guest";
			case "B2":
				return "Breakfast for 2 guests";
			case "BB":
				return "Bed and Breakfast";
			case "BH":
				return "Bed and Breakfast and Half Board";
			case "BR":
				return "Brunch";
			case "HB":
				return "Half Board ((Breakfast and Dinner))";
			case "FB":
				return "Full Board (Breakfast, Lunch, Dinner)";
			case "TL":
				return "All Inclusive";
			case "AI":
				return "All Inclusive";
			case "AL":
				return "Lunch";
			case "AS":
				return "All Inclusive Premium";
			case "CB":
				return "Continental Breakfast";
			case "AB":
				return "American Breakfast";
			case "DB":
				return "Buffet Breakfast";
			case "CE":
				return "Dinner Included";
			case "CO":
				return "Lunch Included";
			default:
				return "Other";
		}
	}
}
