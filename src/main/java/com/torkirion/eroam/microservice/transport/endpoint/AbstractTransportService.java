package com.torkirion.eroam.microservice.transport.endpoint;

import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRS;
import com.torkirion.eroam.microservice.transport.datadomain.IataAirport;
import com.torkirion.eroam.microservice.transport.dto.RouteResult;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi.SaveATrainBookApiSearchRQDTO;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public abstract class AbstractTransportService {

    public static final DateTimeFormatter yyyymmdd = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter yyyymmddHHmm = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    public static final DateFormat dateFormatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateTimeFormatter df2YYYYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter df2YYYYMMDDHHMM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter df2HHmm = DateTimeFormatter.ofPattern("HH:mm");
    public static final SaveATrainBookApiSearchRQDTO.PassengerTypeAttribute SEARCH_PASSENGER_TYPE_ADULT = SaveATrainBookApiSearchRQDTO.PassengerTypeAttribute.
            builder().type("Search::PassengerType::Adult").build();
    public static final SaveATrainBookApiSearchRQDTO.PassengerTypeAttribute SEARCH_PASSENGER_TYPE_CHILD = SaveATrainBookApiSearchRQDTO.PassengerTypeAttribute
            .builder().type("Search::PassengerType::Youth").build();

    public static final Integer SEARCH_PASSENGER_AGE_ADULT = 30;

    public static final SaveATrainBookApiSearchRQDTO.SearchesPassengersAttribute SEARCHES_PASSENGERS_ATTRIBUTE_ADULT = SaveATrainBookApiSearchRQDTO.SearchesPassengersAttribute
            .builder().passengerTypeAttributes(SEARCH_PASSENGER_TYPE_ADULT).age(SEARCH_PASSENGER_AGE_ADULT).build();


    public static final BigDecimal NUMBER_2 = new BigDecimal(2);
    public static final String START_TIME = "00:01";

    protected abstract IataAirport getAirport(String iataCode);
    protected abstract Airline getAirline(String iataCode);
    protected abstract RouteResult.TransportationClass findCheapestClass(RouteResult routeResult);
    protected abstract String getType();
    protected abstract String getProvider();
    protected abstract String makeTransportSearchRSId(List<RouteResult> routeResults);


    protected void combinations(List<List<RouteResult>> collector, List<List<RouteResult>> sets, int n, ArrayList<RouteResult> combo)
    {
        // https://stackoverflow.com/a/49524853
        if (n == sets.size())
        {
            collector.add(new ArrayList<>(combo));
            return;
        }
        for (RouteResult c : sets.get(n))
        {
            combo.add(c);
            combinations(collector, sets, n + 1, combo);
            combo.remove(combo.size() - 1);
        }
    }

    protected AvailTransportSearchRS makeAvailTransportSearchRS(List<RouteResult> routeResults, Integer sequence, TravellerMix travellerMix)
    {
        if (log.isDebugEnabled())
            log.debug("makeAvailTransportSearchRS::enter, routeResults.size=" + routeResults.size());
        if ( routeResults.size() == 0 || routeResults.get(0).getDepartureDateTime() == null || routeResults.get(0).getSegments() == null || routeResults.get(0).getSegments().size() == 0 || routeResults.get(0).getClasses() == null || routeResults.get(0).getClasses().size() == 0)
        {
            if (log.isDebugEnabled())
                log.debug("makeAvailTransportSearchRS::bad route, ignoring");
            return null;
        }
        AvailTransportSearchRS availTransportSearchRS = new AvailTransportSearchRS();
        for ( RouteResult routeResult : routeResults)
        {
            AvailTransportSearchRS.Segment segment = makeSegment(routeResult);
            availTransportSearchRS.getSegments().add(segment);
        }
        String id = makeTransportSearchRSId(routeResults);
        availTransportSearchRS.setId(id);
        availTransportSearchRS.setType(getType());
        availTransportSearchRS.setProvider(getProvider());
        availTransportSearchRS.setCommonData(makeCommonData(sequence, routeResults.get(0), id));
        availTransportSearchRS.setItineraryPricingInfo(makeItineraryPricingInfo(id, routeResults, travellerMix));

        return availTransportSearchRS;
    }

    private AvailTransportSearchRS.Segment makeSegment(RouteResult routeResult)
    {
        if (log.isDebugEnabled())
            log.debug("makeSegment::routeResult=" + routeResult);
        AvailTransportSearchRS.Segment segment = new AvailTransportSearchRS.Segment();
        RouteResult.TransportationClass cheapestClass = findCheapestClass(routeResult);
        for ( RouteResult.Segment routeSegment : routeResult.getSegments())
        {
            if (log.isDebugEnabled())
                log.debug("makeSegment::routeSegment=" + routeSegment);
            IataAirport departureAirportSegment = getAirport(routeSegment.getDepartureAirportLocationCode());
            IataAirport arrivalAirportSegment = getAirport(routeSegment.getArrivalAirportLocationCode());

            AvailTransportSearchRS.Leg leg = new AvailTransportSearchRS.Leg();
            leg.setArrivalAirportLocationCode(routeSegment.getArrivalAirportLocationCode());
            leg.setArrivalData(arrivalAirportSegment.getAirportName() + ", " + arrivalAirportSegment.getIataCode() + " " + arrivalAirportSegment.getCityname());
            leg.setArrivalDate(routeSegment.getArrivalDateTime().toLocalDate());
            leg.setArrivalDateTime(routeSegment.getArrivalDateTime());
            leg.setArrivalDay(routeSegment.getArrivalDateTime().toLocalDate());
            leg.setArrivalLocation(arrivalAirportSegment.getCityname() + "(" + arrivalAirportSegment.getIataCode() + ")");
            leg.setArrivalTerminal(routeSegment.getArrivalTerminal());
            leg.setArrivalTime(routeSegment.getArrivalDateTime().toLocalTime());
            leg.setCabinClassCode(cheapestClass.getClassCode());
            leg.setCabinClassText(cheapestClass.getClassDescription());
            leg.setCheckInDate(null);
            leg.setCheckOutDate(null);
            leg.setCityInfo(new AvailTransportSearchRS.CityInfo());
            leg.getCityInfo().setDepartureCity(departureAirportSegment.getCityname());;
            leg.getCityInfo().setArrivalCity(arrivalAirportSegment.getCityname());;
            leg.setDepartDay(routeSegment.getDepartureDateTime().toLocalDate());
            leg.setDepartureAirportLocationCode(departureAirportSegment.getIataCode());
            leg.setDepartureData(departureAirportSegment.getAirportName() + ", " + departureAirportSegment.getIataCode() + " " + departureAirportSegment.getCityname());
            leg.setDepartureDate(routeSegment.getDepartureDateTime().toLocalDate());
            leg.setDepartureDateTime(routeSegment.getDepartureDateTime());
            leg.setDepartureLocation(departureAirportSegment.getCityname() + "(" + departureAirportSegment.getIataCode() + ")");
            leg.setDepartureTime(routeSegment.getDepartureDateTime().toLocalTime());
            leg.setDepartureTerminal(routeSegment.getDepartureTerminal());
            leg.setEticket(true);
            if (log.isDebugEnabled())
                log.debug("makeSegment::departureAirportSegment=" + departureAirportSegment + ", arrivalAirportSegment=" + arrivalAirportSegment);
            ZoneOffset departureZone = ZoneOffset.ofHours(departureAirportSegment.getTimezone().intValue());
            ZoneOffset arrivalZone = ZoneOffset.ofHours(arrivalAirportSegment.getTimezone().intValue());
            ZonedDateTime departureZoned = routeSegment.getDepartureDateTime().atZone(departureZone);
            ZonedDateTime arrivalZoned = routeSegment.getArrivalDateTime().atZone(arrivalZone);
            int flightDurationMinutes = (int)java.time.temporal.ChronoUnit.MINUTES.between(departureZoned, arrivalZoned);
            if ( flightDurationMinutes > 60 )
                leg.setFlightDuration(flightDurationMinutes / 60 + " hour(s) and " + flightDurationMinutes % 60 + " minute(s)");
            else
                leg.setFlightDuration(flightDurationMinutes + " minutes");
            leg.setFlightNumber(routeSegment.getOperatingAirlineFlightNumber());
            leg.setFlightStop("Stop");
            leg.setJourneyDuration(flightDurationMinutes);
            leg.setLayOverTime(0);
            leg.setLegIndicator(0);
            leg.setMarketingAirlineCode(routeSegment.getMarketingAirlineCode());
            leg.setMarketingAirlineName(getAirline(routeSegment.getMarketingAirlineCode()).getAirline());
            leg.setOperatingAirline(new AvailTransportSearchRS.OperatingAirline());
            leg.getOperatingAirline().setCode(routeSegment.getOperatingAirlineCode());
            leg.getOperatingAirline().setFlightNumber(routeSegment.getOperatingAirlineFlightNumber());
            leg.getOperatingAirline().setName(getAirline(routeSegment.getOperatingAirlineCode()).getAirline());
            leg.setTotalDuration(leg.getFlightDuration());
            leg.setSeatsRemaining(new AvailTransportSearchRS.SeatsRemaining());

            segment.getLegs().add(leg);
        }
        return segment;
    }

    private AvailTransportSearchRS.CommonData makeCommonData(Integer sequence, RouteResult routeResult, String id)
    {
        if (log.isDebugEnabled())
            log.debug("makeCommonData::routeResult=" + routeResult);
        AvailTransportSearchRS.CommonData commonData = new AvailTransportSearchRS.CommonData();
        commonData.setSequenceNumber(sequence.toString());
        commonData.setDirectionInd("OneWay");
        commonData.setTicketType("eTicket");
        commonData.setProvider(getProvider());
        commonData.setTransportTypeName(getType());
        commonData.setSlug(id.toString());
        // for common data, we set this text based on the first segment
        commonData.setOperatingAirlineCode(routeResult.getSegments().get(0).getOperatingAirlineCode());
        commonData.setFlightNumber(routeResult.getFlight());
        commonData.setOperatingAirlineName(getAirline(routeResult.getSegments().get(0).getOperatingAirlineCode()).getAirline());
        commonData.setMarketingAirlineName(getAirline(routeResult.getSegments().get(0).getMarketingAirlineCode()).getAirline());
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("dd MMM yyyy '@' HH:mm");
        IataAirport fromAirportFirstSegment = getAirport(routeResult.getFromIata());
        IataAirport toAirportFirstSegment = getAirport(routeResult.getToIata());
        commonData.setDepartureText(fromAirportFirstSegment.getCityname() + "(" + fromAirportFirstSegment.getIataCode() + ") " + routeResult.getDepartureDateTime().format(df1));
        commonData.setArrivalText(toAirportFirstSegment.getCityname() + "(" + toAirportFirstSegment.getIataCode() + ") " + routeResult.getArrivalDateTime().format(df1));
        commonData.setEtd(routeResult.getDepartureDateTime());
        commonData.setEta(routeResult.getArrivalDateTime());
        RouteResult.TransportationClass cheapestClass = findCheapestClass(routeResult);
        commonData.setCabinClassCode(cheapestClass.getClassCode());
        commonData.setCabinClassText(cheapestClass.getClassDescription());
        commonData.setLayOverTime(0);
        String departureZoneString = makeTZString(fromAirportFirstSegment.getTimezone());
        String arrivalZoneString = makeTZString(toAirportFirstSegment.getTimezone());
        if (log.isDebugEnabled())
            log.debug("makeCommonData::departureZoneString='" + departureZoneString + ", arrivalZoneString='" + arrivalZoneString + "'");
        ZoneId departureZone = ZoneId.of(departureZoneString);
        ZoneId arrivalZone = ZoneId.of(arrivalZoneString);
        ZonedDateTime departureZoned = routeResult.getDepartureDateTime().atZone(departureZone);
        ZonedDateTime arrivalZoned = routeResult.getArrivalDateTime().atZone(arrivalZone);
        int flightDurationMinutes = (int)java.time.temporal.ChronoUnit.MINUTES.between(departureZoned, arrivalZoned);
        commonData.setFlightDuration(flightDurationMinutes);
        commonData.setTotal_duration(flightDurationMinutes);
        commonData.setIsOne(true);
        commonData.setStopCount(routeResult.getSegments().size() - 1);
        if ( flightDurationMinutes > 60 )
            commonData.setTotalDurationText(flightDurationMinutes / 60 + " hour(s) and " + flightDurationMinutes % 60 + " minute(s)");
        else
            commonData.setTotalDurationText(flightDurationMinutes + " minutes");
        commonData.setArrivalTime(routeResult.getArrivalDateTime().toLocalTime());
        commonData.setDepartureTime(routeResult.getDepartureDateTime().toLocalTime());
        commonData.setArrivalDate(routeResult.getArrivalDateTime().toLocalDate());
        commonData.setArrivalDateTime(routeResult.getArrivalDateTime());
        commonData.setDepartureDate(routeResult.getDepartureDateTime().toLocalDate());
        commonData.setDepartureDateTime(routeResult.getDepartureDateTime());
        commonData.setArrivalCity(toAirportFirstSegment.getCityname());
        commonData.setDepartureCity(fromAirportFirstSegment.getCityname());
        commonData.setDepartureAirportLocationCode(fromAirportFirstSegment.getIataCode());
        commonData.setArrivalAirportLocationCode(toAirportFirstSegment.getIataCode());
        commonData.setTotalNetPrice(cheapestClass.getAdultNett());
        commonData.setTotalRetailPrice(cheapestClass.getAdultRrp());
        commonData.setBookingConditions(routeResult.getBookingConditions());
        return commonData;
    }

    private List<AvailTransportSearchRS.ItineraryPricingInfo> makeItineraryPricingInfo(String id, List<RouteResult> routeResults, TravellerMix travellerMix)
    {
        if (log.isDebugEnabled())
            log.debug("makeItineraryPricingInfo::enter for " + id);
        List<AvailTransportSearchRS.ItineraryPricingInfo> pricingInfos = new ArrayList<>();
        Set<String> transportClasses = new HashSet<>();
        StringBuilder sbRef = new StringBuilder();
        for ( RouteResult routeResult : routeResults)
        {
            for (RouteResult.TransportationClass transportClass : routeResult.getClasses() )
            {
                transportClasses.add(transportClass.getReference());
            }
        }
        if (log.isDebugEnabled())
            log.debug("makeItineraryPricingInfo::transportClasses=" + transportClasses);
        // now go through and pull out those routes which have the same reference
        for ( String classReference : transportClasses)
        {
            int transportClassesFound = 0;
            AvailTransportSearchRS.ItineraryPricingInfo pricingInfo = new AvailTransportSearchRS.ItineraryPricingInfo();
            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().setTotalAdult(travellerMix.getAdultCount());
            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().setTotalChild(travellerMix.getChildAges().size());
            pricingInfo.getPricing().setTaxes(new AvailTransportSearchRS.Taxes());
            pricingInfo.getPricing().getTaxes().setCode("TOTALTAX");

            int routeNumber = 0;
            for ( RouteResult routeResult : routeResults)
            {
                for (RouteResult.TransportationClass transportClass : routeResult.getClasses() )
                {
                    if ( transportClass.getReference().equals(classReference))
                    {
                        transportClassesFound++;
                        pricingInfo.setChannel(getProvider());
                        if ( pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().getNetPrice() == null)
                        {
                            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), BigDecimal.ZERO));
                        }
                        pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().getNetPrice().getAmount().add(transportClass.getAdultNett())));
                        pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().getSupplyPrice().getAmount().add(transportClass.getAdultNett())));
                        pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getAdult().getRetailPrice().getAmount().add(transportClass.getAdultRrp())));
                        if ( pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().getNetPrice() == null)
                        {
                            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), BigDecimal.ZERO));
                        }
                        pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().getNetPrice().getAmount().add(transportClass.getChildNett())));
                        pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().getSupplyPrice().getAmount().add(transportClass.getChildNett())));
                        pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), pricingInfo.getPricing().getPricePerPax().getAgeGroupings().getChild().getRetailPrice().getAmount().add(transportClass.getChildRrp())));
                        pricingInfo.getPricing().getPricePerPax().setDecimalPlaces(2);
                        if ( pricingInfo.getPricing().getItineraryPrice().getAmount() == null )
                        {
                            pricingInfo.getPricing().getItineraryPrice().setAmount(BigDecimal.ZERO);
                            pricingInfo.getPricing().getItineraryPrice().getBaseGroup().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getBaseGroup().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getBaseGroup().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getTotalGroup().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getTotalGroup().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getTotalGroup().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getTaxGroup().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getTaxGroup().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().getTaxGroup().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), BigDecimal.ZERO));
                            pricingInfo.getPricing().getItineraryPrice().setDecimalPlaces(2);
                        }
                        BigDecimal adultNettChargeForRoute = transportClass.getAdultNett().multiply(BigDecimal.valueOf(travellerMix.getAdultCount()));
                        BigDecimal adultRrpChargeForRoute = transportClass.getAdultRrp().multiply(BigDecimal.valueOf(travellerMix.getAdultCount()));
                        BigDecimal childNettChargeForRoute = transportClass.getChildNett().multiply(BigDecimal.valueOf(travellerMix.getChildAges().size()));
                        BigDecimal childRrpChargeForRoute = transportClass.getChildRrp().multiply(BigDecimal.valueOf(travellerMix.getChildAges().size()));
                        pricingInfo.getPricing().getItineraryPrice().setAmount(pricingInfo.getPricing().getItineraryPrice().getAmount().add(adultRrpChargeForRoute).add(childRrpChargeForRoute));
                        pricingInfo.getPricing().getItineraryPrice().getBaseGroup().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getItineraryPrice().getBaseGroup().getSupplyPrice().getAmount().add(adultNettChargeForRoute).add(childNettChargeForRoute)));
                        pricingInfo.getPricing().getItineraryPrice().getBaseGroup().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getItineraryPrice().getBaseGroup().getNetPrice().getAmount().add(adultNettChargeForRoute).add(childNettChargeForRoute)));
                        pricingInfo.getPricing().getItineraryPrice().getBaseGroup().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), pricingInfo.getPricing().getItineraryPrice().getBaseGroup().getRetailPrice().getAmount().add(adultRrpChargeForRoute).add(childRrpChargeForRoute)));
                        pricingInfo.getPricing().getItineraryPrice().getTotalGroup().setSupplyPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getItineraryPrice().getTotalGroup().getSupplyPrice().getAmount().add(adultNettChargeForRoute).add(childNettChargeForRoute)));
                        pricingInfo.getPricing().getItineraryPrice().getTotalGroup().setNetPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getCurrency(), pricingInfo.getPricing().getItineraryPrice().getTotalGroup().getNetPrice().getAmount().add(adultNettChargeForRoute).add(childNettChargeForRoute)));
                        pricingInfo.getPricing().getItineraryPrice().getTotalGroup().setRetailPrice(new AvailTransportSearchRS.CurrencyAmount(routeResult.getRrpCurrency(), pricingInfo.getPricing().getItineraryPrice().getTotalGroup().getRetailPrice().getAmount().add(adultRrpChargeForRoute).add(childRrpChargeForRoute)));
                        pricingInfo.getPricing().getTaxes().setCurrency(routeResult.getRrpCurrency());

                        pricingInfo.getCabin().setCabinClassCode(transportClass.getClassCode());
                        pricingInfo.getCabin().setCabinClassText(transportClass.getClassDescription());
                        // TODO pricingInfo.getCabin().setCabinClassBucket("");
                        pricingInfo.getCabin().getCabinBrandName().add(transportClass.getReference());
                        if (log.isDebugEnabled())
                            log.debug("makeItineraryPricingInfo::set cabin to " + pricingInfo.getCabin());

                        // do we NEED to do brand names??

                        for ( RouteResult.Segment segment : routeResult.getSegments())
                        {
                            if (segment.getPassportRequired() != null && segment.getPassportRequired())
                                pricingInfo.getData().setIsPassportMandatory(true);
                        }
                        if("".equals(pricingInfo.getBookingCode())) {
                            pricingInfo.setBookingCode(transportClass.getBookingCode());
                        } else {
                            pricingInfo.setBookingCode(pricingInfo.getBookingCode() + "-" + transportClass.getBookingCode());
                        }
                    }
                }
                routeNumber++;
            }
            if ( transportClassesFound == routeResults.size() )
            {
                pricingInfos.add(pricingInfo);
            }
            else
            {
                if (log.isDebugEnabled())
                    log.debug("makeItineraryPricingInfo::transportClassesFound=" + transportClassesFound + " != route segments " + routeResults.size());
            }
        }
        if (log.isDebugEnabled())
            log.debug("makeItineraryPricingInfo::id " + id + " has " + pricingInfos.size() + " pricingInfos");
        return pricingInfos;
    }

    private String makeTZString(BigDecimal tz)
    {
        return (tz.compareTo(BigDecimal.ZERO) > 0  ? "+" : "-") + (tz.abs().compareTo(BigDecimal.TEN) < 0 ? "0" : "") + tz.abs().toString().replace(".",  ":");
    }
}
