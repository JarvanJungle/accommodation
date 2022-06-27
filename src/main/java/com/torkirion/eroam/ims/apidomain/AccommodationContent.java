package com.torkirion.eroam.ims.apidomain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Because Mehul Joshi could not handle getting an accommodation response to getHotelContentFromMaster with the hotelId being
 * null. He seems to be unable to handle null fields! "So, if hotel Id is unique and would not be copying hotelId then remove
 * hotelId from the API response or make it stay on that field rather disappearing.".  *sigh* OK, so we have to have this ...
 * 
 * @author jadigby
 *
 */
@Data
public class AccommodationContent extends AccommodationContentWithoutId
{
	private String hotelId;
}
