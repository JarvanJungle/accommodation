package com.torkirion.eroam.microservice.transport.endpoint.ctw.mapping;

import com.torkirion.eroam.microservice.cache.AirlineCacheUtil;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.datadomain.IataAirportV2;
import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRS;
import com.torkirion.eroam.microservice.transport.apidomain.TransportRS;
import com.torkirion.eroam.microservice.transport.apidomain.TransportRateCheckRS;
import com.torkirion.eroam.microservice.transport.datadomain.IcaoAircraft;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.CTWCommon;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.CTWItineraryShopRS;
import com.torkirion.eroam.microservice.transport.endpoint.ctw.CTWService;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@Slf4j
public class SearchRsMapping {

    private AirlineCacheUtil airlineCacheUtil;

    private static SearchRsMapping instance;

    public static SearchRsMapping getInstance(AirlineCacheUtil airlineCacheUtil) {
        if(instance == null) {
            instance = new SearchRsMapping();
            instance.airlineCacheUtil = airlineCacheUtil;
        }
        return instance;
    }

    public List<TransportRateCheckRS> mapForRateCheck(CTWItineraryShopRS itineraryShopRs) {
        if(itineraryShopRs == null || itineraryShopRs.getDataLibrary() == null) {
            return Collections.EMPTY_LIST;
        }
        Map<Integer, CTWCommon.Passenger> passengerMap = mapPassengers(itineraryShopRs.getDataLibrary().getPassengers());
        Map<Integer, CTWCommon.Flight> flightMap = mapFlights(itineraryShopRs.getDataLibrary().getFlights());
        Map<Integer, CTWCommon.Fare> fares = mapFares(itineraryShopRs.getDataLibrary().getFares());
        Map<Integer, CTWCommon.FreeBaggageAllowance> freeBaggageAllowanceMap = mapFreeBaggageAllowance(itineraryShopRs.getDataLibrary().getFreeBaggageAllowance());
        Map<Integer, CTWCommon.PaidBaggage> paidBaggageMap = mapPaidBaggage(itineraryShopRs.getDataLibrary().getPaidBaggage());
        List<CTWCommon.ItineraryOffer> offers = itineraryShopRs.getItineraryOffers();
        List<TransportRateCheckRS> rateCheckRSs = new ArrayList<>();
        int i = 0;
        for(CTWCommon.ItineraryOffer offer : offers) {
            //offer.getRequestSegmentsMapping()
            Map<Integer, Integer> mapOfFlightAndSegment = mapOfSegmentAndFight(offer.getRequestSegmentsMapping());
            Map<Integer, Integer> mapOfSegmentAndFare = mapSegmentAndFare(offer.getPassengers());
            //Map<Integer, List<TransportRS.CabinPenalty>> mapSegmentAndCabinPenalties = mapSegmentAndCabinPenalties(offer.getPassengers(), passengerMap, fares);
            AvailTransportSearchRS searchRS = new TransportSearchRSBuilder().builder(this.airlineCacheUtil)
                    .id(offer)
                    .commonData(offer, flightMap, mapOfFlightAndSegment, fares, 1)
                    .segments(offer.getRequestSegmentsMapping(), flightMap)
                    .duration(offer, flightMap)
                    .durationTime(offer)
                    .itineraryPricingInfos(offer, freeBaggageAllowanceMap, paidBaggageMap, passengerMap, mapOfFlightAndSegment, fares, flightMap)
                    .build();

            TransportRateCheckRS rateCheckRS = new TransportRateCheckRS(searchRS);

            rateCheckRS.setPackedData(JsonUtil.convertToJson(offer));
            i++;
            if(rateCheckRS != null) {
                rateCheckRSs.add(rateCheckRS);
            }
        }
        log.debug("map::size of searchRSs: {}", rateCheckRSs.size());
        return rateCheckRSs;
    }

    public List<AvailTransportSearchRS> map(CTWItineraryShopRS itineraryShopRs, Integer transportCallType) {
        if(itineraryShopRs == null || itineraryShopRs.getDataLibrary() == null) {
            return Collections.EMPTY_LIST;
        }
        Map<Integer, CTWCommon.Passenger> passengerMap = mapPassengers(itineraryShopRs.getDataLibrary().getPassengers());
        Map<Integer, CTWCommon.Flight> flightMap = mapFlights(itineraryShopRs.getDataLibrary().getFlights());
        Map<Integer, CTWCommon.Fare> fares = mapFares(itineraryShopRs.getDataLibrary().getFares());
        Map<Integer, CTWCommon.FreeBaggageAllowance> freeBaggageAllowanceMap = mapFreeBaggageAllowance(itineraryShopRs.getDataLibrary().getFreeBaggageAllowance());
        Map<Integer, CTWCommon.PaidBaggage> paidBaggageMap = mapPaidBaggage(itineraryShopRs.getDataLibrary().getPaidBaggage());
        List<CTWCommon.ItineraryOffer> offers = itineraryShopRs.getItineraryOffers();
        List<AvailTransportSearchRS> searchRSs = new ArrayList<>();
        int i = 0;
        for(CTWCommon.ItineraryOffer offer : offers) {
            //offer.getRequestSegmentsMapping()
            Map<Integer, Integer> mapOfFlightAndSegment = mapOfSegmentAndFight(offer.getRequestSegmentsMapping());
            //Map<Integer, Integer> mapOfSegmentAndFare = mapSegmentAndFare(offer.getPassengers());
            //Map<Integer, List<TransportRS.CabinPenalty>> mapSegmentAndCabinPenalties = mapSegmentAndCabinPenalties(offer.getPassengers(), passengerMap, fares);
            AvailTransportSearchRS searchRS = new TransportSearchRSBuilder().builder(this.airlineCacheUtil)
                    .id(offer)
                    .commonData(offer, flightMap, mapOfFlightAndSegment, fares, transportCallType)
                    .segments(offer.getRequestSegmentsMapping(), flightMap)
                    .duration(offer, flightMap)
                    .durationTime(offer)
                    .itineraryPricingInfos(offer, freeBaggageAllowanceMap, paidBaggageMap, passengerMap, mapOfFlightAndSegment, fares, flightMap)
                    .build();
            searchRS.setProvider(CTWService.CHANNEL);
            searchRS.setType("flight");
            i++;
            if(searchRS == null) {
                log.debug("item: {} value: is null", i);
            }
            log.debug("item: {} value: {}", i, searchRS.getId());
            if(searchRSs != null) {
                searchRSs.add(searchRS);
            }
        }
        log.debug("map::size of searchRSs: {}", searchRSs.size());
        return searchRSs;
    }


    private Map<Integer, CTWCommon.Flight> mapFlights(List<CTWCommon.Flight> flights) {
        Map<Integer, CTWCommon.Flight> flightMap = new HashMap<>();
        for(CTWCommon.Flight flight : flights) {
            flightMap.put(flight.getId(), flight);
        }
        return flightMap;
    }

    private Map<Integer, CTWCommon.Fare> mapFares(List<CTWCommon.Fare> fares) {
        Map<Integer, CTWCommon.Fare> fareMap = new HashMap<>();
        for(CTWCommon.Fare fare : fares) {
            fareMap.put(fare.getId(), fare);
        }
        return fareMap;
    }

    private Map<Integer, CTWCommon.Passenger> mapPassengers(ArrayList<CTWCommon.Passenger> passengers) {
        Map<Integer, CTWCommon.Passenger> map = new HashMap<>();
        for(CTWCommon.Passenger passenger : passengers) {
            map.put(passenger.getId(), passenger);
        }
        return map;
    }

    //ArrayList<FreeBaggageAllowance> freeBaggageAllowance
    private Map<Integer, CTWCommon.FreeBaggageAllowance> mapFreeBaggageAllowance(ArrayList<CTWCommon.FreeBaggageAllowance> freeBaggageAllowances) {
        Map<Integer, CTWCommon.FreeBaggageAllowance> freeBaggageMap = new HashMap<>();
        for(CTWCommon.FreeBaggageAllowance baggageAllowance : freeBaggageAllowances) {
            freeBaggageMap.put(baggageAllowance.getId(), baggageAllowance);
        }
        return freeBaggageMap;
    }

    private Map<Integer, CTWCommon.PaidBaggage> mapPaidBaggage(ArrayList<CTWCommon.PaidBaggage> paidBaggages) {
        Map<Integer, CTWCommon.PaidBaggage> paidBaggageMap = new HashMap<>();
        for(CTWCommon.PaidBaggage paidBaggage : paidBaggages) {
            paidBaggageMap.put(paidBaggage.getId(), paidBaggage);
        }
        return paidBaggageMap;
    }

    private Map<Integer, Integer> mapOfSegmentAndFight(ArrayList<CTWCommon.RequestSegmentsMapping> requestSegmentsMappings) { //key fight - value: segment
        Map<Integer, Integer> map = new HashMap<>();
        for(int segmentId = 0;  segmentId < requestSegmentsMappings.size(); segmentId++) {
            CTWCommon.RequestSegmentsMapping segmentsMapping = requestSegmentsMappings.get(segmentId);
            ArrayList<Integer> flights = segmentsMapping.getFlights();
            for(Integer fightId : flights) {
                map.put(fightId, segmentId);
            }
        }
        return map;
    }

    private static Map<ServiceTypeVal, List<FreeBaggageAllowanceWithAge>> mapFreeBaggageAllowanceByPackageKey(Map<Integer, CTWCommon.FreeBaggageAllowance> baggageAllowanceMap,
                                                                                                        ArrayList<CTWCommon.Passenger> passengers,
                                                                                                        Map<Integer, CTWCommon.Passenger> passengerMap,
                                                                                                        Map<Integer, Integer> mapOfFlightAndSegment
                                                                                                        ) {
        Map<ServiceTypeVal, List<FreeBaggageAllowanceWithAge>> map = new HashMap<>();
        for(CTWCommon.Passenger passenger : passengers) {
            CTWCommon.DateOfBirth dOB = passengerMap.get(passenger.getPassengerId()).getDateOfBirth();
            ArrayList<CTWCommon.FreeBaggageAllowance> freeBaggageAllowances = passenger.getBaggage().getFreeBaggageAllowance();
            for(CTWCommon.FreeBaggageAllowance baggageAllowance : freeBaggageAllowances) {
                CTWCommon.FreeBaggageAllowance freeBaggageAllowanceDetail = baggageAllowanceMap.get(baggageAllowance.getFreeBaggageAllowanceId());
                ServiceTypeVal serviceType = convertServiceTypeToEnum(freeBaggageAllowanceDetail.getServiceType());
                if(serviceType == null) {
                    continue;
                }
                if(!map.containsKey(serviceType)) {
                    map.put(serviceType, new ArrayList<>());
                }
                //Integer segmentId = 1;
                for (Integer coveredFlight : baggageAllowance.getCoveredFlights()) {
                    Integer segmentId = mapOfFlightAndSegment.get(coveredFlight);
                    map.get(serviceType).add(new FreeBaggageAllowanceWithAge(freeBaggageAllowanceDetail, getAgeType(dOB), segmentId));
                }
            }
        }
        return map;
    }

    private static Map<ServiceTypeVal, List<PaidBaggageWithAge>> mapPaidBaggageWithAge(Map<Integer, CTWCommon.PaidBaggage> paidBaggageMap,
                                                                                         ArrayList<CTWCommon.Passenger> passengers,
                                                                                         Map<Integer, CTWCommon.Passenger> passengerMap,
                                                                                        Map<Integer, Integer> mapOfFlightAndSegment) {
        Map<ServiceTypeVal, List<PaidBaggageWithAge>> map = new HashMap<>();
        for(CTWCommon.Passenger passenger : passengers) {
            CTWCommon.DateOfBirth dOB = passengerMap.get(passenger.getPassengerId()).getDateOfBirth();
            ArrayList<CTWCommon.PaidBaggage> paidBaggageAllowances = passenger.getBaggage().getPaidBaggage();
            if(paidBaggageAllowances == null) {
                continue;
            }
            for(CTWCommon.PaidBaggage paidBaggageAllowance : paidBaggageAllowances) {
                CTWCommon.PaidBaggage paidBaggageDetail = paidBaggageMap.get(paidBaggageAllowance.getPaidBaggageId());
                ServiceTypeVal serviceType = convertServiceTypeToEnum(paidBaggageDetail.getServiceType());
                if(serviceType == null) {
                    continue;
                }
                if(!map.containsKey(serviceType)) {
                    map.put(serviceType, new ArrayList<>());
                }
                //Integer segmentId = 1;
                for (Integer coveredFlight : paidBaggageAllowance.getCoveredFlights()) {
                    Integer segmentId = mapOfFlightAndSegment.get(coveredFlight);
                    map.get(serviceType).add(new PaidBaggageWithAge(paidBaggageDetail, getAgeType(dOB), segmentId));
                }
            }
        }
        return map;
    }

    private Map<Integer, Integer> mapSegmentAndFare(ArrayList<CTWCommon.Passenger> passengers) {
        CTWCommon.Passenger passengerFirst = passengers.get(0);
        ArrayList<CTWCommon.FaresToFlightsMapping> fares = passengerFirst.getItineraryPricing().getFaresToFlightsMapping();
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 0; i < fares.size(); i++) {
            map.put(i, fares.get(i).getFareId());
        }
        return map;
    }

    public Map<Integer, List<TransportRS.CabinPenalty>> mapSegmentAndCabinPenalties(ArrayList<CTWCommon.Passenger> passengers,
                                                                                    Map<Integer, CTWCommon.Passenger> passengerMap,
                                                                                    Map<Integer, CTWCommon.Fare> fares) {
        boolean isSetAdult = false;
        boolean isSetChild = false;
        Map<Integer, List<TransportRS.CabinPenalty>> map = new HashMap<>();
        for(CTWCommon.Passenger passenger : passengers) {
            ArrayList<CTWCommon.FaresToFlightsMapping> faresToFlightsMapping = passenger.getItineraryPricing().getFaresToFlightsMapping();
            for(int segmentId = 0; segmentId < faresToFlightsMapping.size(); segmentId++) {
                if(!map.containsKey(segmentId)) {
                    map.put(segmentId, new ArrayList<>());
                }
                Integer fareId = faresToFlightsMapping.get(segmentId).getFareId();
                CTWCommon.Passenger passengerDetail = passengerMap.get(passenger.getPassengerId());
                CTWCommon.Fare fare = fares.get(fareId);
                map.get(segmentId).addAll(makeCabinPenalty(passengerDetail, fare));
            }
        }
        return map;
    }

    private List<TransportRS.CabinPenalty> makeCabinPenalty(CTWCommon.Passenger passengerDetail, CTWCommon.Fare fare) {
        String type = "adult";
        if(passengerDetail.getPtcs().contains("ADT")) {
            type = "adult";
        }
        if(passengerDetail.getPtcs().contains("CNN")) {
            type = "child";
        }
        if(passengerDetail.getPtcs().contains("INF")) {
            type = "infant";
        }
        List<TransportRS.CabinPenalty> beforeAndAfter = new ArrayList<>();
        CTWCommon.Change change = fare.getPenalty().getChange();
        CTWCommon.BeforeDeparture beforeDeparture = change.getBeforeDeparture();
        TransportRS.CabinPenalty before = new TransportRS.CabinPenalty();
        before.setApplicability("Before");
        before.setAmount(new BigDecimal(beforeDeparture.getPrice().getAmount()));
        before.setCurrencyCode(beforeDeparture.getPrice().getCurrency());
        before.setType(type);
        beforeAndAfter.add(before);

        TransportRS.CabinPenalty after = new TransportRS.CabinPenalty();
        CTWCommon.AfterDeparture afterDeparture = change.getAfterDeparture();
        before.setApplicability("After");
        before.setAmount(new BigDecimal(afterDeparture.getPrice().getAmount()));
        before.setCurrencyCode(afterDeparture.getPrice().getCurrency());
        before.setType(type);
        beforeAndAfter.add(after);

        return beforeAndAfter;
    }

    private static ServiceTypeVal convertServiceTypeToEnum(String serviceTypeText ) {
        if(serviceTypeText.equals("A")) {
            return ServiceTypeVal.A;
        }
        if(serviceTypeText.equals("B")) {
            return ServiceTypeVal.B;
        }
        return null;
    }


    private static class FreeBaggageAllowanceWithAge {
        public CTWCommon.FreeBaggageAllowance freeBaggageAllowance;
        public AgeType ageType;
        public Integer segment;

        public FreeBaggageAllowanceWithAge(CTWCommon.FreeBaggageAllowance freeBaggageAllowance, AgeType ageType, Integer segment) {
            this.freeBaggageAllowance = freeBaggageAllowance;
            this.ageType = ageType;
            this.segment = segment;
        }
    }

    private static class PaidBaggageWithAge {
        public CTWCommon.PaidBaggage paidBaggage;
        public AgeType ageType;
        public Integer segmentId;

        public PaidBaggageWithAge(CTWCommon.PaidBaggage paidBaggage, AgeType ageType, Integer segmentId) {
            this.paidBaggage = paidBaggage;
            this.ageType = ageType;
            this.segmentId = segmentId;
        }
    }

    private static AgeType getAgeType(CTWCommon.DateOfBirth dob) {
        int age = Calendar.getInstance().get(Calendar.YEAR) - dob.getYear();
        if(age > 17) {
            return AgeType.ADULT;
        }
        if(age > 1) {
            return AgeType.CHILD;
        }
        return AgeType.INFANT;
    }

    public static class TransportSearchRSBuilder {

        private AvailTransportSearchRS searchRS;
        private AirlineCacheUtil airlineCacheUtil;
        private boolean isValid = true;
        private int totalDurationOfJourney = 0;

        public boolean isValid() {
            return isValid;
        }

        public void setValid(boolean valid) {
            isValid = valid;
        }

        public TransportSearchRSBuilder builder(AirlineCacheUtil uti) {
            searchRS = new AvailTransportSearchRS();
            //searchRS.setType("flight");
            //searchRS.setProvider("CTW");
            this.airlineCacheUtil = uti;
            return this;
        }

        public TransportSearchRSBuilder id(CTWCommon.ItineraryOffer  offer) {
            searchRS.setId(CTWService.CHANNEL + "_" + offer.getUuid());
            return this;
        }

        public TransportSearchRSBuilder segments(ArrayList<CTWCommon.RequestSegmentsMapping> requestSegmentsMapping,
                                                 Map<Integer, CTWCommon.Flight> flightMap) {
            if(this.isValid() == false) {
                return this;
            }
            List<AvailTransportSearchRS.Segment> segments = new ArrayList<>();
            for(CTWCommon.RequestSegmentsMapping segmentsMapping : requestSegmentsMapping) {
                TransportRS.Segment segment = mapSegment(segmentsMapping, flightMap);
                searchRS.getSegments().add(segment);
            }
            return this;
        }

        private AvailTransportSearchRS.Segment mapSegment(CTWCommon.RequestSegmentsMapping segmentsMapping,
                                                          Map<Integer, CTWCommon.Flight> flightMap) {
            if(segmentsMapping == null) {
                return new AvailTransportSearchRS.Segment();
            }
            AvailTransportSearchRS.Segment segment = new AvailTransportSearchRS.Segment();
            if(segmentsMapping.getFlights() == null) {
                return segment;
            }
            int index = 0;
            for(Integer fightId : segmentsMapping.getFlights()) {
                if(!flightMap.containsKey(fightId)) {
                    log.warn("mapSegment:: ctw can't find the flight Id: {}", fightId);
                    continue;
                }
                segment.getLegs().add(mapFightToLeg(flightMap.get(fightId), index));
                index++;
            }
            return segment;
        }

        private AvailTransportSearchRS.Leg mapFightToLeg(CTWCommon.Flight flightInfo, int index) {
            IataAirportV2 originAirPort = airlineCacheUtil.getByCode(flightInfo.getDepartureAirport());
            IataAirportV2 destAirPort = airlineCacheUtil.getByCode(flightInfo.getArrivalAirport());
            AvailTransportSearchRS.Leg leg = new AvailTransportSearchRS.Leg();
//            private String arrivalAirportLocationCode;
            leg.setArrivalAirportLocationCode(flightInfo.getArrivalAirport());
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//            private LocalDateTime arrivalDateTime;
            leg.setArrivalDateTime(convertDateTimeToLocalDate(flightInfo.getArrivalDateTime()));
//            private String cabinClassCode;
            leg.setCabinClassCode(flightInfo.getCabin());
//            private String cabinClassText;
            leg.setCabinClassText(CabinUtil.getCabinText(flightInfo.getCabin()));
//            private String departureAirportLocationCode;
            leg.setDepartureAirportLocationCode(flightInfo.getDepartureAirport());
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
//            private LocalDateTime departureDateTime;
            leg.setDepartureDateTime(convertDateTimeToLocalDate(flightInfo.getDepartureDateTime()));
//            private Boolean eticket;
            leg.setEticket(true);
//            private String flightNumber;
            leg.setFlightNumber(StringUtils.trimToEmpty(flightInfo.getMarketingFlightNumber()));
//
//            private Integer journeyDuration;
            leg.setJourneyDuration(flightInfo.getDuration());
//            private Integer legIndicator;
            leg.setLegIndicator(index);
//            private String marketingAirlineCode;
            leg.setMarketingAirlineCode(StringUtils.trimToEmpty(flightInfo.getMarketingCarrier()));
//            private String marketingAirlineName;
            Airline airport = airlineCacheUtil.getAirlineByCode(StringUtils.trimToEmpty(flightInfo.getMarketingCarrier()));
            leg.setMarketingAirlineName(airport != null ? airport.getAirline() : "N/A");
            //TODO -BA file = NA
//
//            private String marriageGroup;
            //TODO
//            private String mealCode;
            //TODO
//            private AvailTransportSearchRS.OperatingAirline operatingAirline;
            AvailTransportSearchRS.OperatingAirline ol = new AvailTransportSearchRS.OperatingAirline();
            ol.setCode(StringUtils.trimToEmpty(flightInfo.getOperatingCarrier()));
            ol.setFlightNumber(StringUtils.trimToEmpty(flightInfo.getOperatingFlightNumber()));
            IcaoAircraft icaoAircraft = airlineCacheUtil.getIcaoAircraftByCode(StringUtils.trimToEmpty(flightInfo.getAircraftTypes().get(0).getAircraftTypeCode()));
            if (Objects.nonNull(icaoAircraft))
                ol.setEquipment(icaoAircraft.getFullName());
            Airline olAirport = airlineCacheUtil.getAirlineByCode(StringUtils.trimToEmpty(flightInfo.getOperatingCarrier()));
            if(Objects.nonNull(olAirport))
                ol.setName(olAirport.getAirline());

            leg.setOperatingAirline(ol);
//            private String resBookDesigCode;
            leg.setResBookDesigCode(StringUtils.trimToEmpty(flightInfo.getRbd()));
//            private String resBookDesigText;
//
//            private AvailTransportSearchRS.SeatsRemaining seatsRemaining;
            AvailTransportSearchRS.SeatsRemaining seatsRemaining = new AvailTransportSearchRS.SeatsRemaining();
            seatsRemaining.setNumber(flightInfo.getAvailableSeats());
            leg.setSeatsRemaining(seatsRemaining);
            //TODO belowMinimum
//            private Integer stopQuantity;
            //TODO need to confirm because each leg equals one fight So the value of stopQuantity always is 0
//            private AvailTransportSearchRS.StopQuantityInfo stopQuantityInfo;
            //TODO need to be confirmed

//            private String arrivalData;
            leg.setArrivalData(destAirPort.getAirportName() + ", " + destAirPort.getCityname() + ", " + destAirPort.getCountry());
//            private String departureData;
            leg.setDepartureData(originAirPort.getAirportName() + ", " + originAirPort.getCityname() + ", " + originAirPort.getCountry());
//            private Integer layOverTime;
            //TODO
//            private String layOverTimeText;
            //TODO
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
//            private LocalTime arrivalTime;
            leg.setArrivalTime(convertDateTimeToLocalTime(flightInfo.getArrivalDateTime().getTime()));
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
//            private LocalTime departureTime;
            leg.setDepartureTime(convertDateTimeToLocalTime(flightInfo.getDepartureDateTime().getTime()));
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
//            private LocalDate arrivalDate;
            leg.setArrivalDate(convertDateToLocalDate(flightInfo.getArrivalDateTime().getDate()));
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
//            private LocalDate departureDate;
            leg.setDepartureDate(convertDateToLocalDate(flightInfo.getDepartureDateTime().getDate()));
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//            private LocalDate checkInDate;
            leg.setCheckInDate(leg.getDepartureDate());
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//            private LocalDate checkOutDate;
            leg.setCheckOutDate(leg.getArrivalDate());
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEEE")
//            private LocalDate departDay;
            leg.setDepartDay(convertDateToLocalDate(flightInfo.getArrivalDateTime().getDate()));
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "EEEE")
//            private LocalDate arrivalDay;
            leg.setArrivalDay(convertDateToLocalDate(flightInfo.getDepartureDateTime().getDate()));
//            private AvailTransportSearchRS.CityInfo cityInfo;
            AvailTransportSearchRS.CityInfo cityInfo = new TransportRS.CityInfo();
            cityInfo.setDepartureCity(originAirPort.getCityname());
            cityInfo.setArrivalCity(destAirPort.getCityname());
            leg.setCityInfo(cityInfo);
//
//            private String departureLocation;
            leg.setDepartureLocation(originAirPort.getCityname() + "(" + originAirPort.getIataCode() + ")");

//            private String arrivalLocation;
            leg.setArrivalLocation(destAirPort.getCityname() + "(" + destAirPort.getIataCode() + ")");
//            private String departureTerminal;
            leg.setDepartureTerminal(StringUtils.trimToEmpty(flightInfo.getDepartureTerminal()));
//            private String arrivalTerminal;
            leg.setArrivalTerminal(StringUtils.trimToEmpty(flightInfo.getArrivalTerminal()));
//            private String flightDuration;
            leg.setFlightDuration(String.valueOf(flightInfo.getDuration()));
//
//            private String totalDuration;
            leg.setTotalDuration(leg.getFlightDuration());
//            private String flightStop;
//
//            private String stopDetail;
//
//            private Integer stopCount;
            return leg;
        }


        public TransportSearchRSBuilder commonData(CTWCommon.ItineraryOffer offer,
                                                   Map<Integer, CTWCommon.Flight> flightMap,
                                                   Map<Integer, Integer> mapOfFlightAndSegment,
                                                   Map<Integer, CTWCommon.Fare> fares,
                                                   Integer transportCallType) {
            CTWCommon.Passenger passengerFirst = offer.getPassengers().get(0);
            ArrayList<CTWCommon.FaresToFlightsMapping> faresToFlightsMapping = passengerFirst.getItineraryPricing().getFaresToFlightsMapping();
            Map<Integer, Integer> flightToFareMap = new HashMap<>();
            faresToFlightsMapping.forEach(fareToFlightsMapping -> {
                int fareId = fareToFlightsMapping.getFareId();
                fareToFlightsMapping.getCoveredFlights().forEach(flight -> flightToFareMap.put(flight, fareId));
            });
            for(CTWCommon.RequestSegmentsMapping segmentsMapping : offer.getRequestSegmentsMapping()) {
                Integer fareId = flightToFareMap.get(segmentsMapping.getFlights().get(0));
                CTWCommon.Fare fare = fares.get(fareId);
                TransportRS.CommonData commonData = makeCommonDataItem(segmentsMapping, flightMap, fare, transportCallType, offer);
                if(commonData == null) {
                    this.setValid(false);
                }
                this.searchRS.getCommonDatas().add(commonData);
            }
            return this;
        }

        private AvailTransportSearchRS.CommonData makeCommonDataItem(CTWCommon.RequestSegmentsMapping segmentsMapping,
                                                                     Map<Integer, CTWCommon.Flight> flightMap,
                                                                     CTWCommon.Fare fare,
                                                                     Integer transportCallType,
                                                                     CTWCommon.ItineraryOffer offer) {
            IataAirportV2 originAirline = airlineCacheUtil.getByCode(segmentsMapping.getOrigin());
            IataAirportV2 destAirline = airlineCacheUtil.getByCode(segmentsMapping.getDestination());
            if(originAirline == null || destAirline == null) {
                log.warn("makeCommonDataItem can't find the airLine on the table named airline by IaTa code: {} or {}",  segmentsMapping.getOrigin(),
                        segmentsMapping.getDestination());
                return null;
            }
            ArrayList<Integer> flights = segmentsMapping.getFlights();
            Integer firstFlightId = flights.get(0);
            CTWCommon.Flight firstFlight = flightMap.get(firstFlightId);
            CTWCommon.Flight lastFlight = firstFlight;
            //Calculate layover time
            Long layoverTime = 0L;
            int stopCount = 0;
            if(flights.size() > 1) {
                CTWCommon.Flight prev = lastFlight;
                for(int i = 1; i < flights.size(); i++) {
                    lastFlight = flightMap.get(flights.get(i));
                    layoverTime += ChronoUnit.MINUTES.between(convertDateTimeToLocalDate(prev.getArrivalDateTime()),
                            convertDateTimeToLocalDate(lastFlight.getDepartureDateTime()));
                    stopCount++;
                    prev = lastFlight;
                }
            }

            AvailTransportSearchRS.CommonData commonData = new AvailTransportSearchRS.CommonData();

//            private String fareSourceCode;
            //TODO - BA - ignore
//            private String fareType;
            //TODO - BA - considered
//            private String isRefundable;
            CTWCommon.Penalty penalty = fare.getPenalty();
            if (penalty.getRefund().getBeforeDeparture().isAllowed()
                    || penalty.getRefund().getAfterDeparture().isAllowed()) {
                commonData.setIsRefundable("true");
            } else {
                commonData.setIsRefundable("false");
            }
            //TODO - BA - considered
//            private String sequenceNumber;
            //TODO - BA - ignore
//            private String directionInd; = 1 way
            if(transportCallType == 1) {
                commonData.setDirectionInd("OneWay");
            } else {
                commonData.setDirectionInd("Return");
            }
            //TODO - BA - considered
//            private String isPassportMandatory;
            commonData.setIsPassportMandatory("false");
            //TODO - BA - double check
//            private String ticketType;
            commonData.setTicketType("eTicket");
//            private String validatingAirlineCode;
            commonData.setValidatingAirlineCode("");
//            private String provider;
            commonData.setProvider("CTW");
//            private String transportTypeName;
            commonData.setTransportTypeName("flight");
//            private String carrier;
            //TODO - BA - double check
//            private String CabinClassCode;
            commonData.setCabinClassCode(fare.getCabin());
//            private String cabinClassText;
            commonData.setCabinClassText(CabinUtil.getCabinText(fare.getCabin()));
            //TODO - BA
            //commonData.setCabinClassCode(CabinUtil.getCabinText(firstFlight.getCabin()));
//            private String operatingAirlineCode;
            commonData.setOperatingAirlineCode(firstFlight.getOperatingCarrier());

//            private String OperatingAirlineName;
            Airline operCarAirPort = airlineCacheUtil.getAirlineByCode(StringUtils.trimToEmpty(firstFlight.getOperatingCarrier()));
            if(operCarAirPort != null) {
                commonData.setOperatingAirlineName(operCarAirPort.getAirline());
            }
            //TODO - BA
//            private String flightNumber;
            commonData.setFlightNumber(String.valueOf(firstFlight.getMarketingFlightNumber()));
            //TODO - BA - considered
//            private String MarketingAirlineName;
            String marketingCarrier = firstFlight.getMarketingCarrier();
            Airline airport = airlineCacheUtil.getAirlineByCode(StringUtils.trimToEmpty(marketingCarrier));
            if(airport != null) {
                commonData.setMarketingAirlineName(airport.getAirline());
            }
            //TODO - BA - considered
            /* departure and arrival time */
//            private LocalDateTime departureDateTime;
            commonData.setDepartureDateTime(convertDateTimeToLocalDate(firstFlight.getDepartureDateTime())); //first
//            private LocalDateTime arrivalDateTime;
            commonData.setArrivalDateTime(convertDateTimeToLocalDate(lastFlight.getArrivalDateTime())); //last
//            private LocalDateTime etd;
            commonData.setEtd(commonData.getDepartureDateTime());  //deparuter_date_time
//            private LocalDateTime eta;
            commonData.setEta(commonData.getArrivalDateTime());   //
//            private LocalTime arrivalTime;
            commonData.setArrivalTime(LocalTime.from(commonData.getArrivalDateTime()));
//            private LocalTime departureTime;
            commonData.setDepartureTime(LocalTime.from(commonData.getDepartureDateTime()));

//            private LocalDate arrivalDate;
            commonData.setArrivalDate(LocalDate.from(commonData.getArrivalDateTime()));
//            private LocalDate departureDate;
            commonData.setDepartureDate(LocalDate.from(commonData.getDepartureDateTime()));
            /*--------------------------------------------------------------------------------------------------------*/
//            private Integer layOverTime;
            commonData.setLayOverTime(layoverTime.intValue()); // calculate by minutes
//            private String layOverTimeText;
            commonData.setLayOverTimeText(textDuration(commonData.getLayOverTime()));

//            private Integer flightDuration;
            int flightDuration = 0;
            for(Integer fightId : segmentsMapping.getFlights()) {
                CTWCommon.Flight flight = flightMap.get(fightId);
                flightDuration += flight.getDuration();
            }
            commonData.setFlightDuration(flightDuration);   //totalFlightTime
//            private String durationTime;
            commonData.setDurationTime(textDuration(segmentsMapping.getTotalFlightTime()));
            //TODO - BA - ignore
//            private Boolean isOne;
            commonData.setIsOne((segmentsMapping.getFlights().size() < 2) ? true : false);
            //TODO - when segment has only one fight the values is true, more than 2 flights that is false
//            private Integer stopCount;
            commonData.setStopCount(stopCount);
            // TODO - verify later
//            private String arrivalCity;
            commonData.setArrivalCity(destAirline.getCityname());
            // TODO - verify later
//            private String departureCity;
            commonData.setDepartureCity(originAirline.getCityname());
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//            private LocalDate checkInDate;
            commonData.setCheckInDate(commonData.getDepartureDate());
//            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
//            private LocalDate checkOutDate;
            commonData.setCheckOutDate(commonData.getArrivalDate());
//            private String departureAirportLocationCode;
            commonData.setDepartureAirportLocationCode(firstFlight.getDepartureAirport());
//            private String arrivalAirportLocationCode;
            commonData.setArrivalAirportLocationCode(lastFlight.getArrivalAirport());
//            private BigDecimal totalNetPrice;
            //TODO - BA -doing
//            private BigDecimal totalRetailPrice;
            //TODO - BA -doing
//            private BigDecimal taxAmount;
            BigDecimal amount = new BigDecimal(offer.getTotalYqyr().getAmount()).add(new BigDecimal(offer.getTotalIata().getAmount()));
            commonData.setTaxAmount(amount.divide(new BigDecimal(offer.getRequestSegmentsMapping().size())));
            //TODO - BA -doing
//            private String bookingConditions;
            //TODO - BA - ignore
//            private Integer total_duration; // flight_duration
            commonData.setTotal_duration(segmentsMapping.getTotalFlightTime());
            //TODO BA
            // private String totalDurationText;
            commonData.setTotalDurationText(textDuration(commonData.getTotal_duration()));

            // private String departureText;
            commonData.setDepartureText(commonData.getDepartureCity() +
                    "(" + commonData.getDepartureAirportLocationCode() + ") " + commonData.getDepartureDateTime().format(dateTimeFormatter));

            // private String arrivalText;
            commonData.setArrivalText(commonData.getArrivalCity() +
                    "(" + commonData.getArrivalAirportLocationCode() + ") " + commonData.getArrivalDateTime().format(dateTimeFormatter));

            //private String slug;
            //TODO - BA - considered
            CTWCommon.Date departureDate = firstFlight.getDepartureDateTime().getDate();
            commonData.setSlug(segmentsMapping.getOrigin() + "-" + segmentsMapping.getDestination() + "-" + commonData.getDepartureDateTime().format(yyyyMMdd));
            return commonData;
        }

        private String makeCommonCabinCode() {
            return null;
        }

        public TransportSearchRSBuilder itineraryPricingInfos(CTWCommon.ItineraryOffer offer,
                                                              Map<Integer, CTWCommon.FreeBaggageAllowance> freeBaggageAllowanceMap,
                                                              Map<Integer, CTWCommon.PaidBaggage> paidBaggageMap,
                                                              Map<Integer, CTWCommon.Passenger> passengerMap,
                                                              Map<Integer, Integer> mapOfFlightAndSegment,
                                                              Map<Integer, CTWCommon.Fare> fares,
                                                              Map<Integer, CTWCommon.Flight> flightMap
                                                              ) {
            List<AvailTransportSearchRS.ItineraryPricingInfo> pricingInfos = new ArrayList<>();
            pricingInfos.add(itineraryPricingInfo(offer, freeBaggageAllowanceMap, paidBaggageMap, passengerMap, mapOfFlightAndSegment, fares, flightMap));
            searchRS.setItineraryPricingInfo(pricingInfos);
            return this;
        }

        private AvailTransportSearchRS.ItineraryPricingInfo itineraryPricingInfo(CTWCommon.ItineraryOffer offer,
                                                                                 Map<Integer, CTWCommon.FreeBaggageAllowance> freeBaggageAllowanceMap,
                                                                                 Map<Integer, CTWCommon.PaidBaggage> paidBaggageMap,
                                                                                 Map<Integer, CTWCommon.Passenger> passengerMap,
                                                                                 Map<Integer, Integer> mapOfFlightAndSegment,
                                                                                 Map<Integer, CTWCommon.Fare> fares,
                                                                                 Map<Integer, CTWCommon.Flight> flightMap) {
            AvailTransportSearchRS.ItineraryPricingInfo pricingInfo = new TransportRS.ItineraryPricingInfo();
            pricingInfo.setChannel("CTW");
            pricingInfo.setBaggageDetails(baggageDetails(offer.getPassengers(), freeBaggageAllowanceMap, paidBaggageMap, passengerMap, mapOfFlightAndSegment));
            pricingInfo.setPricing(makePricing(offer, passengerMap));
            pricingInfo.setCabin(makeCabin(offer, offer.getPassengers(), passengerMap, fares, flightMap, mapOfFlightAndSegment));
            //pricingInfo.getExtra().setIsRefundable(false);
            pricingInfo.getExtra().setIsSoldOut(false);
            pricingInfo.getExtra().setIsRefundableTag(false);
            return pricingInfo;
        }

        private TransportRS.Cabin makeCabin(CTWCommon.ItineraryOffer offer,
                                            ArrayList<CTWCommon.Passenger> passengers,
                                            Map<Integer, CTWCommon.Passenger> passengerMap,
                                            Map<Integer, CTWCommon.Fare> fares,
                                            Map<Integer, CTWCommon.Flight> flightMap,
                                            Map<Integer, Integer> mapOfFlightAndSegment
                                            ) {
            TransportRS.Cabin cabin = new TransportRS.Cabin();
            String cabinClassCode = flightMap.get(offer.getRequestSegmentsMapping().get(0).getFlights().get(0)).getFareCabin();
            cabin.setCabinClassCode(cabinClassCode);
            cabin.setCabinClassBucket(makeCabinClassBuckets(passengers, passengerMap, fares, mapOfFlightAndSegment, flightMap));
            List<String> fareBucketList = cabin.getCabinClassBucket().stream().map(cabinClassBucket -> {
                if (cabinClassBucket.getPassenger().equals(TransportRS.CabinClassBucketPax.adult)) {
                    return cabinClassBucket.getFareBucket();
                }
                return null;
            }).distinct().filter(Objects::nonNull).collect(Collectors.toList());
            cabin.setList(String.join(",", fareBucketList));
            cabin.setCabinPenalties(makCabinPenalties(passengers, passengerMap, fares));
            cabin.setCabinClassText(CabinUtil.getCabinText(cabinClassCode));
            return cabin;
        }

        private List<TransportRS.CabinClassBucket> makeCabinClassBuckets(ArrayList<CTWCommon.Passenger> passengers,
                                                                         Map<Integer, CTWCommon.Passenger> passengerMap,
                                                                         Map<Integer, CTWCommon.Fare> fares,
                                                                         Map<Integer, Integer> mapOfFlightAndSegment,
                                                                         Map<Integer, CTWCommon.Flight> flightMap) {
            List<TransportRS.CabinClassBucket> cabinClassBuckets = new ArrayList<>();
            boolean isSetAdult = false;
            boolean isSetChild = false;
            boolean isSetInfant = false;
            for(CTWCommon.Passenger passenger : passengers) {;
                CTWCommon.Passenger passengerDetail = passengerMap.get(passenger.getPassengerId());
                if(!isSetAdult && passengerDetail.getPtcs().contains("ADT")) {
                    cabinClassBuckets.addAll(mapCabinClassBucketWithAge(passenger, mapOfFlightAndSegment, flightMap, fares, TransportRS.CabinClassBucketPax.adult));
                    isSetAdult = true;
                }
                if(!isSetChild && passengerDetail.getPtcs().contains("CHD")) {
                    cabinClassBuckets.addAll(mapCabinClassBucketWithAge(passenger, mapOfFlightAndSegment, flightMap, fares, TransportRS.CabinClassBucketPax.child));
                    isSetChild = true;
                }
                if(!isSetInfant && passengerDetail.getPtcs().contains("INF")) {
                    cabinClassBuckets.addAll(mapCabinClassBucketWithAge(passenger, mapOfFlightAndSegment, flightMap, fares, TransportRS.CabinClassBucketPax.infant));
                    isSetInfant = true;
                }
            }
            return cabinClassBuckets;
        }

        private List<TransportRS.CabinClassBucket> mapCabinClassBucketWithAge(CTWCommon.Passenger passenger,
                                                                              Map<Integer, Integer> mapOfFlightAndSegment,
                                                                              Map<Integer, CTWCommon.Flight> flightMap,
                                                                              Map<Integer, CTWCommon.Fare> fares,
                                                                              TransportRS.CabinClassBucketPax passengerType) {
            List<TransportRS.CabinClassBucket> cabinClassBucketWithAgeList = new ArrayList<>();
            for (CTWCommon.FaresToFlightsMapping faresToFlightsMapping : passenger.getItineraryPricing().getFaresToFlightsMapping()) {
                CTWCommon.Fare fare = fares.get(faresToFlightsMapping.getFareId());
                for (Integer flightId : faresToFlightsMapping.getCoveredFlights()) {
                    CTWCommon.Flight flight = flightMap.get(flightId);
                    int segment = mapOfFlightAndSegment.get(flightId);
                    TransportRS.CabinClassBucket cabinClassBucket = new TransportRS.CabinClassBucket();
                    cabinClassBucket.setSegment(segment);
                    cabinClassBucket.setPassenger(passengerType);
                    cabinClassBucket.setSeatsRemaining(flight.getAvailableSeats());
                    cabinClassBucket.setCabinClass(flight.getFareCabin());
                    cabinClassBucket.setCabinClassText(CabinUtil.getCabinText(flight.getFareCabin()));
                    cabinClassBucket.setFareBucket(StringUtils.trimToEmpty(fare.getFbc()));
                    cabinClassBucket.setMealCode(!CollectionUtils.isEmpty(flight.getMeal()) ? String.valueOf(flight.getMeal().get(0)) : null);
                    cabinClassBucketWithAgeList.add(cabinClassBucket);
                }
            }
            return cabinClassBucketWithAgeList;
        }

        private TransportRS.CabinPenalties makCabinPenalties(ArrayList<CTWCommon.Passenger> passengers, Map<Integer, CTWCommon.Passenger> passengerMap,
                                                             Map<Integer, CTWCommon.Fare> fares) {
            boolean isSetAdult = false;
            boolean isSetChild = false;
            boolean isSetInfant = false;
            TransportRS.CabinPenalties result = new TransportRS.CabinPenalties();
            List<TransportRS.CabinPenalty> cabinPenalties  = new ArrayList<>();
            List<String> cabinPenaltiesTypes = new ArrayList<>();
            for(CTWCommon.Passenger passenger : passengers) {
                CTWCommon.Passenger passengerDetail = passengerMap.get(passenger.getPassengerId());
                int fareId = passenger.getItineraryPricing().getFaresToFlightsMapping().get(0).getFareId();
                if(isSetAdult == false && passengerDetail.getPtcs().contains("ADT")) {
                    cabinPenalties.addAll(makePenaltyBeforeAnAfter("adult", fares.get(fareId)));
                    cabinPenaltiesTypes.add("adult");
                    isSetAdult = true;
                }
                if(isSetChild == false && passengerDetail.getPtcs().contains("CHD")) {
                    cabinPenalties.addAll(makePenaltyBeforeAnAfter("child", fares.get(fareId)));
                    cabinPenaltiesTypes.add("child");
                    isSetChild = true;
                }
                if(isSetInfant == false && passengerDetail.getPtcs().contains("INF")) {
                    cabinPenalties.addAll(makePenaltyBeforeAnAfter("infant", fares.get(fareId)));
                    cabinPenaltiesTypes.add("infant");
                    isSetInfant = true;
                }
            }
            Collections.sort(cabinPenaltiesTypes);
            result.setTypes(String.join(",", cabinPenaltiesTypes));
            result.setPenalties(cabinPenalties);
            return result;
        }

        private List<TransportRS.CabinPenalty> makePenaltyBeforeAnAfter(String type, CTWCommon.Fare fare) {
            List<TransportRS.CabinPenalty> beforeAndAfter = new ArrayList<>();
            CTWCommon.Change change = fare.getPenalty().getChange();
            CTWCommon.BeforeDeparture beforeDeparture = change.getBeforeDeparture();
            CTWCommon.BeforeDeparture refundBeforeDeparture = fare.getPenalty().getRefund().getBeforeDeparture();
            TransportRS.CabinPenalty before = new TransportRS.CabinPenalty();
            if(beforeDeparture.isAllowed() == true) {
                before.setPassenger(type);
                before.setApplicability("Before");
                before.setAmount(new BigDecimal(beforeDeparture.getPrice().getAmount()));
                before.setCurrencyCode(beforeDeparture.getPrice().getCurrency());
                before.setRefundable(refundBeforeDeparture.isAllowed());
                before.setType("Exchange");
                before.setChangeable(true);
            } else {
                before.setChangeable(false);
            }
            beforeAndAfter.add(before);

            TransportRS.CabinPenalty after = new TransportRS.CabinPenalty();
            CTWCommon.AfterDeparture afterDeparture = change.getAfterDeparture();
            CTWCommon.AfterDeparture refundAfterDeparture = fare.getPenalty().getRefund().getAfterDeparture();
            if(afterDeparture.isAllowed() == true) {
                after.setPassenger(type);
                after.setApplicability("After");
                after.setAmount(new BigDecimal(afterDeparture.getPrice().getAmount()));
                after.setCurrencyCode(afterDeparture.getPrice().getCurrency());
                after.setRefundable(refundAfterDeparture.isAllowed());
                after.setType("Exchange");
                after.setChangeable(true);
            } else {
                after.setChangeable(false);
            }
            beforeAndAfter.add(after);

            CTWCommon.NoShow noShow = fare.getPenalty().getNoShow();
            if (noShow.isAllowed()) {
                TransportRS.CabinPenalty noShowPenalty = new TransportRS.CabinPenalty();
                noShowPenalty.setAmount(new BigDecimal(noShow.getPrice().getAmount()));
                noShowPenalty.setCurrencyCode(noShow.getPrice().getCurrency());
                noShowPenalty.setPassenger(type);
                noShowPenalty.setType("NoShow");
                beforeAndAfter.add(noShowPenalty);
            }
            return beforeAndAfter;
        }

        private TransportRS.BaggageDetails baggageDetails(ArrayList<CTWCommon.Passenger> passengers,
                                                          Map<Integer, CTWCommon.FreeBaggageAllowance> freeBaggageAllowanceMap,
                                                          Map<Integer, CTWCommon.PaidBaggage> paidBaggageMap,
                                                          Map<Integer, CTWCommon.Passenger> passengerMap,
                                                          Map<Integer, Integer> mapOfFlightAndSegment) {
            TransportRS.BaggageDetails baggageDetails = new TransportRS.BaggageDetails();
            Map<ServiceTypeVal, List<FreeBaggageAllowanceWithAge>> mapFreeBaggageCustomized = mapFreeBaggageAllowanceByPackageKey(freeBaggageAllowanceMap, passengers, passengerMap, mapOfFlightAndSegment);
            Map<ServiceTypeVal, List<PaidBaggageWithAge>> mapPaidBaggageCustomized = mapPaidBaggageWithAge(paidBaggageMap, passengers, passengerMap, mapOfFlightAndSegment);

            boolean isSetAdult = false;
            boolean isSetChild = false;
            boolean isSetInfant = false;
            List<String> baggageDetailsTypes = new ArrayList<>();
            for(CTWCommon.Passenger passenger : passengers) {
                CTWCommon.Passenger passengerDetail = passengerMap.get(passenger.getPassengerId());
                if(passengerDetail.getPtcs().contains("ADT")) {
                    if(!isSetAdult) {
                        baggageDetailsTypes.add("adult");
                        isSetAdult = true;
                    }
                }
                if(passengerDetail.getPtcs().contains("CHD")) {
                    if(!isSetChild) {
                        baggageDetailsTypes.add("child");
                        isSetChild = true;
                    }
                }
                if(passengerDetail.getPtcs().contains("INF")) {
                    if(!isSetInfant) {
                        baggageDetailsTypes.add("infant");
                        isSetInfant = true;
                    }
                }
            }
            Collections.sort(baggageDetailsTypes);
            baggageDetails.setTypes(String.join(",", baggageDetailsTypes));
            baggageDetails.setCabinBaggage(makeCabinBaggages(mapFreeBaggageCustomized.get(ServiceTypeVal.B), mapPaidBaggageCustomized.get(ServiceTypeVal.B)));
            baggageDetails.setCheckInBaggage(makeCheckinBaggages(mapFreeBaggageCustomized.get(ServiceTypeVal.A), mapPaidBaggageCustomized.get(ServiceTypeVal.A)));
            return baggageDetails;
        }

        private List<TransportRS.CabinBaggage> makeCabinBaggages(List<FreeBaggageAllowanceWithAge> freeBaggageAllowanceWithAgeTypeB,
                                                                 List<PaidBaggageWithAge> paidBaggageWithAgesB) {
            List<TransportRS.CabinBaggage> cabinBaggages = new ArrayList<>();
            if(freeBaggageAllowanceWithAgeTypeB != null) {
                for (FreeBaggageAllowanceWithAge freeBaggageAllowanceWithAge : freeBaggageAllowanceWithAgeTypeB) {
                    TransportRS.CabinBaggage cabinBaggage = new TransportRS.CabinBaggage();
                    cabinBaggage.setTypes(freeBaggageAllowanceWithAge.ageType.name().toLowerCase());
                    cabinBaggage.setBags(makeBags(freeBaggageAllowanceWithAge.freeBaggageAllowance));
                    cabinBaggage.setSegments(List.of(freeBaggageAllowanceWithAge.segment));
                    cabinBaggages.add(cabinBaggage);
                }
            }
            if(paidBaggageWithAgesB != null) {
                for (PaidBaggageWithAge paidBaggageWithAge : paidBaggageWithAgesB) {
                    TransportRS.CabinBaggage cabinBaggage = new TransportRS.CabinBaggage();
                    cabinBaggage.setTypes(paidBaggageWithAge.ageType.name().toLowerCase());
                    cabinBaggage.setBags(makeBags(paidBaggageWithAge.paidBaggage));
                    cabinBaggage.setSegments(List.of(paidBaggageWithAge.segmentId));
                    cabinBaggages.add(cabinBaggage);
                }
            }
            return cabinBaggages;
        }


        private List<TransportRS.CheckInBaggage> makeCheckinBaggages(List<FreeBaggageAllowanceWithAge> freeBaggageAllowanceWithAgeTypeA,
                                                                 List<PaidBaggageWithAge> paidBaggageWithAgesA) {
            List<TransportRS.CheckInBaggage> checkInBaggages = new ArrayList<>();
            if(freeBaggageAllowanceWithAgeTypeA != null) {
                for (FreeBaggageAllowanceWithAge freeBaggageAllowanceWithAge : freeBaggageAllowanceWithAgeTypeA) {
                    TransportRS.CheckInBaggage checkInBaggage = new TransportRS.CheckInBaggage();
                    checkInBaggage.setTypes(freeBaggageAllowanceWithAge.ageType.name().toLowerCase());
                    checkInBaggage.setBags(makeBags(freeBaggageAllowanceWithAge.freeBaggageAllowance));
                    checkInBaggage.setSegments(List.of(freeBaggageAllowanceWithAge.segment));
                    checkInBaggages.add(checkInBaggage);
                }
            }
            if(paidBaggageWithAgesA != null) {
                for (PaidBaggageWithAge paidBaggageWithAge : paidBaggageWithAgesA) {
                    TransportRS.CheckInBaggage checkInBaggage = new TransportRS.CheckInBaggage();
                    checkInBaggage.setTypes(paidBaggageWithAge.ageType.name().toLowerCase());
                    checkInBaggage.setBags(makeBags(paidBaggageWithAge.paidBaggage));
                    checkInBaggage.setSegments(List.of(paidBaggageWithAge.segmentId));
                    checkInBaggages.add(checkInBaggage);
                }
            }
            return checkInBaggages;
        }

        private List<TransportRS.Bag> makeBags(CTWCommon.FreeBaggageAllowance freeBaggageAllowance) {
            List<TransportRS.Bag> bags = new ArrayList<>();
            TransportRS.Bag bag = new TransportRS.Bag();
            if(freeBaggageAllowance.getAllowedPieces() == 0) {
                bag.setDescription("0 bag permitted, see carrier for weight and size restrictions");
            } else {
                bag.setDescription(freeBaggageAllowance.getCommercialName());
            }
            bag.setUnit(freeBaggageAllowance.getAllowedWeightUnit());
            bag.setWeight(freeBaggageAllowance.getAllowedWeight());
            bag.setPieces(freeBaggageAllowance.getAllowedPieces());
            bags.add(bag);
            return bags;
        }

        private List<TransportRS.Bag> makeBags(CTWCommon.PaidBaggage paidBaggage) {
            List<TransportRS.Bag> bags = new ArrayList<>();
            TransportRS.Bag bag = new TransportRS.Bag();
//            if(paidBaggage == null || paidBaggage.get == 0) {
//                bag.setDescription("0 bag permitted, see carrier for weight and size restrictions");
//            } else {
//                bag.setDescription(paidBaggage.getCommercialName());
//            }
//            bag.setDescription(paidBaggage.get);
//            bag.setUnit(paidBaggage.getAllowedWeightUnit());
//            bag.setWeight(paidBaggage.getAllowedWeight());
            bag.setPieces(paidBaggage.getAllowedPieces());
            bags.add(bag);
            return bags;
        }

        private TransportRS.ItineraryPricingInfoPricing makePricing(CTWCommon.ItineraryOffer offer,
                                                                    Map<Integer, CTWCommon.Passenger> passengerMap) {
            TransportRS.ItineraryPricingInfoPricing pricing = new TransportRS.ItineraryPricingInfoPricing();
            pricing.setBrandNames(Collections.EMPTY_LIST);
            TransportRS.ItineraryPrice itineraryPrice = new TransportRS.ItineraryPrice();
            itineraryPrice.setAmount(new BigDecimal(offer.getTotal().getAmount()));

            TransportRS.PriceGroup baseGroup = new TransportRS.PriceGroup();
            baseGroup.setNetPrice(CURRENCY_AMOUNT_0);
            baseGroup.setRetailPrice(CURRENCY_AMOUNT_0);
            TransportRS.CurrencyAmount supplyPriceBaseGroup = new TransportRS.CurrencyAmount();
            supplyPriceBaseGroup.setAmount(new BigDecimal(offer.getTotalBase().getAmount()));
            supplyPriceBaseGroup.setCurrency(offer.getTotalBase().getCurrency());
            baseGroup.setSupplyPrice(supplyPriceBaseGroup);
            itineraryPrice.setBaseGroup(baseGroup);
            itineraryPrice.setDecimalPlaces(2);

            TransportRS.PriceGroup taxGroup = new TransportRS.PriceGroup();
            TransportRS.CurrencyAmount supplyPriceTaxGroup = new TransportRS.CurrencyAmount();
            supplyPriceTaxGroup.setAmount(new BigDecimal(offer.getTotalYqyr().getAmount()).add(new BigDecimal(offer.getTotalIata().getAmount())));
            supplyPriceTaxGroup.setCurrency(offer.getTotalBase().getCurrency());
            taxGroup.setSupplyPrice(supplyPriceTaxGroup);
            taxGroup.setRetailPrice(CURRENCY_AMOUNT_0);
            taxGroup.setNetPrice(CURRENCY_AMOUNT_0);
            itineraryPrice.setTaxGroup(taxGroup);

            TransportRS.PriceGroup totalGroup = new TransportRS.PriceGroup();
            TransportRS.CurrencyAmount supplyPriceTotalGroup = new TransportRS.CurrencyAmount();
            BigDecimal amountSupplyPriceTotalGroup = supplyPriceBaseGroup.getAmount();
            amountSupplyPriceTotalGroup = amountSupplyPriceTotalGroup.add(supplyPriceTaxGroup.getAmount());
            supplyPriceTotalGroup.setAmount(amountSupplyPriceTotalGroup);
            supplyPriceTotalGroup.setCurrency(supplyPriceBaseGroup.getCurrency());
            totalGroup.setSupplyPrice(supplyPriceTotalGroup);
            itineraryPrice.setTotalGroup(totalGroup);

            pricing.setItineraryPrice(itineraryPrice);
            pricing.setPricePerPax(makePricePerPax(offer, passengerMap));
            pricing.setListOfTaxes(makeTaxes(offer));
            return pricing;
        }

        private List<TransportRS.Taxes> makeTaxes(CTWCommon.ItineraryOffer offer) {
            List<TransportRS.Taxes> listOfTaxes = new ArrayList<>();
            CTWCommon.TotalYqyr totalYqyr = offer.getTotalYqyr();
            TransportRS.Taxes yqyrTax = new TransportRS.Taxes();
            yqyrTax.setCode("totalYqyr");
            yqyrTax.setAmount(new BigDecimal(totalYqyr.getAmount()));
            yqyrTax.setCurrency(totalYqyr.getCurrency());
            yqyrTax.setRetailAmount(BigDecimal_0);
            yqyrTax.setDescription("");
            listOfTaxes.add(yqyrTax);

            CTWCommon.TotalIata totalIata = offer.getTotalIata();
            TransportRS.Taxes iataTax = new TransportRS.Taxes();
            iataTax.setCode("totalIata");
            iataTax.setAmount(new BigDecimal(totalIata.getAmount()));
            iataTax.setCurrency(totalIata.getCurrency());
            iataTax.setRetailAmount(BigDecimal_0);
            iataTax.setDescription("");
            listOfTaxes.add(iataTax);

            return listOfTaxes;
        }

        private TransportRS.PricePerPax makePricePerPax(CTWCommon.ItineraryOffer offer, Map<Integer, CTWCommon.Passenger> passengerMap) {
            TransportRS.PricePerPax pricePerPax = new TransportRS.PricePerPax();
            pricePerPax.setAgeGroupings(makeAgeGroupings(offer, passengerMap));
            pricePerPax.setDecimalPlaces(2);
            return pricePerPax;
        }

        private TransportRS.AgeGroupings makeAgeGroupings(CTWCommon.ItineraryOffer offer, Map<Integer, CTWCommon.Passenger> passengerMap) {
            TransportRS.AgeGroupings ageGroupings = new TransportRS.AgeGroupings();
            ArrayList<CTWCommon.Passenger> passengers = offer.getPassengers();
            boolean isSetAdult = false;
            boolean isSetChild = false;
            boolean isSetInfant = false;
            int totalAdult = 0;
            int totalChild = 0;
            int totalInfant = 0;
            for(CTWCommon.Passenger passenger : passengers) {
                CTWCommon.Passenger passengerDetail = passengerMap.get(passenger.getPassengerId());
                if(passengerDetail.getPtcs().contains("ADT")) {
                    if(isSetAdult == false) {
                        ArrayList<CTWCommon.TicketingInfo> ticketingInfos = passenger.getItineraryPricing().getTicketingInfos();
                        ageGroupings.setAdult(makeAdultPriceGroup(ticketingInfos));
                        isSetAdult = true;
                    }
                    totalAdult++;
                }
                ageGroupings.getAdult().setTotalAdult(totalAdult);
                if(passengerDetail.getPtcs().contains("CHD")) {
                    if(isSetChild == false) {
                        ArrayList<CTWCommon.TicketingInfo> ticketingInfos = passenger.getItineraryPricing().getTicketingInfos();
                        ageGroupings.setChild(makeChildPriceGroup(ticketingInfos));
                        isSetChild = true;
                    }
                    totalChild++;
                }
                ageGroupings.getChild().setTotalChild(totalChild);
                if(passengerDetail.getPtcs().contains("INF")) {
                    if(isSetInfant == false) {
                        ArrayList<CTWCommon.TicketingInfo> ticketingInfos = passenger.getItineraryPricing().getTicketingInfos();
                        ageGroupings.setInfant(makeInfantPriceGroup(ticketingInfos));
                        isSetInfant = true;
                    }
                    totalInfant++;
                }
                ageGroupings.getInfant().setTotalInfant(totalInfant);
            }
            return ageGroupings;
        }

        private TransportRS.AdultPriceGroup makeAdultPriceGroup(ArrayList<CTWCommon.TicketingInfo> ticketingInfos) {
            BigDecimal amount = new BigDecimal(0);
            String currency = "AUD";
            for(CTWCommon.TicketingInfo ticketingInfo : ticketingInfos) {
                amount = amount.add(new BigDecimal(ticketingInfo.getTotal().getAmount()));
                currency = ticketingInfo.getTotal().getCurrency();
            }
            TransportRS.AdultPriceGroup adult = new TransportRS.AdultPriceGroup();
            TransportRS.CurrencyAmount currencyAmount = new TransportRS.CurrencyAmount();
            currencyAmount.setCurrency(currency);
            currencyAmount.setAmount(amount);
            adult.setSupplyPrice(currencyAmount);
            return adult;
        }

        private TransportRS.ChildPriceGroup makeChildPriceGroup(ArrayList<CTWCommon.TicketingInfo> ticketingInfos) {
            BigDecimal amount = new BigDecimal(0);
            String currency = "AUD";
            for(CTWCommon.TicketingInfo ticketingInfo : ticketingInfos) {
                amount = amount.add(new BigDecimal(ticketingInfo.getTotal().getAmount()));
                currency = ticketingInfo.getTotal().getCurrency();
            }
            TransportRS.ChildPriceGroup child = new TransportRS.ChildPriceGroup();
            TransportRS.CurrencyAmount currencyAmount = new TransportRS.CurrencyAmount();
            currencyAmount.setCurrency(currency);
            currencyAmount.setAmount(amount);
            child.setSupplyPrice(currencyAmount);
            return child;
        }

        private TransportRS.InfantPriceGroup makeInfantPriceGroup(ArrayList<CTWCommon.TicketingInfo> ticketingInfos) {
            BigDecimal amount = new BigDecimal(0);
            String currency = "AUD";
            for(CTWCommon.TicketingInfo ticketingInfo : ticketingInfos) {
                amount = amount.add(new BigDecimal(ticketingInfo.getTotal().getAmount()));
                currency = ticketingInfo.getTotal().getCurrency();
            }
            TransportRS.InfantPriceGroup infant = new TransportRS.InfantPriceGroup();
            TransportRS.CurrencyAmount currencyAmount = new TransportRS.CurrencyAmount();
            currencyAmount.setCurrency(currency);
            currencyAmount.setAmount(amount);
            infant.setSupplyPrice(currencyAmount);
            return infant;
        }

        public TransportSearchRSBuilder duration(CTWCommon.ItineraryOffer offer, Map<Integer, CTWCommon.Flight> flightMap) {
            TransportRS.Duration duration = new TransportRS.Duration();
            duration.setComboDuration(offer.getTotalFlightTime());
            duration.setComboDurationText(textDuration(duration.getComboDuration()));
            Set<String> marketingAirLineNames = new HashSet<>();
            for(CTWCommon.RequestSegmentsMapping rsSegment : offer.getRequestSegmentsMapping()) {
                for(Integer flightId : rsSegment.getFlights()) {
                    CTWCommon.Flight flight = flightMap.get(flightId);
                    marketingAirLineNames.add(flight.getMarketingCarrier());
                }
            }
            duration.setAirlineTitle(String.join("," , marketingAirLineNames));
            searchRS.setDuration(duration);
            return this;
        }

        public TransportSearchRSBuilder durationTime(CTWCommon.ItineraryOffer offer) {
            searchRS.setDuration_time(String.valueOf(offer.getTotalFlightTime()));
            return this;
        }

        public AvailTransportSearchRS build() {
            if(isValid == false) {
                return null;
            }
            return searchRS;
        }
    }

    //util
    private static LocalTime convertDateTimeToLocalTime(CTWCommon.Time t) {
        return LocalTime.of(t.getHour(), t.getMinutes());
    }

    private static LocalDate convertDateToLocalDate(CTWCommon.Date d) {
        return LocalDate.of(d.getYear(), d.getMonth(), d.getDay());
    }

    private static LocalDateTime convertDateTimeToLocalDate(CTWCommon.DateTime dt) {
        return convertDateTimeToLocalDate(dt.getDate(), dt.getTime());
    }

    private static LocalDateTime convertDateTimeToLocalDate(CTWCommon.Date d, CTWCommon.Time t) {
        return LocalDateTime.of(d.getYear(), d.getMonth(), d.getDay(), t.getHour(), t.getMinutes());
    }

    private static String textDuration(int duration) {
        //8 Hour(s) and 25 Minute(s)
        StringBuilder sb = new StringBuilder();
        sb.append(duration / 60);
        sb.append(" Hour(s) and ");
        sb.append(duration % 60);
        sb.append(" Minute(s)");
        return sb.toString();
    }

    private static class CabinUtil {
        private static Map<String, String> cabinMap  = new HashMap<String, String>() {{
            put("Y", "Economy");
            put("W", "Premium economy");
            put("C", "Business");
            put("J", "Premium business");
            put("F", "First");
            put("R", "Premium first");
        }};

        public static String getCabinText(String code) {
            if(!cabinMap.containsKey(code)) {
                return "";
            }
            return cabinMap.get(code);
        }
    }

    static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy @ HH:mm");
    static final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");



    private static class PackageKey {
        public ServiceTypeVal serviceTYpe;
        public AgeType ageType;

        public PackageKey(String serviceTYpe, AgeType ageType) {
            if(ServiceTypeVal.A.name().equals(serviceTYpe)) {
                this.serviceTYpe = ServiceTypeVal.A;
            } else {
                this.serviceTYpe = ServiceTypeVal.B;
            }
            this.ageType = ageType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PackageKey that = (PackageKey) o;
            return this.serviceTYpe == that.serviceTYpe && this.ageType == that.ageType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(serviceTYpe, ageType);
        }
    }

    static enum AgeType {
        ADULT,
        CHILD,
        INFANT
    }

    enum ServiceTypeVal {
        A, B
    }

    private static final BigDecimal BigDecimal_0 = new BigDecimal(0);
    private static final TransportRS.CurrencyAmount CURRENCY_AMOUNT_0 = new TransportRS.CurrencyAmount(null, BigDecimal_0);
}
