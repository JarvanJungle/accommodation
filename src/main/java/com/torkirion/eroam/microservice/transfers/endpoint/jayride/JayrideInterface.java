package com.torkirion.eroam.microservice.transfers.endpoint.jayride;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.spi.CachingProvider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.datadomain.AccommodationRCRepo;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoCancelRQ;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoCancelRS;
import com.torkirion.eroam.microservice.accommodation.endpoint.yalago.YalagoHttpService;
import com.torkirion.eroam.microservice.apidomain.CurrencyValue;
import com.torkirion.eroam.microservice.apidomain.LatitudeLongitude;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRQ;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferBookRS;
import com.torkirion.eroam.microservice.transfers.apidomain.Endpoint;
import com.torkirion.eroam.microservice.transfers.apidomain.EndpointType;
import com.torkirion.eroam.microservice.transfers.apidomain.EndpointExtended;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferCancellationPolicyLine;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferDirection;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult.ImageTag;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferResult.Luggage;
import com.torkirion.eroam.microservice.transfers.dto.SearchRQDTO;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferSupplier;
import com.torkirion.eroam.microservice.transfers.apidomain.TransferType;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.CreateQuoteRS.Quote;
import com.torkirion.eroam.microservice.transfers.endpoint.jayride.CreateQuoteRS.RefundPolicy;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JayrideInterface
{
	private SystemPropertiesDAO properties;

	private JayrideAPIProperties jayrideAPIProperties;

	private static final BigDecimal NETT_MARKDOWN = new BigDecimal("0.92");

	public JayrideAPIProperties getJayrideAPIProperties()
	{
		return jayrideAPIProperties;
	}

	public JayrideInterface(SystemPropertiesDAO properties, String site, String channel) throws Exception
	{
		this.properties = properties;
		init(site, channel);
	}

	private void init(String site, String channel) throws Exception
	{
		log.debug("init::entering with site " + site + ", channel " + channel);

		jayrideAPIProperties = new JayrideAPIProperties(properties, site);
		log.debug("init::jayrideAPIProperties=" + jayrideAPIProperties);
	}

	public List<TransferResult> createQuote(CreateQuoteRQ createQuoteRQ, SearchRQDTO searchRQ) throws Exception
	{
		log.debug("createQuote::entering for createQuoteRQ=" + createQuoteRQ);

		long searchStartTime = System.currentTimeMillis();

		HttpService httpService = new JayrideHttpService(jayrideAPIProperties);

		String responseString = httpService.doCallPost("v2/quote-request?key=" + jayrideAPIProperties.apikey, createQuoteRQ);
		log.debug("createQuote::responseString = " + responseString);

		log.debug("createQuote::time taken = " + (System.currentTimeMillis() - searchStartTime));
		try
		{
			CreateQuoteRS createQuoteRS = getObjectMapper().readValue(responseString, CreateQuoteRS.class);
			log.debug("createQuote::createQuoteRS=" + createQuoteRS);
			if (createQuoteRS != null && createQuoteRS.getResults() != null && createQuoteRS.getResults().getQuotes() != null)
			{
				List<TransferResult> transferResults = makeTransferResults(createQuoteRS, searchRQ);
				return transferResults;
			}
			return new ArrayList<>();
		}
		catch (Exception e)
		{
			log.error("createQuote::caught exception " + e.toString(), e);
			return null;
		}
	}

	public BookingRS book(BookingRQ bookingRQ) throws Exception
	{
		log.debug("book::entering for bookingRQ=" + bookingRQ);

		long searchStartTime = System.currentTimeMillis();

		if (jayrideAPIProperties.bypassBooking)
		{
			BookingRS bookingRS = new BookingRS();
			bookingRS.setBookings(new ArrayList<>());
			BookingRS.Booking booking = new BookingRS.Booking();
			booking.setBooking_id("JAY_" + bookingRQ.getQuote_request_id());
			booking.setBooking_status("confirmed");
			bookingRS.getBookings().add(booking);
			booking.setService_info(new BookingRS.ServiceInfo());
			booking.getService_info().setSupplier(new BookingRS.Supplier());
			booking.getService_info().getSupplier().setName("Rocket Transfers");
			booking.getService_info().getSupplier().setDescription("Rocket Transfers");
			booking.getService_info().getSupplier().setEmail("test@gmail.com");
			booking.getService_info().getSupplier().setPhone("+1 413 555 1212");
			booking.getService_info().getSupplier().setId("ABC");
			log.debug("book::dummy booking returned");
			return bookingRS;
		}

		HttpService httpService = new JayrideHttpService(jayrideAPIProperties);

		String responseString = httpService.doCallPost("v2/booking?key=" + jayrideAPIProperties.apikey, bookingRQ);
		log.debug("book::responseString = " + responseString);

		log.debug("book::time taken = " + (System.currentTimeMillis() - searchStartTime));
		BookingRS bookingRS = getObjectMapper().readValue(responseString, BookingRS.class);
		log.debug("book::bookingRS=" + bookingRS);
		return bookingRS;
	}

	public JayrideCancelRS cancel(String bookingId) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("cancel::entering");
		long startTime = System.currentTimeMillis();

		JayrideCancelRQ jayrideCancelRQ = new JayrideCancelRQ();
		jayrideCancelRQ.setMessage("No longer required");

		HttpService httpService = new JayrideHttpService(jayrideAPIProperties);

		String responseString = httpService.doCallPatch("v2/booking/" + bookingId + "/status?key=" + jayrideAPIProperties.apikey, jayrideCancelRQ);
		if (log.isDebugEnabled())
			log.debug("cancel::responseString = " + responseString);

		if (log.isDebugEnabled())
			log.debug("cancel::time taken = " + (System.currentTimeMillis() - startTime));
		try
		{
			JayrideCancelRS jayrideCancelRS = getObjectMapper().readValue(responseString, JayrideCancelRS.class);
			return jayrideCancelRS;
		}
		catch (Exception e)
		{
			log.error("cancel::caught exception " + e.toString(), e);
			return null;
		}
	}

	public JayrideRetrieveRS retrieve(String bookingId) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("retrieve::entering");
		long startTime = System.currentTimeMillis();

		HttpService httpService = new JayrideHttpService(jayrideAPIProperties);

		String responseString = httpService.doCallGet("booking/" + bookingId + "?key=" + jayrideAPIProperties.apikey, null);
		if (log.isDebugEnabled())
			log.debug("retrieve::responseString = " + responseString);

		if (log.isDebugEnabled())
			log.debug("retrieve::time taken = " + (System.currentTimeMillis() - startTime));
		try
		{
			JayrideRetrieveRS jayrideRetrieveRS = getObjectMapper().readValue(responseString, JayrideRetrieveRS.class);
			return jayrideRetrieveRS;
		}
		catch (Exception e)
		{
			log.error("cancel::caught exception " + e.toString(), e);
			return null;
		}
	}

	protected List<TransferResult> makeTransferResults(CreateQuoteRS createQuoteRS, SearchRQDTO searchRQ)
	{
		log.debug("makeTransferResults::entering for " + createQuoteRS.getResults().getQuotes().size() + " transfers");
		List<TransferResult> transferResults = new ArrayList<>();

		for (Quote quote : createQuoteRS.getResults().getQuotes())
		{
			if (!quote.getStatus().equals("price-confirmed"))
			{
				log.warn("makeTransferResults::invalid status " + quote.getStatus() + ", bypassing");
				continue;
			}
			LocalDateTime transferDateEarliest = null;
			if (createQuoteRS.getQuote_request().getFlight() != null)
			{
				if (createQuoteRS.getQuote_request().getFlight().getDeparture_datetime_local() != null)
				{
					LocalDateTime d = LocalDateTime.parse(createQuoteRS.getQuote_request().getFlight().getDeparture_datetime_local(), df_YYYmmddhhmm);
					if (transferDateEarliest == null || d.isBefore(transferDateEarliest))
					{
						transferDateEarliest = d;
					}
				}
				if (createQuoteRS.getQuote_request().getFlight().getLanding_datetime_local() != null)
				{
					LocalDateTime d = LocalDateTime.parse(createQuoteRS.getQuote_request().getFlight().getLanding_datetime_local(), df_YYYmmddhhmm);
					if (transferDateEarliest == null || d.isBefore(transferDateEarliest))
					{
						transferDateEarliest = d;
					}
				}
			}
			TransferResult transferResult = new TransferResult();
			transferResult.setChannel(JayrideService.CHANNEL);
			EndpointExtended startPoint = new EndpointExtended();
			startPoint.setEndpointCode(searchRQ.getStartPoint().getEndpointCode());
			startPoint.setEndpointType(searchRQ.getStartPoint().getEndpointType());
			startPoint.setDescription(createQuoteRS.getQuote_request().getFrom_location().getDescription());
			startPoint.setGeoCoordinates(new LatitudeLongitude(createQuoteRS.getQuote_request().getFrom_location().getLatitude(), createQuoteRS.getQuote_request().getFrom_location().getLongitide()));
			transferResult.setStartPoint(startPoint);
			EndpointExtended endPoint = new EndpointExtended();
			endPoint.setEndpointCode(searchRQ.getEndPoint().getEndpointCode());
			endPoint.setEndpointType(searchRQ.getEndPoint().getEndpointType());
			endPoint.setDescription(createQuoteRS.getQuote_request().getTo_location().getDescription());
			endPoint.setGeoCoordinates(new LatitudeLongitude(createQuoteRS.getQuote_request().getTo_location().getLatitude(), createQuoteRS.getQuote_request().getTo_location().getLongitide()));
			transferResult.setEndPoint(endPoint);
			transferResult.setBookingCode("JR" + quote.getQuote_id() + "/" + createQuoteRS.getQuote_request_id());
			transferResult.setChannelCode(quote.getQuote_id() + "/" + createQuoteRS.getQuote_request_id());
			transferResult.setChannel(JayrideService.CHANNEL);
			switch (quote.getService_info().getType())
			{
				case "shared":
					transferResult.setTransferType(TransferType.SHARED);
					break;
				case "private":
					transferResult.setTransferType(TransferType.PRIVATE);
					break;
				default:
					log.warn("makeTransferResults::unknown transferType  " + quote.getService_info().getType() + ", bypassing");
					continue;
			}
			transferResult.setTransferDescription(quote.getService_info().getDescription());
			if (transferResult.getTransferDescription() == null || transferResult.getTransferDescription().length() == 0)
			{
				StringBuffer description = new StringBuffer(quote.getService_info().getSupplier().getName() + " : ");
				switch (startPoint.getEndpointType())
				{
					case AIRPORT:
						description.append("Airport to ");
						break;
					case FLIGHT:
						description.append("Airport to ");
						break;
					case HOTEL:
						description.append("Hotel to ");
						break;
				}
				switch (endPoint.getEndpointType())
				{
					case AIRPORT:
						description.append("Airport");
						break;
					case FLIGHT:
						description.append("Airport");
						break;
					case HOTEL:
						description.append("Hotel");
						break;
				}
				transferResult.setTransferDescription(description.toString());
			}
			transferResult.setImageUrls(new ArrayList<>());
			if (quote.getService_info().getPhoto_url() != null)
			{
				TransferResult.Image image = new TransferResult.Image();
				image.setImageURL(quote.getService_info().getPhoto_url());
				image.setImageTag(ImageTag.TRANSFER);
				transferResult.getImageUrls().add(image);
			}
			if (quote.getService_info().getPhoto_urls() != null)
			{
				for (String imageURL : quote.getService_info().getPhoto_urls())
				{
					TransferResult.Image image = new TransferResult.Image();
					image.setImageURL(imageURL);
					image.setImageTag(ImageTag.TRANSFER);
					transferResult.getImageUrls().add(image);
				}
			}
			if (quote.getService_info().getSupplier().getPhoto_url() != null)
			{
				TransferResult.Image image = new TransferResult.Image();
				image.setImageURL(quote.getService_info().getSupplier().getPhoto_url());
				image.setImageTag(ImageTag.SUPPLIER);
				transferResult.getImageUrls().add(image);
			}
			transferResult.setSupplier(new TransferSupplier());
			transferResult.getSupplier().setSupplierName(quote.getService_info().getSupplier().getName());
			transferResult.getSupplier().setSupplierDescription(quote.getService_info().getSupplier().getDescription());
			transferResult.getSupplier().setImageUrl(quote.getService_info().getSupplier().getPhoto_url());
			CurrencyValue totalFare = new CurrencyValue(quote.getFare().getCurrency_code(), quote.getFare().getPrice());
			CurrencyValue nettFare = new CurrencyValue(totalFare.getCurrencyId(), totalFare.getAmount().multiply(NETT_MARKDOWN).setScale(2, RoundingMode.HALF_UP));
			transferResult.setTotalRate(totalFare);
			transferResult.setSupplyRate(nettFare);
			transferResult.setCancellationPolicy(makeCNnxPolicy(quote.getFare().getRefund_policies(), transferDateEarliest));
			transferResult.setCancellationPolicyText(quote.getFare().getRefund_cancellation_policy());
			transferResult.setRefundableStatus(Functions.isTransferCNXNonRefundable(transferResult.getCancellationPolicy()));
			if (quote.getLuggage() != null && quote.getLuggage().getInclusive_allowance() != null)
			{
				transferResult.setLuggageDetails(new Luggage());
				transferResult.getLuggageDetails().setInclusiveAllowance(quote.getLuggage().getInclusive_allowance());
				;
			}
			transferResult.setCancellationPolicyText(quote.getFare().getRefund_cancellation_policy());
			transferResult.setBookingConditions(transferResult.getCancellationPolicyText());
			if (quote.getService_info().getPassenger_reviews() != null)
			{
				transferResult.setReviews(new TransferResult.Review());
				transferResult.getReviews().setCount(quote.getService_info().getPassenger_reviews().getCount());
				transferResult.getReviews().setRating(quote.getService_info().getPassenger_reviews().getAverage_rating());
				if ( transferResult.getReviews().getRating().compareTo(BD_3_5) < 0)
				{
					log.warn("makeTransferResults::rating " + transferResult.getReviews().getRating() + " < 3.5, bypassing " + quote.getQuote_id());
					continue;
				}
			}
			transferResults.add(transferResult);
		}
		log.debug("makeTransferResults::returning " + transferResults.size() + " transfers");
		return transferResults;
	}

	protected SortedSet<TransferCancellationPolicyLine> makeCNnxPolicy(List<RefundPolicy> refundPolicy, LocalDateTime transferDateEarliest)
	{
		SortedSet<TransferCancellationPolicyLine> cnxPolicy = new TreeSet<>();

		boolean hasZeroBefore = false;
		for (RefundPolicy refundPolicyLine : refundPolicy)
		{
			if (refundPolicyLine.getMinute_prior() == 0)
			{
				hasZeroBefore = true;
			}
			TransferCancellationPolicyLine transferCancellationPolicyLine = new TransferCancellationPolicyLine();
			BigDecimal penaltyPercent = BigDecimal.ONE.subtract(refundPolicyLine.getPercent()).multiply(BD_100);
			transferCancellationPolicyLine.setPenaltyPercent(penaltyPercent);
			transferCancellationPolicyLine.setBefore(true);
			LocalDateTime cnxdate = transferDateEarliest.minusMinutes(refundPolicyLine.getMinute_prior());
			transferCancellationPolicyLine.setAsOf(cnxdate.toLocalDate());
			transferCancellationPolicyLine.setPenaltyDescription("If cancelled on or before " + df2ddmmmYY.format(cnxdate.toLocalDate()) + ", a " + 0 + "% charge applies");
			cnxPolicy.add(transferCancellationPolicyLine);
		}
		if (!hasZeroBefore)
		{
			TransferCancellationPolicyLine transferCancellationPolicyLine = new TransferCancellationPolicyLine();
			transferCancellationPolicyLine.setPenaltyPercent(BD_100);
			transferCancellationPolicyLine.setBefore(true);
			transferCancellationPolicyLine.setAsOf(transferDateEarliest.toLocalDate());
			transferCancellationPolicyLine.setPenaltyDescription(
					"If cancelled on or before " + df2ddmmmYY.format(transferDateEarliest.toLocalDate()) + ", a " + transferCancellationPolicyLine.getPenaltyPercent() + "% charge applies");
			cnxPolicy.add(transferCancellationPolicyLine);
		}
		return cnxPolicy;
	}

	private final ObjectMapper getObjectMapper()
	{
		if (_objectMapper == null)
		{
			_objectMapper = new ObjectMapper();
			_objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
		return _objectMapper;
	}

	private static DateTimeFormatter df_YYYmmddhhmm = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

	private static DateTimeFormatter df2ddmmmYY = DateTimeFormatter.ofPattern("dd MMM yy");

	private static final BigDecimal BD_100 = new BigDecimal("100");
	private static final BigDecimal BD_3_5 = new BigDecimal("3.5");

	private ObjectMapper _objectMapper = null;
}
