package com.torkirion.eroam.microservice.activities.endpoint.viatorV2.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.activities.apidomain.ActivityBookRQ;
import com.torkirion.eroam.microservice.activities.apidomain.BookingAnswers;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class ConfirmBooking extends ConfirmHoldBooking
{
	@Data
	public static class LanguageGuide
	{
		@JsonProperty("type")
		private String type;

		@JsonProperty("language")
		private String language;
	}

	@Data
	public static class BookerInfo
	{
		@JsonProperty("firstName")
		private String firstName;

		@JsonProperty("lastName")
		private String lastName;
	}

	@Data
	public static class BookingQuestionAnswers
	{
		@JsonProperty("question")
		private String question;

		@JsonProperty("answer")
		private String answer;

		@JsonProperty("travelerNum")
		private Integer travelerNum;

		private String unit;
	}

	@Data
	public static class Communication
	{
		@JsonProperty("email")
		private String email;

		@JsonProperty("phone")
		private String phone;
	}

	@Data
	public static class AdditionalBookingDetails
	{
		@JsonProperty("voucherDetails")
		private VoucherDetails voucherDetails;
	}

	@Data
	public static class VoucherDetails
	{
		@JsonProperty("companyName")
		private String companyName;

		@JsonProperty("email")
		private String email;

		@JsonProperty("phone")
		private String phone;

		@JsonProperty("voucherText")
		private String voucherText;
	}

	private LanguageGuide languageGuide;

	//private String bookingRef;

	private String partnerBookingRef;

	private BookerInfo bookerInfo;

	private String startTime;

	private List<BookingQuestionAnswers> bookingQuestionAnswers = new ArrayList<>();

	private Communication communication;

	private AdditionalBookingDetails additionalBookingDetails;

	@JsonIgnore
	private String internalItemReference;

	public ConfirmBooking() {
	}

	public ConfirmBooking(ActivityBookRQ bookRQ, ActivityBookRQ.ActivityRequestItem item) {
		this.setBookerInfo(new BookerInfo());
		this.getBookerInfo().setFirstName(bookRQ.getBooker().getGivenName());
		this.getBookerInfo().setLastName(bookRQ.getBooker().getSurname());
		this.getBookerInfo().setLastName(bookRQ.getBooker().getSurname());
		this.setCommunication(new Communication());
		this.setAdditionalBookingDetails(null);
		this.setStartTime(item.getDepartureId());

		//private LanguageGuide languageGuide;
		this.setLanguageGuide(new LanguageGuide());
		this.getLanguageGuide().setLanguage("en");
		this.getLanguageGuide().setType("AUDIO");

		if ( item.getBookingQuestionAnswers() != null )
		{
			this.setBookingQuestionAnswers(new ArrayList<>());
			for ( BookingAnswers bookingQuestionAnswer :  item.getBookingQuestionAnswers() )
			{
				ConfirmBooking.BookingQuestionAnswers answer = new ConfirmBooking.BookingQuestionAnswers();
				answer.setQuestion(bookingQuestionAnswer.getQuestionId());
				answer.setAnswer(bookingQuestionAnswer.getAnswer());
				if ( bookingQuestionAnswer.getTravelerNum() != null)
					answer.setTravelerNum(bookingQuestionAnswer.getTravelerNum() + 1);
				if("PICKUP_POINT".equals(answer.getQuestion())) {
					answer.setUnit("FREETEXT");
				}
				this.getBookingQuestionAnswers().add(answer);
			}
		}
	}
}
