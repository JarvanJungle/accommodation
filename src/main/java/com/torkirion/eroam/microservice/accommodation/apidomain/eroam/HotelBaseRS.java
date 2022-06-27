package com.torkirion.eroam.microservice.accommodation.apidomain.eroam;

import java.math.BigDecimal;
import java.util.*;

import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.accommodation.apidomain.OleryAccommodationData;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
public class HotelBaseRS
{
	@Data
	public static class Image
	{
		private String thumbnail;
		private String main;
	}

	@Data
	public static class HotelImage
	{
		private String hotel_id;
		private Image images;
		private Integer innstant_image_id;
	}

	@Data
	public static class BasicHotelDetail
	{
		private String hotel_id; // note must be String

		private String hotel_name;

		private String address;

		private String zip_code;

		private String phone;

		private BigDecimal star; // note can be 4.5

		private String category;
		
		private String fax_number;

		private BigDecimal latitude;

		private BigDecimal longitude;

		private String description;

		private Integer innstant_city_id;

		private Integer eroam_city_id;

		private String tags;

		private String amenities;

		private CurrencyValue total_retail_price;

		private CurrencyValue total_net_price;

		private List<Hotel.Room> selectedRooms = new ArrayList<>();

		private List<HotelImage> hotel_images = new ArrayList<>();
		
		private OleryAccommodationData oleryData;
		
		private List<Hotel.RoomNumber> hotel_room_list = new ArrayList<>();
	}

	@Data
	public static class Hotel
	{
		private String id; // note must be String

		private String name;

		private Boolean highlightedHotel = Boolean.FALSE;

		private String address;

		private String city;

		private String countryCode;

		private BigDecimal hotelRating; // note can be 4.5

		private String hotelRatingDisplay;

		private String category;

		private String shortDescription;

		private String currency;

		private BigDecimal latitude;

		private BigDecimal longitude;

		private String image;

		private BigDecimal price;

		private BigDecimal retail_price;

		private BigDecimal tax_amount;

		private String provider; // channel

		private String check_in_date;

		private String check_out_date;

		private Integer duration;

		private BigDecimal distance; // from centre of bounding box?

		private CurrencyValue total_retail_price;

		private CurrencyValue total_net_price;

		private Room selectedRoom;

		private List<Room> selectedRooms = new ArrayList<>();

		private Boolean hotel_refundable_status;

		private String hotel_category; // e.g. "highsuite"

		private String phone;

		private OleryAccommodationData oleryData;

		private String meal_plan; // e.g. "AI"

		private List<String> facilities;

		private Boolean selected;

		@Data
		public static class RoomCancellationFrame
		{
			private String from;

			private String to;

			private CurrencyValue penalty;
		}

		@Data
		public static class RoomCancellation
		{
			private String type;

			private String cancellationPolicyText;

			private List<RoomCancellationFrame> frames = new ArrayList<>();
		}

		@Data
		public static class Passengers
		{
			private Integer adults;

			private List<Integer> children;
		}

		@Data
		public static class Quantity
		{
			private Integer min;

			private Integer max;
		}

		@Data
		public static class RoomItem
		{
			private String name;

			private String roomExtraInformation;

			private String category;

			private String bedding;

			private String board;

			private String boardDescription;

			private String hotelId; // must be string, not int

			private Passengers pax;

			private Quantity quantity;

			private Boolean detailsAvailable;
			
			private String matchCode;
		}

		@Data
		public static class Provider
		{
			private String id;

			private String name;
		}

		@Data
		public static class RoomNumber
		{
			private Integer roomNumber;
			
			private List<Hotel.Room> roomList = new ArrayList<>();
		}

		@Data
		public static class Room
		{
			private Integer roomNumber;

			private CurrencyValue price;

			private CurrencyValue netPrice;

			private CurrencyValue barRate;

			private String confirmation = "fully bookable";

			private String paymentType = "pre";

			private Boolean packageRate = false;

			private Boolean commissionable = true;

			private Boolean bundlesOnly = true;

			private List<Provider> providers = new ArrayList<>();

			private List<String> specialOffers = new ArrayList<>();

			private List<RoomItem> items = new ArrayList<>();

			private RoomCancellation cancellation;

			private String bookingConditions;

			private String code;

			private String name;

			private CurrencyValue retail_price;

			private CurrencyValue net_price;
		}
	}
}
