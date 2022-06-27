package com.torkirion.eroam.microservice.accommodation.endpoint.sabrecsl;

import com.hotelbeds.schemas.messages.*;
import com.sabre.schema.hotel.avail.v4_0_0.*;
import com.sabre.schema.hotel.avail.v4_0_0.DateRangeTypeRef;
import com.sabre.schema.hotel.avail.v4_0_0.PrepaidQualifierType;
import com.sabre.schema.hotel.avail.v4_0_0.RateInfoRef;
import com.sabre.schema.hotel.avail.v4_0_0.Room;
import com.sabre.schema.hotel.avail.v4_0_0.SearchCriteria;
import com.sabre.schema.hotel.avail.v4_0_0.UnitOfMeasureType;
import com.sabre.schema.hotel.content.v4_0_0.*;
import com.sabre.schema.hotel.content.v4_0_0.AdditionalInfoType;
import com.sabre.schema.hotel.content.v4_0_0.ImageTypes;
import com.sabre.schema.hotel.details.v3_0_0.*;
import com.sabre.schema.hotel.details.v3_0_0.DescriptiveInfoRef;
import com.sabre.schema.hotel.details.v3_0_0.Images;
import com.sabre.schema.hotel.details.v3_0_0.MediaRef;
import com.sabre.schema.hotel.details.v3_0_0.MediaTypes;
import com.sabre.schema.hotel.envelope.Body;
import com.sabre.schema.hotel.envelope.Envelope;
import com.sabre.schema.hotel.envelope.Header;
import com.sabre.schema.hotel.messageheader.*;
import com.sabre.schema.hotel.pricecheck.v4_0_0.HotelPriceCheckRQ;
import com.sabre.schema.hotel.pricecheck.v4_0_0.HotelPriceCheckRS;
import com.sabre.schema.hotel.ws._2002._12.secext.Security;
import com.sabre.schema.sp.reservation.v2_4.CreatePassengerNameRecordRQ;
import com.sabre.schema.sp.reservation.v2_4.CreatePassengerNameRecordRS;
import com.sabre.schema.webservices.TokenCreateRQ;
import com.sabre.schema.webservices.TokenCreateRS;
import com.torkirion.eroam.HttpService;
import com.torkirion.eroam.microservice.accommodation.Functions;
import com.torkirion.eroam.microservice.accommodation.apidomain.*;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByGeocordBoxRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchByHotelIdRQDTO;
import com.torkirion.eroam.microservice.accommodation.dto.AvailSearchRQDTO;
import com.torkirion.eroam.microservice.accommodation.endpoint.hotelbeds.HotelbedsService;
import com.torkirion.eroam.microservice.apidomain.ResponseExtraInformation;
import com.torkirion.eroam.microservice.apidomain.Traveller;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.datadomain.SystemProperty;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class SaberCSLInterface
{
	public static final String API_VERSION = "1.0";

	private SystemPropertiesDAO properties;

	private SabreCSLAPIProperties sabreCSLAPIProperties;

	public SabreCSLAPIProperties getSabreCSLAPIProperties()
	{
		return sabreCSLAPIProperties;
	}

	public SaberCSLInterface(SystemPropertiesDAO properties, String site, String channel) throws Exception
	{
		this.properties = properties;
		init(site, channel);
	}

	private void init(String site, String channel) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("init::entering with site " + site + ", channel " + channel + ", properties=" + properties);

		sabreCSLAPIProperties = new SabreCSLAPIProperties(properties, site);

		jaxbContextEnvelope = JAXBContext.newInstance(Envelope.class, MessageHeader.class, Security.class, GetHotelDetailsRQ.class, GetHotelAvailRS.class, GetHotelDetailsRS.class, TokenCreateRQ.class,
				TokenCreateRS.class, HotelPriceCheckRQ.class, HotelPriceCheckRS.class, CreatePassengerNameRecordRQ.class);
	}

	private static final BigDecimal HB_MARKUP = new BigDecimal("1.1363636");

	public static BigDecimal applyInventoryMarkup(BigDecimal nett, BigDecimal gross) throws Exception
	{
		return nett.multiply(HB_MARKUP).setScale(0, RoundingMode.UP);
	}

	public GetHotelAvailRS startSearchHotels(AvailSearchByGeocordBoxRQDTO availSearchRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("startSearchHotels::entering for availSearchRQ=" + availSearchRQ);

		GetHotelAvailRQ availabilityRQ = makeAvailabilityRQ(availSearchRQ);

		// get data by geo search
		availabilityRQ.getSearchCriteria().setGeoSearch(new SearchCriteria.GeoSearch());
		availabilityRQ.getSearchCriteria().getGeoSearch().setGeoRef(new GeoRef());
		availabilityRQ.getSearchCriteria().getGeoSearch().getGeoRef()
				.setRadius(distance(availSearchRQ.getNorthwest().getLatitude().doubleValue(), availSearchRQ.getNorthwest().getLongitude().doubleValue(),
						availSearchRQ.getSoutheast().getLatitude().doubleValue(), availSearchRQ.getSoutheast().getLongitude().doubleValue(), 'M') / 2);
		availabilityRQ.getSearchCriteria().getGeoSearch().getGeoRef().setUOM(UnitOfMeasureType.MI);
		availabilityRQ.getSearchCriteria().getGeoSearch().getGeoRef().setGeoCode(new GeoRef.GeoCode());

		Double lat = (availSearchRQ.getNorthwest().getLatitude().doubleValue() + availSearchRQ.getSoutheast().getLatitude().doubleValue()) / 2;
		Double lon = (availSearchRQ.getNorthwest().getLongitude().doubleValue() + availSearchRQ.getSoutheast().getLongitude().doubleValue()) / 2;
		availabilityRQ.getSearchCriteria().getGeoSearch().getGeoRef().getGeoCode().setLatitude(lat.doubleValue());
		availabilityRQ.getSearchCriteria().getGeoSearch().getGeoRef().getGeoCode().setLongitude(lon.doubleValue());
		return searchHotels(availabilityRQ);
	}

	private GetHotelAvailRQ makeAvailabilityRQ(AvailSearchRQDTO availSearchRQ) throws DatatypeConfigurationException
	{
		if (log.isDebugEnabled())
			log.debug("makeAvailabilityRQ::entering for availSearchRQ=" + availSearchRQ);

		GetHotelAvailRQ availabilityRQ = new GetHotelAvailRQ();

		availabilityRQ.setSearchCriteria(new SearchCriteria());
		availabilityRQ.getSearchCriteria().setSortBy(SortByType.TOTAL_RATE);
		availabilityRQ.getSearchCriteria().setSortOrder(SortOrderType.DESC);
		availabilityRQ.getSearchCriteria().setOffSet(BigInteger.valueOf(1));
		availabilityRQ.getSearchCriteria().setPageSize(200);

		availabilityRQ.getSearchCriteria().setRateInfoRef(new RateInfoRef());
		availabilityRQ.getSearchCriteria().getRateInfoRef().setCurrencyCode("USD");
		availabilityRQ.getSearchCriteria().getRateInfoRef().setBestOnly("2");
		availabilityRQ.getSearchCriteria().getRateInfoRef().setRooms(new RateInfoRef.Rooms());
		int i = 1;
		List<Room> roomList = new ArrayList<>();
		Room room;
		for (TravellerMix travel : availSearchRQ.getTravellers())
		{
			room = new Room();
			room.setAdults(BigInteger.valueOf(travel.getAdultCount()));
			room.setChildren(BigInteger.valueOf(travel.getChildAges().size()));
			room.setIndex(BigInteger.valueOf(i));
			roomList.add(room);
			i++;
		}

		availabilityRQ.getSearchCriteria().getRateInfoRef().getRooms().getRooms().addAll(roomList);
		availabilityRQ.getSearchCriteria().getRateInfoRef().setConvertedRateInfoOnly(false);
		availabilityRQ.getSearchCriteria().getRateInfoRef().setRefundableOnly(false);
		availabilityRQ.getSearchCriteria().getRateInfoRef().setCurrencyCode("AUD");
		availabilityRQ.getSearchCriteria().getRateInfoRef().setPrepaidQualifier(PrepaidQualifierType.EXCLUDE_PREPAID);
		availabilityRQ.getSearchCriteria().getRateInfoRef().setStayDateTimeRange(new DateRangeTypeRef());
		availabilityRQ.getSearchCriteria().getRateInfoRef().getStayDateTimeRange().setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(availSearchRQ.getCheckin().toString()));
		availabilityRQ.getSearchCriteria().getRateInfoRef().getStayDateTimeRange().setEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(availSearchRQ.getCheckout().toString()));

		return availabilityRQ;
	}

	private double distance(double lat1, double lon1, double lat2, double lon2, char unit)
	{
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == 'K')
		{
			dist = dist * 1.609344;
		}
		else if (unit == 'N')
		{
			dist = dist * 0.8684;
		}
		return (dist);
	}

	private double deg2rad(double deg)
	{
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad)
	{
		return (rad * 180.0 / Math.PI);
	}

	private GetHotelAvailRS searchHotels(GetHotelAvailRQ availabilityRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("searchHotels::entering for availabilityRQ=" + availabilityRQ);

		Marshaller marshaller = jaxbContextEnvelope.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		Unmarshaller unmarshaller = jaxbContextEnvelope.createUnmarshaller();

		long searchStartTime = System.currentTimeMillis();

		Envelope request = initEnvelope(availabilityRQ, "GetHotelAvailRQ", true);

		StringWriter sw = new StringWriter();
		marshaller.marshal(request, sw);

		String requestString = fixRequestNamespaces(sw.toString(), false, true);

		if (log.isDebugEnabled())
			log.debug("searchHotels::requestString = " + requestString);

		String responseString = fixResponseNamespaces(getBodyToString(doCallPost(requestString)), "<GetHotelAvailRS");

		if (log.isDebugEnabled())
			log.debug("searchHotels::responseString = " + responseString);

		if (responseString.contains("USG_INVALID_SECURITY_TOKEN"))
		{
			String tokenNew = getToken();
			properties.saveSiteChannelProperty(sabreCSLAPIProperties.siteInit, SystemProperty.ProductType.TRANSFERS, SabreCSLService.CHANNEL, "token", tokenNew);
			log.debug("searchHotels::token refreshed for site " + sabreCSLAPIProperties.siteInit);
			searchHotels(availabilityRQ);
		}

		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
			Object responseObject = unmarshaller.unmarshal(bin);

			if (log.isDebugEnabled())
				log.debug("searchHotels::responseObject = " + responseObject);

			if (log.isDebugEnabled())
				log.debug("searchHotels::time taken = " + (System.currentTimeMillis() - searchStartTime));
			if (responseObject instanceof GetHotelAvailRS)
			{
				return (GetHotelAvailRS) responseObject;
			}
			else
			{
				log.error("searchHotels::bad responseString : " + responseString + " for " + requestString);
				return null;
			}
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}
	}

	public String doCallPost(String requestData) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("doCallPost::entering");
		HttpService httpService = new SabreHttpService(sabreCSLAPIProperties);
		return httpService.doCallPost("", requestData);
	}

	public String fixRequestNamespaces(String s, Boolean isContent, Boolean isChangeDate)
	{
		if (s == null)
			return "";
		s = s.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
		if (isContent)
		{
			s = s.replaceAll("<GetHotelContentRQ",
					"<GetHotelContentRQ xmlns=\"http://services.sabre.com/hotel/content/v3_0_0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"3.0.0\"");
		}
		else
		{
			s = s.replaceAll("<GetHotelAvailRQ>", "<GetHotelAvailRQ xmlns=\"http://services.sabre.com/hotel/avail/v3_0_0\" version=\"3.0.0\">");
			if (isChangeDate)
			{
				s = s.replaceAll("StayDateTimeRange", "StayDateRange");
			}
			s = s.replaceAll("<TokenCreateRQ>", "<TokenCreateRQ Version=\"1.0.0\" xmlns=\"http://webservices.sabre.com\"/>");
			s = s.replaceAll("<ns3:MessageHeader>", "<eb:MessageHeader eb:version=\"3.0.0\" xmlns:eb=\"http://www.ebxml.org/namespaces/messageHeader\">");
			s = s.replaceAll("ns3:", "eb:");
			s = s.replaceAll("ns1:", "SOAP-ENV:");
			s = s.replaceAll("ns4:", "wsse:");
			s = s.replaceAll("xmlns:ns3=\"http://www.ebxml.org/namespaces/messageHeader\"", "");
			s = s.replaceAll("xmlns:ns4=\"http://schemas.xmlsoap.org/ws/2002/12/secext\"", "");
			s = s.replaceAll("xmlns:ns1=\"http://schemas.xmlsoap.org/soap/envelope/\"", "");
			s = s.replaceAll("<HotelPriceCheckRQ>", "<HotelPriceCheckRQ xmlns=\"http://services.sabre.com/hotel/pricecheck/v3_0_0\" version=\"3.0.0\">");
			s = s.replaceAll("<CreatePassengerNameRecordRQ>", "<CreatePassengerNameRecordRQ xmlns=\"http://services.sabre.com/sp/reservation/v2_4\" version=\"2.4.0\" haltOnHotelBookError=\"true\">");

			s = s.replaceAll("<SOAP-ENV:Envelope",
					"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			s = s.replaceAll("<wsse:Security>", "<wsse:Security xmlns:wsse=\"http://schemas.xmlsoap.org/ws/2002/12/secext\">");
			s = s.replaceAll("<GetHotelDetailsRQ>",
					"<GetHotelDetailsRQ xmlns=\"http://services.sabre.com/hotel/details/v3_0_0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"3.0.0\" xsi:schemaLocation=\"http://services.sabre.com/hotel/details/v3_0_0 GetHotelDetailsRQ_v3.0.0.xsd\">");
		}

		return s;
	}

	public String fixRequestContent(String s)
	{
		if (s == null)
			return "";
		s = s.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");

		return s;
	}

	public String fixResponseNamespaces(String response, String tagName)
	{
		if (response == null)
			return "";
		response = response.replaceAll("ns28:", "");
		response = response.replaceAll("ns27:", "");
		response = response.replaceAll("ns7:", "");
		response = response.replaceAll("ns40:", "");
		response = response.replaceAll("ns30:", "");
		response = response.replaceAll("ns23:", "");
		response = response.replaceAll("sws:", "");
		response = tagName + response.substring(response.indexOf(">"));
		return response;
	}

	public GetHotelContentRS getHotelContent(String hotelCode) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("getHotelContent::entering for hotelCode=" + hotelCode);

		GetHotelContentRQ hotelContentRQ = new GetHotelContentRQ();
		hotelContentRQ.setSearchCriteria(new com.sabre.schema.hotel.content.v4_0_0.SearchCriteria());
		hotelContentRQ.getSearchCriteria().setHotelRefs(new com.sabre.schema.hotel.content.v4_0_0.HotelRefs());
		hotelContentRQ.getSearchCriteria().getHotelRefs().setHotelRef(new HotelRef());
		hotelContentRQ.getSearchCriteria().getHotelRefs().getHotelRef().setHotelCode(hotelCode);
		hotelContentRQ.getSearchCriteria().getHotelRefs().getHotelRef().setCodeContext(com.sabre.schema.hotel.content.v4_0_0.CodeContextType.GLOBAL);

		hotelContentRQ.getSearchCriteria().setDescriptiveInfoRef(new com.sabre.schema.hotel.content.v4_0_0.DescriptiveInfoRef());
		hotelContentRQ.getSearchCriteria().getDescriptiveInfoRef().setAmenities(true);
		hotelContentRQ.getSearchCriteria().getDescriptiveInfoRef().setPropertyInfo(true);
		hotelContentRQ.getSearchCriteria().getDescriptiveInfoRef().setSecurityFeatures(true);
		hotelContentRQ.getSearchCriteria().getDescriptiveInfoRef().setLocationInfo(true);
		hotelContentRQ.getSearchCriteria().getDescriptiveInfoRef().setDescriptions(new com.sabre.schema.hotel.content.v4_0_0.DescriptionsRef());

		com.sabre.schema.hotel.content.v4_0_0.DescriptionsRef.Description description = new com.sabre.schema.hotel.content.v4_0_0.DescriptionsRef.Description();
		description.setType(com.sabre.schema.hotel.content.v4_0_0.DescriptionType.SHORT_DESCRIPTION);
		hotelContentRQ.getSearchCriteria().getDescriptiveInfoRef().getDescriptions().getDescriptions().add(description);

		hotelContentRQ.getSearchCriteria().setMediaRef(new com.sabre.schema.hotel.content.v4_0_0.MediaRef());
		hotelContentRQ.getSearchCriteria().getMediaRef().setMediaTypes(new com.sabre.schema.hotel.content.v4_0_0.MediaTypes());
		hotelContentRQ.getSearchCriteria().getMediaRef().getMediaTypes().setImages(new com.sabre.schema.hotel.content.v4_0_0.Images());
		com.sabre.schema.hotel.content.v4_0_0.Images.Image image = new com.sabre.schema.hotel.content.v4_0_0.Images.Image();
		image.setType(ImageTypes.ORIGINAL);
		hotelContentRQ.getSearchCriteria().getMediaRef().getMediaTypes().getImages().getImages().add(image);
		hotelContentRQ.getSearchCriteria().getMediaRef().getMediaTypes().setPanoramicMedias(new com.sabre.schema.hotel.content.v4_0_0.PanoramicMedias());
		com.sabre.schema.hotel.content.v4_0_0.PanoramicMedias.PanoramicMedia panoramicMedia = new com.sabre.schema.hotel.content.v4_0_0.PanoramicMedias.PanoramicMedia();
		panoramicMedia.setType(com.sabre.schema.hotel.content.v4_0_0.PanoramaMediaTypes.HD_360);
		hotelContentRQ.getSearchCriteria().getMediaRef().getMediaTypes().getPanoramicMedias().getPanoramicMedias().add(panoramicMedia);
		hotelContentRQ.getSearchCriteria().getMediaRef().getMediaTypes().setVideos(new com.sabre.schema.hotel.content.v4_0_0.Videos());
		com.sabre.schema.hotel.content.v4_0_0.Videos.Video video = new com.sabre.schema.hotel.content.v4_0_0.Videos.Video();
		video.setType(com.sabre.schema.hotel.content.v4_0_0.VideoTypes.VIDEO_360);
		hotelContentRQ.getSearchCriteria().getMediaRef().getMediaTypes().getVideos().getVideos().add(video);

		hotelContentRQ.getSearchCriteria().getMediaRef().setCategories(new com.sabre.schema.hotel.content.v4_0_0.Categories());
		com.sabre.schema.hotel.content.v4_0_0.Categories.Category category = new com.sabre.schema.hotel.content.v4_0_0.Categories.Category();
		category.setCode(BigInteger.valueOf(20));
		hotelContentRQ.getSearchCriteria().getMediaRef().getCategories().getCategories().add(category);

		hotelContentRQ.getSearchCriteria().getMediaRef().setAdditionalInfo(new com.sabre.schema.hotel.content.v4_0_0.AdditionalInfoRef());
		com.sabre.schema.hotel.content.v4_0_0.AdditionalInfoRef.Info info = new com.sabre.schema.hotel.content.v4_0_0.AdditionalInfoRef.Info();
		info.setValue(true);
		info.setType(AdditionalInfoType.CAPTION);
		hotelContentRQ.getSearchCriteria().getMediaRef().getAdditionalInfo().getInfos().add(info);
		if (log.isDebugEnabled())
			log.debug("getHotelContent::hotelContentRQ=" + hotelContentRQ);

		Envelope envelope = initEnvelope(hotelContentRQ, "GetHotelContentRQ", true);

		Marshaller marshaller = jaxbContextEnvelope.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		Unmarshaller unmarshaller = jaxbContextEnvelope.createUnmarshaller();

		StringWriter sw = new StringWriter();
		marshaller.marshal(envelope, sw);

		String requestString = sw.toString();

		if (log.isDebugEnabled())
			log.debug("searchHotels::requestString = " + requestString);

		long searchStartTime = System.currentTimeMillis();

		String responseString = fixResponseNamespaces(getBodyToString(doCallPost(fixRequestNamespaces(requestString, true, false))), "<GetHotelContentRS");

		if (log.isDebugEnabled())
			log.debug("searchHotels::responseString = " + responseString);

		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
			Object responseObject = unmarshaller.unmarshal(bin);
			if (log.isDebugEnabled())
				log.debug("searchHotels::responseObject = " + responseObject);

			if (log.isDebugEnabled())
				log.debug("searchHotels::time taken = " + (System.currentTimeMillis() - searchStartTime));
			if (responseObject instanceof GetHotelContentRS)
			{
				return (GetHotelContentRS) responseObject;
			}
			else
			{
				log.error("searchHotels::bad responseString : " + responseString + " for " + requestString);
				return null;
			}
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}
	}

	private String getBodyToString(String responseString)
	{
		StringWriter sw = new StringWriter();
		try
		{
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(responseString)));
			Node companyNode = doc.getElementsByTagName("soap-env:Body").item(0);
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(companyNode.getChildNodes().item(0)), new StreamResult(sw));
		}
		catch (TransformerException | ParserConfigurationException te)
		{
			log.error("nodeToString Transformer Exception");
		}
		catch (SAXException e)
		{
			log.error("parse XML err: " + e);
		}
		catch (IOException e)
		{
			log.error("StringWriter XML err: " + e);
		}
		return sw.toString();
	}

	public GetHotelDetailsRS startSearchHotelsDetail(AvailSearchByHotelIdRQDTO availSearchRQ) throws Exception
	{
		GetHotelDetailsRQ hotelDetailsRQ = makeDetailsRQ(availSearchRQ);
		hotelDetailsRQ.getSearchCriteria().getHotelRefs().getHotelRef().setHotelCode((String) availSearchRQ.getHotelIds().toArray()[0]);

		return searchHotelDetail(hotelDetailsRQ);
	}

	public GetHotelDetailsRS searchHotelDetail(GetHotelDetailsRQ hotelDetailsRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("searchHotels::entering for hotelDetailsRQ=" + hotelDetailsRQ);

		Marshaller marshaller = jaxbContextEnvelope.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		Unmarshaller unmarshaller = jaxbContextEnvelope.createUnmarshaller();

		Envelope envelope = initEnvelope(hotelDetailsRQ, "GetHotelDetailsRQ", true);

		StringWriter sw = new StringWriter();
		marshaller.marshal(envelope, sw);
		String requestString = sw.toString();

		if (log.isDebugEnabled())
			log.debug("searchHotels::requestString = " + requestString);

		long searchStartTime = System.currentTimeMillis();

		String responseString = fixResponseNamespaces(getBodyToString(doCallPost(fixRequestNamespaces(requestString, false, false))), "<GetHotelDetailsRS");
		if (log.isDebugEnabled())
			log.debug("searchHotels::responseString = " + responseString);

		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
			Object responseObject = unmarshaller.unmarshal(bin);
			if (log.isDebugEnabled())
				log.debug("searchHotels::responseObject = " + responseObject);

			if (log.isDebugEnabled())
				log.debug("searchHotels::time taken = " + (System.currentTimeMillis() - searchStartTime));
			if (responseObject instanceof GetHotelDetailsRS)
			{
				return (GetHotelDetailsRS) responseObject;
			}
			else
			{
				log.error("searchHotels::bad responseString : " + responseString + " for " + requestString);
				return null;
			}
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}

	}

	private GetHotelDetailsRQ makeDetailsRQ(AvailSearchRQDTO availSearchRQ) throws DatatypeConfigurationException
	{
		GetHotelDetailsRQ hotelDetailsRQ = new GetHotelDetailsRQ();
		hotelDetailsRQ.setSearchCriteria(new com.sabre.schema.hotel.details.v3_0_0.SearchCriteria());
		hotelDetailsRQ.getSearchCriteria().setHotelRefs(new com.sabre.schema.hotel.details.v3_0_0.HotelRefs());
		hotelDetailsRQ.getSearchCriteria().getHotelRefs().setHotelRef(new com.sabre.schema.hotel.details.v3_0_0.HotelRefs.HotelRef());

		hotelDetailsRQ.getSearchCriteria().setRateInfoRef(new com.sabre.schema.hotel.details.v3_0_0.RateInfoRef());
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().setCurrencyCode("USD");
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().setRefundableOnly(false);
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().setConvertedRateInfoOnly(true);
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().setShowNegotiatedRatesFirst(true);
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().setPrepaidQualifier(com.sabre.schema.hotel.details.v3_0_0.PrepaidQualifierType.INCLUDE_PREPAID);
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().setStayDateTimeRange(new com.sabre.schema.hotel.details.v3_0_0.DateRangeTypeRef());

		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().getStayDateTimeRange().setStartDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(availSearchRQ.getCheckin().toString()));
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().getStayDateTimeRange().setEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(availSearchRQ.getCheckout().toString()));

		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().setRooms(new com.sabre.schema.hotel.details.v3_0_0.RateInfoRef.Rooms());
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().getRooms().setRoomSetTypes(new RoomSetTypes());
		List<RoomSetTypes.RoomSet> roomSets = new ArrayList<>();
		RoomSetTypes.RoomSet rateSource = new RoomSetTypes.RoomSet();
		rateSource.setType("RateSource");
		RoomSetTypes.RoomSet roomType = new RoomSetTypes.RoomSet();
		roomType.setType("RoomType");
		RoomSetTypes.RoomSet bedType = new RoomSetTypes.RoomSet();
		bedType.setType("BedType");
		RoomSetTypes.RoomSet roomView = new RoomSetTypes.RoomSet();
		roomView.setType("RoomView");
		roomSets.add(rateSource);
		roomSets.add(roomView);
		roomSets.add(bedType);
		roomSets.add(roomType);
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().getRooms().getRoomSetTypes().getRoomSets().addAll(roomSets);

		int i = 1;
		List<com.sabre.schema.hotel.details.v3_0_0.Room> roomList = new ArrayList<>();
		com.sabre.schema.hotel.details.v3_0_0.Room room;
		for (TravellerMix travel : availSearchRQ.getTravellers())
		{
			room = new com.sabre.schema.hotel.details.v3_0_0.Room();
			room.setAdults(BigInteger.valueOf(travel.getAdultCount()));
			room.setChildren(BigInteger.valueOf(travel.getChildAges().size()));
			room.setIndex(BigInteger.valueOf(i));
			room.setChildAges(BigInteger.valueOf(travel.getChildAges().size()).toString());
			roomList.add(room);
			i++;
		}
		hotelDetailsRQ.getSearchCriteria().getRateInfoRef().getRooms().getRooms().addAll(roomList);

		hotelDetailsRQ.getSearchCriteria().setHotelContentRef(new HotelContentRef());
		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().setDescriptiveInfoRef(new DescriptiveInfoRef());
		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().getDescriptiveInfoRef().setPropertyInfo(true);
		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().getDescriptiveInfoRef().setLocationInfo(false);
		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().getDescriptiveInfoRef().setAmenities(false);
		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().getDescriptiveInfoRef().setSecurityFeatures(false);

		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().setMediaRef(new MediaRef());
		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().getMediaRef().setMediaTypes(new MediaTypes());
		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().getMediaRef().getMediaTypes().setImages(new Images());
//		hotelDetailsRQ.getSearchCriteria().getHotelContentRef().getMediaRef().getMediaTypes().getImages().
		return hotelDetailsRQ;
	}

	public Envelope initEnvelope(Object body, String action, Boolean isToken) throws Exception
	{
		Envelope envelope = new Envelope();
		Header header = new Header();
		MessageHeader messageHeader = new MessageHeader();
		messageHeader.setFrom(new From());
		messageHeader.setTo(new To());
		PartyId partyIdFrom = new PartyId();
		partyIdFrom.setValue("1900");
		PartyId partyIdTo = new PartyId();
		partyIdTo.setValue("webservices.sabre.com");
		messageHeader.getFrom().getPartyId().add(partyIdFrom);
		messageHeader.getTo().getPartyId().add(partyIdTo);
		messageHeader.setCPAId("TM61");
		messageHeader.setConversationId("1234567890");
		messageHeader.setAction(action);
		messageHeader.setMessageData(new MessageData());
		messageHeader.getMessageData().setMessageId(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
		messageHeader.getMessageData().setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")));
		header.getAny().add(messageHeader);

		Security security = new Security();
		if (isToken)
		{
			security.setBinarySecurityToken(new Security.BinarySecurityToken());
			security.getBinarySecurityToken().setValue(getToken());
		}
		else
		{
			security.setUsernameToken(new Security.UsernameToken());
			security.getUsernameToken().setUsername(sabreCSLAPIProperties.username);
			security.getUsernameToken().setPassword(sabreCSLAPIProperties.password);
			security.getUsernameToken().setOrganization(sabreCSLAPIProperties.pcc);
			security.getUsernameToken().setDomain("DEFAULT");
		}

		header.getAny().add(security);

		Body bodyEn = new Body();
		bodyEn.getAny().add(body);

		envelope.getHeader().add(header);
		envelope.setBody(bodyEn);

		return envelope;
	}

	public String getToken() throws Exception
	{
		Marshaller marshaller = jaxbContextEnvelope.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		Unmarshaller unmarshaller = jaxbContextEnvelope.createUnmarshaller();
		TokenCreateRQ tokenCreateRQ = new TokenCreateRQ();
		tokenCreateRQ.setVersion("1.0.0");
		Envelope envelope = initEnvelope(tokenCreateRQ, "TokenCreateRQ", false);

		StringWriter sw = new StringWriter();
		marshaller.marshal(envelope, sw);

		String requestString = fixRequestNamespaces(sw.toString(), false, true);

		if (log.isDebugEnabled())
			log.debug("searchHotels::requestString = " + requestString);
		String responseString = doCallPost(requestString);

		String bodyString = fixResponseNamespaces(getBodyToString(responseString), "<TokenCreateRS");
		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
			ByteArrayInputStream binBody = new ByteArrayInputStream(bodyString.getBytes());
			Object responseObject = unmarshaller.unmarshal(bin);
			Object bodyObject = unmarshaller.unmarshal(binBody);
			if (log.isDebugEnabled())
				log.debug("searchHotels::responseObject = " + responseObject);

//			if (log.isDebugEnabled())
//				log.debug("searchHotels::time taken = " + (System.currentTimeMillis() - searchStartTime));
			if (bodyObject instanceof TokenCreateRS)
			{
				if (((TokenCreateRS) bodyObject).getSuccess() != null)
				{
					Header header = ((Envelope) ((JAXBElement) responseObject).getValue()).getHeader().get(0);
					for (Object object : header.getAny())
					{
						if (object instanceof Security)
						{
							return ((Security) object).getBinarySecurityToken().getValue();
						}
					}
				}

			}

			else
			{
				log.error("searchHotels::bad responseString : " + responseString + " for " + requestString);
				return null;
			}
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}
		return null;
	}

	public HotelPriceCheckRS checkRates(String rateKeys) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("checkRates::entering for rateKeys=" + rateKeys);

		Marshaller marshaller = jaxbContextEnvelope.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		Unmarshaller unmarshaller = jaxbContextEnvelope.createUnmarshaller();

		HotelPriceCheckRQ hotelPriceCheckRQ = new HotelPriceCheckRQ();
		hotelPriceCheckRQ.setRateInfoRef(new com.sabre.schema.hotel.pricecheck.v4_0_0.RateInfoRef());
		hotelPriceCheckRQ.getRateInfoRef().setRateKey(rateKeys);

		Envelope envelope = initEnvelope(hotelPriceCheckRQ, "HotelPriceCheckRQ", true);

		StringWriter sw = new StringWriter();
		marshaller.marshal(envelope, sw);
		String requestString = sw.toString();

		if (log.isDebugEnabled())
			log.debug("checkRates::requestString = " + requestString);

		long searchStartTime = System.currentTimeMillis();

		String responseString = fixResponseNamespaces(getBodyToString(doCallPost(fixRequestNamespaces(requestString, false, false))), "<HotelPriceCheckRS");
		if (log.isDebugEnabled())
			log.debug("checkRates::responseString = " + responseString);

		try
		{
			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
			Object responseObject = unmarshaller.unmarshal(bin);
			if (log.isDebugEnabled())
				log.debug("checkRates::responseObject = " + responseObject);

			if (log.isDebugEnabled())
				log.debug("checkRates::time taken = " + (System.currentTimeMillis() - searchStartTime));
			if (responseObject instanceof HotelPriceCheckRS)
			{
				return (HotelPriceCheckRS) responseObject;
			}
			else
			{
				log.error("searchHotels::bad responseString : " + responseString + " for " + requestString);
				return null;
			}
		}
		catch (Exception e)
		{
			log.error("searchHotels::caught exception " + e.toString(), e);
			return null;
		}
	}

	public AccommodationBookRS book(AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::entering");
		String productNameForError = null;
		LocalDate productDateForError = null;

		if (bookRQ.getItems().size() == 1)
		{
			// SINGLE ROOM
			AccommodationBookRS accommodationBookRS = bookSingle(bookRQ);
			return accommodationBookRS;
		}

		if (log.isDebugEnabled())
			log.debug("book::multi-room!  Break the calls into individuals!");

//		if (bookRQ.getItems().size() > 1)
//		{
//			// MULTI ROOM
//			AccommodationBookRS accommodationBookRS = bookSingle(bookRQ);
//			return accommodationBookRS;
//		}

		// TODO delete all the rest of this !! This was from when HB multi-room was broken...
		List<Traveller> travellers = new ArrayList<Traveller>();
		travellers.addAll(bookRQ.getTravellers());

		Set<AccommodationBookRQ.AccommodationRequestItem> items = new HashSet<AccommodationBookRQ.AccommodationRequestItem>();
		items.addAll(bookRQ.getItems());

		AccommodationBookRS bookRS = null;

		int passCount = 0;
		for (AccommodationBookRQ.AccommodationRequestItem item : items)
		{
			bookRQ.getTravellers().clear();
			bookRQ.getItems().clear();

			bookRQ.getItems().add(item);
			Set<Integer> travellerIndex = new HashSet<>(item.getTravellerIndex());
			item.getTravellerIndex().clear();
			int addCount = 0;
			for (Integer tIndex : travellerIndex)
			{
				bookRQ.getTravellers().add(travellers.get(tIndex));
				item.getTravellerIndex().add(addCount++);
			}
			try
			{
				if (log.isDebugEnabled())
					log.debug("book::calling multiBook for item " + passCount++ + " : " + item.toString());
				AccommodationBookRS brs = bookSingle(bookRQ);
				if (bookRS == null)
					bookRS = brs;
				else
				{
					bookRS.getItems().addAll(brs.getItems());
					bookRS.getWarnings().addAll(brs.getWarnings());
					bookRS.getErrors().addAll(brs.getErrors());
					bookRS.setBookingReference(bookRS.getBookingReference() + ";" + brs.getBookingReference());
				}
			}
			catch (Exception e)
			{
				if (log.isDebugEnabled())
					log.debug("book::multi-room!  Caught exception " + e.toString(), e);
				if (bookRS == null)
					bookRS = new AccommodationBookRS();
				AccommodationBookRS.ResponseItem brs = new AccommodationBookRS.ResponseItem();
				brs.setChannel(HotelbedsService.CHANNEL);
				brs.setItemStatus(Booking.ItemStatus.FAILED);
				brs.setInternalItemReference(item.getInternalItemReference());
				bookRS.getItems().add(brs);
				bookRS.getErrors().add(new ResponseExtraInformation("500", "System error"));
			}
		}

		if (log.isDebugEnabled())
			log.debug("book::returning multiBook " + bookRS);
		return bookRS;
	}

	protected AccommodationBookRS bookSingle(AccommodationBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("bookSingle::entering with " + bookRQ);
		String productNameForError = null;
		LocalDate productDateForError = null;

		AccommodationBookRS response = new AccommodationBookRS();
		response.setInternalBookingReference(bookRQ.getInternalBookingReference());

		Date today = Functions.normaliseDate(new Date());

		try
		{
			CreatePassengerNameRecordRQ createPassengerNameRecordRQ = new CreatePassengerNameRecordRQ();

			createPassengerNameRecordRQ.setTravelItineraryAddInfo(new CreatePassengerNameRecordRQ.TravelItineraryAddInfo());
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().setCustomerInfo(new CreatePassengerNameRecordRQ.TravelItineraryAddInfo.CustomerInfo());
			CreatePassengerNameRecordRQ.TravelItineraryAddInfo.CustomerInfo.PersonName personName = new CreatePassengerNameRecordRQ.TravelItineraryAddInfo.CustomerInfo.PersonName();
			personName.setGivenName(bookRQ.getBooker().getGivenName());
			personName.setSurname(bookRQ.getBooker().getSurname());
			personName.setNameNumber("1.1");
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getCustomerInfo().getPersonName().add(personName);
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getCustomerInfo().setContactNumbers(new CreatePassengerNameRecordRQ.TravelItineraryAddInfo.CustomerInfo.ContactNumbers());

			CreatePassengerNameRecordRQ.TravelItineraryAddInfo.CustomerInfo.ContactNumbers.ContactNumber contactNumber = new CreatePassengerNameRecordRQ.TravelItineraryAddInfo.CustomerInfo.ContactNumbers.ContactNumber();
			contactNumber.setNameNumber("1.1");
			contactNumber.setPhone(bookRQ.getBooker().getTelephone());
			contactNumber.setPhoneUseType("H");
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getCustomerInfo().getContactNumbers().getContactNumber().add(contactNumber);

			createPassengerNameRecordRQ.getTravelItineraryAddInfo().setAgencyInfo(new CreatePassengerNameRecordRQ.TravelItineraryAddInfo.AgencyInfo());
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().setAddress(new CreatePassengerNameRecordRQ.TravelItineraryAddInfo.AgencyInfo.Address());
			//createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setAddressLine("Umbrella Corp Travel");
			//createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setCityName("Raccoon City");
			//createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setCountryCode("US");
			//createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setPostalCode("44444");
			//createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setStreetNmbr("666 Springfield");
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setAddressLine(sabreCSLAPIProperties.addressLine);
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setCityName(sabreCSLAPIProperties.cityName);
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setCountryCode(sabreCSLAPIProperties.countryCode);
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setPostalCode(sabreCSLAPIProperties.postalCode);
			createPassengerNameRecordRQ.getTravelItineraryAddInfo().getAgencyInfo().getAddress().setStreetNmbr(sabreCSLAPIProperties.streetNmbr);

			BigInteger roomNumber = BigInteger.ZERO;
			for (AccommodationBookRQ.AccommodationRequestItem item : bookRQ.getItems())
			{
				roomNumber = roomNumber.add(BigInteger.ONE);
				if (log.isDebugEnabled())
					log.debug("book::processing roomNumber " + roomNumber);

				createPassengerNameRecordRQ.setHotelBook(new CreatePassengerNameRecordRQ.HotelBook());
				createPassengerNameRecordRQ.getHotelBook().setBookGDSviaCSL(true);
				createPassengerNameRecordRQ.getHotelBook().setBookingInfo(new CreatePassengerNameRecordRQ.HotelBook.BookingInfo());
				createPassengerNameRecordRQ.getHotelBook().getBookingInfo().setBookingKey(item.getBookingCode());
				createPassengerNameRecordRQ.getHotelBook().setRooms(new CreatePassengerNameRecordRQ.HotelBook.Rooms());

				CreatePassengerNameRecordRQ.HotelBook.Rooms.Room room = new CreatePassengerNameRecordRQ.HotelBook.Rooms.Room();
				room.setRoomIndex(roomNumber.intValue());
				CreatePassengerNameRecordRQ.HotelBook.Rooms.Room.Guests guests = new CreatePassengerNameRecordRQ.HotelBook.Rooms.Room.Guests();

				// TravellerMix travellers = new TravellerMix();
				// if (log.isDebugEnabled())
				// log.debug("book::processing room pax travellers=" + travellers.toString());
				// if (log.isDebugEnabled())
				// log.debug("book::processing room pax travellers.adults=" + travellers.getAdultCount());

				if (bookRQ.getItems().size() == 1)
				{
					BigInteger travellerNumber = BigInteger.ZERO;
					boolean leadGuest = true;
					for (Integer travellerIndex : item.getTravellerIndex())
					{
						travellerNumber = travellerNumber.add(BigInteger.ONE);
						CreatePassengerNameRecordRQ.HotelBook.Rooms.Room.Guests.Guest guest = new CreatePassengerNameRecordRQ.HotelBook.Rooms.Room.Guests.Guest();

						Traveller traveller = bookRQ.getTravellers().get(travellerIndex);

						guest.setIndex(travellerNumber.intValue());
						guest.setLastName(traveller.getSurname());
						guest.setFirstName(traveller.getGivenName());
						guest.setContact(new CreatePassengerNameRecordRQ.HotelBook.Rooms.Room.Guests.Guest.Contact());
						guest.getContact().setPhone(traveller.getTelephone());
						guest.setEmail(traveller.getEmail());
						guest.setType(10);
						guest.setLeadGuest(leadGuest);
						guests.getGuest().add(guest);
						leadGuest = false;
					}
				}
				room.setGuests(guests);
				createPassengerNameRecordRQ.getHotelBook().getRooms().getRoom().add(room);

				// Payment Info
				{
					createPassengerNameRecordRQ.getHotelBook().setPaymentInformation(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation());
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().setType("DEPOSIT");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().setFormOfPayment(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment());
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment()
							.setPaymentCard(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard());
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().setPaymentType("CC");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().setCardCode("VI");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().setCardNumber("4444333322221111");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().setExpiryMonth(11);
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard()
							.setExpiryYear(DatatypeFactory.newInstance().newXMLGregorianCalendar(String.valueOf(2023)));
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard()
							.setFullCardHolderName(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.FullCardHolderName());
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getFullCardHolderName().setFirstName("TEST");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getFullCardHolderName().setLastName("BOOKING");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().setCSC("123");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard()
							.setAddress(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address());
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress().setCityName("Krakow");
					CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.StateProvince stateProvince = new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.StateProvince();
					stateProvince.setCode("KR");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress().setStateProvince(stateProvince);
					CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.StateProvinceCodes stateProvinceCodes = new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.StateProvinceCodes();
					CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.StateProvinceCodes.Code code = new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.StateProvinceCodes.Code();
					code.setValue("KR");
					stateProvinceCodes.getCode().add(code);
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress().setStateProvinceCodes(stateProvinceCodes);
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress().setPostCode("30-415");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress()
							.setCountryCodes(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.CountryCodes());

					CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.CountryCodes.Code codeCity = new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.CountryCodes.Code();
					codeCity.setValue("PL");
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress().getCountryCodes().getCode().add(codeCity);
//				createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress().setCityCodes(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Address.CityCodes());
//				createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getAddress().getCityCodes().getCode().add(codeCity);
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard()
							.setPhone(new CreatePassengerNameRecordRQ.HotelBook.PaymentInformation.FormOfPayment.PaymentCard.Phone());
					createPassengerNameRecordRQ.getHotelBook().getPaymentInformation().getFormOfPayment().getPaymentCard().getPhone().setPhoneNumber(bookRQ.getBooker().getTelephone());
				}
			}
			createPassengerNameRecordRQ.setPostProcessing(new CreatePassengerNameRecordRQ.PostProcessing());
			createPassengerNameRecordRQ.getPostProcessing().setEndTransaction(new CreatePassengerNameRecordRQ.PostProcessing.EndTransaction());
			createPassengerNameRecordRQ.getPostProcessing().getEndTransaction().setSource(new CreatePassengerNameRecordRQ.PostProcessing.EndTransaction.Source());
			createPassengerNameRecordRQ.getPostProcessing().getEndTransaction().getSource().setReceivedFrom("API");
			createPassengerNameRecordRQ.getPostProcessing().setRedisplayReservation(new CreatePassengerNameRecordRQ.PostProcessing.RedisplayReservation());
			createPassengerNameRecordRQ.getPostProcessing().getRedisplayReservation().setWaitInterval(1000);

			Marshaller marshaller = jaxbContextEnvelope.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
			Unmarshaller unmarshaller = jaxbContextEnvelope.createUnmarshaller();

			Envelope envelope = initEnvelope(createPassengerNameRecordRQ, "CreatePassengerNameRecordRQ", true);

			StringWriter sw = new StringWriter();
			marshaller.marshal(envelope, sw);
			String requestString = sw.toString();

			if (log.isDebugEnabled())
				log.debug("book::requestString = " + requestString);

			long searchStartTime = System.currentTimeMillis();

			if (sabreCSLAPIProperties.bypassBooking)
			{
				log.warn("book::bypassBooking is true");
				log.warn("book::requestString=" + requestString);
				int randomInt = (int) (Math.random() * 10000.0);
				String randomHotelValue = Integer.toString(randomInt);
				int roomValue = 1;
				response.setBookingReference("SCSL" + randomHotelValue);
				for (AccommodationBookRS.ResponseItem item : response.getItems())
				{
					randomInt = (int) (Math.random() * 10000.0);
					item.setBookingItemReference("SCSL" + randomHotelValue + "_" + roomValue++);
					item.setChannel(HotelbedsService.CHANNEL);
					item.setItemStatus(com.torkirion.eroam.microservice.accommodation.apidomain.Booking.ItemStatus.BOOKED);
				}
				return response;
			}

			String responseString = fixResponseNamespaces(getBodyToString(doCallPost(fixRequestNamespaces(requestString, false, false))), "<CreatePassengerNameRecordRS");
			if (log.isDebugEnabled())
				log.debug("book::responseString = " + responseString);

			try
			{
				ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
				Object responseObject = unmarshaller.unmarshal(bin);
				if (log.isDebugEnabled())
					log.debug("book::responseObject = " + responseObject);

				if (log.isDebugEnabled())
					log.debug("book::time taken = " + (System.currentTimeMillis() - searchStartTime));
				if (responseObject instanceof CreatePassengerNameRecordRS)
				{
					return null;
				}
				else
				{
					log.error("book::bad responseString : " + responseString + " for " + requestString);
					return null;
				}
			}
			catch (Exception e)
			{
				log.error("searchHotels::caught exception " + e.toString(), e);
				return null;
			}

//			if (responseString == null || responseString.length() == 0)
//			{
//				log.error("book::empty responseString : " + responseString + " for " + requestString);
//				String text = "An error has occurred and we are unable to process your request, please do not try again, call us for assistance";
//				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1001");
//				response.getErrors().add(responseExtraInformation);
//				return response;
//			}
//
//			ByteArrayInputStream bin = new ByteArrayInputStream(responseString.getBytes());
//			Object responseObject = unmarshaller.unmarshal(bin);
//			if (log.isDebugEnabled())
//				log.debug("book::responseObject = " + responseObject);
//
//			if (!(responseObject instanceof BookingRS))
//			{
//				log.error("book::bad response object");
//				String text = "A system error has occurred and your booking has not been created.";
//				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1002");
//				response.getErrors().add(responseExtraInformation);
//				return response;
//			}
//
//			BookingRS hbResponse = (BookingRS) responseObject;
//
//			if (hbResponse.getError() != null)
//			{
//				if (hbResponse.getError().getMessage() != null && hbResponse.getError().getMessage().contains("Invalid data. rateKey does not exist or expired"))
//				{
//					String text = "While trying to finalise your booking , this rate has expired.   You may try the room type again, but the rate will only remain valid for 30 minutes.";
//					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1003");
//					response.getErrors().add(responseExtraInformation);
//					return response;
//				}
//				if (hbResponse.getError().getMessage() != null && hbResponse.getError().getMessage().contains("Booking confirmation error: 164"))
//				{
//					// hideHotel(productIDForError);
//					String text = "While trying to finalise your booking, this room type has become unavailable.   Please try a different room type to complete your booking.";
//					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1004");
//					response.getErrors().add(responseExtraInformation);
//					return response;
//				}
//				if (hbResponse.getError().getMessage() != null && hbResponse.getError().getMessage().contains("There are stop sales on the dates indicated"))
//				{
//					// hideHotel(productIDForError, conn);
//					String text = "While trying to finalise your booking, this room type has become unavailable due to a stop sell being applied by the hotel.   Please try a different room type to complete your booking.";
//					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1005");
//					response.getErrors().add(responseExtraInformation);
//					return response;
//				}
//				String text = "A hotelbeds error has occurred and your booking has not been created: '" + hbResponse.getError().getMessage() + "'";
//				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1006");
//				response.getErrors().add(responseExtraInformation);
//				return response;
//			}
//			if (hbResponse.getBooking() == null || hbResponse.getBooking().getStatus() == null || !hbResponse.getBooking().getStatus().equals("CONFIRMED") || hbResponse.getBooking().getHotel() == null
//					|| hbResponse.getBooking().getHotel().getRooms() == null || hbResponse.getBooking().getHotel().getRooms().getRoom() == null)
//			{
//				String text = "A system error has occurred and your booking has not been created. ";
//				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "1007");
//				response.getErrors().add(responseExtraInformation);
//				return response;
//			}
//
//			for (RoomBookingResponse roomBookingResponse : hbResponse.getBooking().getHotel().getRooms().getRoom())
//			{
//				if (!roomBookingResponse.getStatus().equals("CONFIRMED"))
//				{
//					log.error("book::bad response value '" + roomBookingResponse.getStatus() + "' in booking " + roomBookingResponse + " in request " + bookRQ.toString());
//					try
//					{
//						AccommodationCancelRQ cancelRQ = new AccommodationCancelRQ();
//						cancelRQ.setBookingReference(hbResponse.getBooking().getReference());
//						AccommodationCancelRS cancelRS = cancel(cancelRQ);
//						log.error("book::cancelRS : " + cancelRS);
//					}
//					catch (Exception e)
//					{
//						log.error("book::error on hard rollback cancel");
//					}
//					String text = MessageFormat.format("A system error has occurred and your booking for {0} on {1, date, medium} has not been created. ", productNameForError, productDateForError);
//					ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
//					response.getErrors().add(responseExtraInformation);
//					return response;
//				}
//			}
//			// TODO multi-room
//			response.setBookingReference(hbResponse.getBooking().getReference());
//			if ( response.getItems().size() != hbResponse.getBooking().getHotel().getRooms().getRoom().size())
//			{
//				log.error("book::incorrect number of rooms returned " + hbResponse + " in request " + bookRQ.toString());
//				try
//				{
//					AccommodationCancelRQ cancelRQ = new AccommodationCancelRQ();
//					cancelRQ.setBookingReference(hbResponse.getBooking().getReference());
//					AccommodationCancelRS cancelRS = cancel(cancelRQ);
//					log.error("book::cancelRS : " + cancelRS);
//				}
//				catch (Exception e)
//				{
//					log.error("book::error on hard rollback cancel");
//				}
//				String text = MessageFormat.format("A system error has occurred and your booking for {0} on {1, date, medium} has not been created. ", productNameForError, productDateForError);
//				ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
//				response.getErrors().add(responseExtraInformation);
//				return response;
//			}
//			for ( int i = 0; i < response.getItems().size(); i ++)
//			{
//				if (log.isDebugEnabled())
//					log.debug("book::processing response room " + i);
//				AccommodationBookRS.ResponseItem item = response.getItems().get(i);
//				RoomBookingResponse roomBookingResponse = hbResponse.getBooking().getHotel().getRooms().getRoom().get(i);
//				for (RateHotelResponse rateHotelResponse : roomBookingResponse.getRates().getRate())
//				{
//					if (log.isDebugEnabled())
//						log.debug("book::processing roomBookingResponse " + roomBookingResponse.toString() + " rateHotelResponse " + rateHotelResponse.toString());
//					String hbVoucherInfo = MessageFormat.format(HB_VOUCHER_INFO, hbResponse.getBooking().getInvoiceCompany().getName(),
//							hbResponse.getBooking().getInvoiceCompany().getRegistrationNumber(), hbResponse.getBooking().getReference());
//					BigDecimal linePrice = rateHotelResponse.getNet();
//					String confirmationRef = hbResponse.getBooking().getReference();
//					item.setBookingItemReference(confirmationRef);
//					item.setItemStatus(Booking.ItemStatus.BOOKED);
//					item.setChannel(HotelbedsService.CHANNEL);
//					item.setItemRemark(roomBookingResponse.getName() + ": " + hbVoucherInfo);
//					if (log.isDebugEnabled())
//						log.debug("bookHotels::eRoam does not show itemRemarks - copy to main as well");
//					if (!response.getRemarks().contains(hbVoucherInfo))
//						response.getRemarks().add(hbVoucherInfo);
//					if (rateHotelResponse.getRateComments() != null && rateHotelResponse.getRateComments().length() > 0)
//						response.getRemarks().add(roomBookingResponse.getName() + ": " + rateHotelResponse.getRateComments());
//					if (log.isDebugEnabled())
//						log.debug("book::setting itinItem " + item.getBookingItemReference() + " to " + confirmationRef + ", price was " + linePrice);
//					break;
//				}
//			}
//			return response;
		}
		catch (Exception e)
		{
			log.error("book::caught " + e.toString(), e);
			String text = MessageFormat.format("A system error has occurred and your booking for {0} on {1, date, medium} has not been created. ", productNameForError, productDateForError);
			ResponseExtraInformation responseExtraInformation = new ResponseExtraInformation(text, "997");
			response.getErrors().add(responseExtraInformation);
			return response;
		}
	}

	private static JAXBContext jaxbContextEnvelope;

	private static DateTimeFormatter df2YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final String HB_VOUCHER_INFO = "Payable through {0}, acting as agent for the service operating company, details of which can be provided upon request. VAT: {1} Reference: {2}";
}
