package com.torkirion.eroam.microservice.transport.endpoint;

import com.torkirion.eroam.microservice.transport.apidomain.AvailTransportSearchRS;
import com.torkirion.eroam.microservice.transport.datadomain.SaveATrainVendorStation;
import com.torkirion.eroam.microservice.transport.dto.RouteResult;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi.SaveATrainBookApiSubRouteRSDTO;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi.SaveATrainStartChooseRSDTO;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi.SearchApiSearchRSDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SaveATrainMapper {
    public static List<RouteResult> makeRouteResults(List<SearchApiSearchRSDTO.Route> routes,
                                                                             Map<String, SaveATrainVendorStation> stationMap
                                                                                  ) {
        if(log.isDebugEnabled()) {
            log.debug("makeRouteResults::start");
        }
        List<RouteResult> routeResults = routes.stream().map(r -> makeRouteResult(r, stationMap)).collect(Collectors.toList());
        return routeResults;
    }

    private static RouteResult makeRouteResult(SearchApiSearchRSDTO.Route route, Map<String, SaveATrainVendorStation> stationMap) {
        RouteResult routeResult = new RouteResult();
        routeResult.setCurrency(CURRENCY_DEFAULT);
        routeResult.setRrpCurrency(CURRENCY_DEFAULT);
        routeResult.setFlight(route.getTrainNumbers());
        routeResult.setDepartureDateTime(LocalDateTime.parse(route.getDepartureDate() + " " + route.getDepartureTime(), SaveATrainService.df2YYYYMMDDHHMM));
        routeResult.setArrivalDateTime(LocalDateTime.parse(route.getArrivalDate() + " " + route.getArrivalTime(), SaveATrainService.df2YYYYMMDDHHMM));
        routeResult.setSearchIataFrom(route.getOriginStation());
        routeResult.setSearchIataTo(route.getDestinStation());
        routeResult.setFromIata(route.getOriginStation());
        routeResult.setToIata(route.getDestinStation());
        routeResult.setSegments(makeRouteResultSegments(route, stationMap));
        routeResult.setClasses(makeListRouteResultTransportationClasses(route));
        return routeResult;
    }

    private static List<RouteResult.TransportationClass> makeListRouteResultTransportationClasses(SearchApiSearchRSDTO.Route route) {
        BigDecimal firstClassPrice = route.getPrice().getFirstClass();
        BigDecimal secondClassPrice = route.getPrice().getSecondClass();
        List<RouteResult.TransportationClass> classes = new ArrayList<>();
        if(firstClassPrice != BigDecimal.ZERO) {
            RouteResult.TransportationClass transportationClass = new RouteResult.TransportationClass();
            transportationClass.setClassCode(CLASS_CODE_FIRST_CLASS);
            transportationClass.setReference(CLASS_CODE_FIRST_CLASS);
            transportationClass.setAdultRrp(firstClassPrice);
            transportationClass.setAdultNett(firstClassPrice);
            transportationClass.setChildRrp(firstClassPrice);
            transportationClass.setChildNett(firstClassPrice);
            classes.add(transportationClass);
        }
        if(secondClassPrice != BigDecimal.ZERO) {
            RouteResult.TransportationClass transportationClass = new RouteResult.TransportationClass();
            transportationClass.setClassCode(CLASS_CODE_SECOND_CLASS);
            transportationClass.setReference(CLASS_CODE_SECOND_CLASS);
            transportationClass.setAdultRrp(secondClassPrice);
            transportationClass.setAdultNett(secondClassPrice);
            transportationClass.setChildRrp(secondClassPrice);
            transportationClass.setChildNett(secondClassPrice);
            classes.add(transportationClass);
        }
        return classes;
    }

    private static List<RouteResult.Segment> makeRouteResultSegments(SearchApiSearchRSDTO.Route route, Map<String, SaveATrainVendorStation> stationMap) {
        List<RouteResult.Segment> segments = new ArrayList<>();
        int numberOfSegment = getNumberOfSegment(route.getChanges());
        List<String> stationConnections = getConnections(route.getConnections(), numberOfSegment);
        List<String> arrivalTimeConnections = getConnections(route.getArrivalConnections(), numberOfSegment);
        List<String> departureTimeConnections = getConnections(route.getDepartureConnections(), numberOfSegment);

        List<String> departureStations = listDepartureStation(stationConnections, route.getOriginStation());
        List<String> arrivalStations = listArrivalStation(stationConnections, route.getDestinStation());
        List<LocalDateTime> departureDateTimes = listDepartureDateTime(departureTimeConnections, route.getDepartureDate(), route.getDepartureTime(), route.getArrivalDate());
        List<LocalDateTime> arrivalDateTimes = listArrivalDateTime(arrivalTimeConnections, route.getDepartureDate(), route.getArrivalDate(), route.getArrivalTime());
        List<String> trainNumbers = getTrainNumbers(route.getTrainNumbers());
        if(log.isDebugEnabled()) {
            log.debug("makeRouteResultSegments:: " +
                    "\n(departureStations:{}, " +
                    "\n arrivalStations: {}," +
                    "\n departureDateTimes: {}" +
                    "\n arrivalDateTimes: {}" +
                    "\n trainNumbers: {}", departureStations, arrivalStations, departureDateTimes, arrivalDateTimes, trainNumbers);
        }
        for(int i = 0; i < numberOfSegment; i++) {
            RouteResult.Segment segment = new RouteResult.Segment();
            segment.setSegmentNumber(i + 1);
            segment.setDepartureAirportLocationCode(departureStations.get(i));

            /*--start process if walk---*/
            LocalDateTime departureDateTime = departureDateTimes.get(i);
            if(LOCAL_DATE_TIME_WALK.equals(departureDateTime)) {
                segment.setDepartureDateTime(arrivalDateTimes.get(i - 1));
            } else {
                segment.setDepartureDateTime(departureDateTime);
            }
            segment.setArrivalAirportLocationCode(arrivalStations.get(i));
            /*-------------------------*/
            LocalDateTime arrivalDateTime = arrivalDateTimes.get(i);
            if(LOCAL_DATE_TIME_WALK.equals(arrivalDateTime)) {
                segment.setArrivalDateTime(departureDateTimes.get(i + 1));
            } else {
                segment.setArrivalDateTime(arrivalDateTime);
            }
            /*--end process if walk---*/

            String trainNumber = trainNumbers.get(i);
            if(TRAIN_NUMBER_WALK.equals(trainNumber)) {
                segment.setOperatingAirlineCode(TRAIN_NUMBER_WALK);
            } else {
                Duration duration = Duration.between(segment.getDepartureDateTime(), segment.getArrivalDateTime());
                segment.setFlightDurationMinutes((int)duration.toMinutes());
                segment.setOperatingAirlineCode("Train");
                segment.setOperatingAirlineFlightNumber(trainNumber);
            }
            segments.add(segment);
        }
        return segments;
    }

    private static List<String> listDepartureStation(List<String> stationConnections, String originStation) {
        List<String> departureStations = new ArrayList<>();
        departureStations.add(originStation);
        if(CollectionUtils.isEmpty(stationConnections)) {
            return departureStations;
        }
        departureStations.addAll(stationConnections);
        return departureStations;
    }

    private static List<String> listArrivalStation(List<String> stationConnections, String destinationStation) {
        List<String> arrivalStations = new ArrayList<>();
        if(!CollectionUtils.isEmpty(stationConnections)) {
            arrivalStations.addAll(stationConnections);
        }
        arrivalStations.add(destinationStation);
        return arrivalStations;
    }

    private static List<LocalDateTime> listDepartureDateTime(List<String> departureTimeConnections, String departureDate, String departureTime, String arrivalDate) {
        List<LocalDateTime> departureDateTimes = new ArrayList<>();
        int legNumber = 0;
        departureDateTimes.add(legNumber, getLocalDateTime(departureDate, departureTime));
        if(CollectionUtils.isEmpty(departureTimeConnections)) {
            return departureDateTimes;
        }
        for(String departureTimeConnection : departureTimeConnections) {
            if(!WORK_TO_STATION.equals(departureTimeConnection)) {
                LocalDateTime nearestDepartureDateTime = departureDateTimes.get(legNumber);
                LocalTime nearestDepartureTime = nearestDepartureDateTime.toLocalTime();
                LocalTime departureConnectionLocalTime = LocalTime.parse(departureTimeConnection, SaveATrainService.df2HHmm);
                if(departureConnectionLocalTime.isBefore(nearestDepartureTime)) {
                    LocalDateTime departureDateTimeConnection = getLocalDateTime(arrivalDate, departureTimeConnection);
                    departureDateTimes.add(++legNumber, departureDateTimeConnection);
                    continue;
                }
                LocalDateTime departureDateTimeConnection = getLocalDateTime(departureDate, departureTimeConnection);
                departureDateTimes.add(++legNumber, departureDateTimeConnection);
                continue;
            }
            departureDateTimes.add(++legNumber, LOCAL_DATE_TIME_WALK);
        }
        return departureDateTimes;
    }

    private static List<LocalDateTime> listArrivalDateTime(List<String> arrivalTimeConnections, String departureDate, String arrivalDate, String finalArrivalTime) {
        List<LocalDateTime> arrivalDateTimes = new ArrayList<>();
        int legNumber = 0;
        LocalTime finalArrivalTimeLT = LocalTime.parse(finalArrivalTime, SaveATrainService.df2HHmm);
        for(String arrivalTimeConnection : arrivalTimeConnections) {
            if(!WORK_TO_STATION.equals(arrivalTimeConnection)) {
                LocalTime arrivalTimeConnectionLT = LocalTime.parse(arrivalTimeConnection, SaveATrainService.df2HHmm);
                if (arrivalTimeConnectionLT.isAfter(finalArrivalTimeLT)) {
                    arrivalDateTimes.add(legNumber++, getLocalDateTime(departureDate, arrivalTimeConnection));  // if arrivalTimeConnectionLT > finalArrivalTimeLT use departureDate
                    continue;
                }
                arrivalDateTimes.add(legNumber++, getLocalDateTime(arrivalDate, arrivalTimeConnection));
                continue;
            }
            arrivalDateTimes.add(legNumber++, LOCAL_DATE_TIME_WALK);
        }
        arrivalDateTimes.add(legNumber, getLocalDateTime(arrivalDate, finalArrivalTime));
        return arrivalDateTimes;
    }

    private static int getNumberOfSegment(String changes) {
        try {
            int numberOfSegment = Integer.parseInt(changes);
            return numberOfSegment + 1;
        } catch (NumberFormatException e) {
            log.error("getNumberOfSegment::error: ", e.getMessage());
        }
        return 1;
    }

    private static List<String> getTrainNumbers(String trainNumberStr) {
        if(trainNumberStr == null || "".equals(trainNumberStr)) {
            return Collections.EMPTY_LIST;
        }
        return List.of(trainNumberStr.split(",")).stream().map(c -> c.trim()).collect(Collectors.toList());
    }

    private static List<String> getConnections(String connectionsStr, int numberOfSegment) {
        if(numberOfSegment == 1 || connectionsStr == null || "NULL".equals(connectionsStr)) {
            return Collections.EMPTY_LIST;
        }
        return List.of(connectionsStr.split(",")).stream().map(c -> c.trim()).collect(Collectors.toList());
    }

    private static LocalDateTime getLocalDateTime(String date, String time) {
        return LocalDateTime.parse(date + " " + time, SaveATrainService.df2YYYYMMDDHHMM);
    }

    private static List<AvailTransportSearchRS.Segment> makeSegmentsBySaveATrainRoutes(List<SearchApiSearchRSDTO.Route> routes, Map<String, SaveATrainVendorStation> stationMap) {
        if(routes == null || CollectionUtils.isEmpty(routes)) {
            return Collections.EMPTY_LIST;
        }
        return routes.stream().map(r -> makeSegmentBySaveATrainRoute(r, stationMap)).filter(s -> s != null).collect(Collectors.toList());
    }

    private static AvailTransportSearchRS.Segment makeSegmentBySaveATrainRoute(SearchApiSearchRSDTO.Route route, Map<String, SaveATrainVendorStation> stationMap) {
        SaveATrainVendorStation departure = stationMap.get(route.getOriginStation());
        if(departure == null) {
            return null;
        }
        SaveATrainVendorStation arrival = stationMap.get(route.getDestinStation());
        if(arrival == null) {
            return null;
        }
        AvailTransportSearchRS.Segment segment = new AvailTransportSearchRS.Segment();
        segment.setLegs(new ArrayList<>());
        AvailTransportSearchRS.Leg leg = new AvailTransportSearchRS.Leg();
        leg.setDepartureDate(LocalDate.parse(route.getDepartureDate(), SaveATrainService.df2YYYYMMDD));
        leg.setArrivalDate(LocalDate.parse(route.getArrivalDate(), SaveATrainService.df2YYYYMMDD));
        leg.setDepartureDateTime(makeDateTime(route.getDepartureDate(), route.getDepartureTime()));
        leg.setArrivalDateTime(makeDateTime(route.getArrivalDate(), route.getArrivalTime()));
        leg.setDepartureLocation(departure.getName());
        leg.setArrivalLocation(arrival.getName());
        leg.setTotalDuration(route.getDuration());
        leg.setFlightNumber(route.getTrainNumbers());
        segment.getLegs().add(leg);
        return segment;
    }

    public static List<RouteResult> makeRouteResults(SaveATrainStartChooseRSDTO startChooseRs) {
        List<SaveATrainBookApiSubRouteRSDTO.Transfer> transfers = startChooseRs.getSubRouteRS().getTransfers();
        if(CollectionUtils.isEmpty(transfers)) {
            return Collections.EMPTY_LIST;
        }
        SaveATrainBookApiSubRouteRSDTO.Transfer transfer = transfers.get(0);
        RouteResult routeResult = new RouteResult();
        routeResult.setCurrency(CURRENCY_DEFAULT);
        routeResult.setRrpCurrency(CURRENCY_DEFAULT);
        routeResult.setFlight(makeTrainNumbersByTransfer(transfer));
        routeResult.setDepartureDateTime(startChooseRs.getChosenResult().getDepartureDatetime());
        routeResult.setArrivalDateTime(startChooseRs.getChosenResult().getArrivalDatetime());
        routeResult.setSearchIataFrom(startChooseRs.getChosenResult().getRoute().getOriginStation().getUid());
        routeResult.setSearchIataTo(startChooseRs.getChosenResult().getRoute().getDestinationStation().getUid());
        routeResult.setFromIata(startChooseRs.getChosenResult().getRoute().getOriginStation().getUid());
        routeResult.setToIata(startChooseRs.getChosenResult().getRoute().getDestinationStation().getUid());
        routeResult.setSegments(makeSegments(transfer));
        routeResult.setClasses(makeListRouteResultTransportationClasses(transfer, startChooseRs.getSubRouteRS().getSearchIdentifier(),
                                                                        startChooseRs.getSubRouteRS().getResultId()));
        return List.of(routeResult);
    }

    private static List<RouteResult.Segment> makeSegments(SaveATrainBookApiSubRouteRSDTO.Transfer transfer) {
        List<SaveATrainBookApiSubRouteRSDTO.Change> changes = transfer.getChanges();
        if(CollectionUtils.isEmpty(changes)) {
            return Collections.EMPTY_LIST;
        }
        List<RouteResult.Segment> segments = new ArrayList<>();
        int index = 0;
        for(SaveATrainBookApiSubRouteRSDTO.Change change : changes) {
            RouteResult.Segment segment = new RouteResult.Segment();
            segment.setSegmentNumber(index++);
            segment.setDepartureAirportLocationCode(change.getOriginStationSatUid());
            segment.setDepartureDateTime(LocalDateTime.parse(change.getDepartureDatetime(), SaveATrainService.df2YYYYMMDDHHMM));

            segment.setArrivalAirportLocationCode(change.getDestinationStationSatUid());
            segment.setArrivalDateTime(LocalDateTime.parse(change.getArrivalDatetime(), SaveATrainService.df2YYYYMMDDHHMM));
            segment.setOperatingAirlineCode("Train");

            Duration duration = Duration.between(segment.getDepartureDateTime(), segment.getArrivalDateTime());
            segment.setFlightDurationMinutes((int)duration.toMinutes());
            segment.setOperatingAirlineFlightNumber(change.getTrain().getCategory() + "_" + change.getTrain().getNumber());
            segments.add(segment);

        }
        return segments;
    }

    private static List<RouteResult.TransportationClass> makeListRouteResultTransportationClasses(SaveATrainBookApiSubRouteRSDTO.Transfer transfer,
                                                                                                  String searchIdentifier,
                                                                                                  int resultId
                                                                                                  ) {
        List<SaveATrainBookApiSubRouteRSDTO.Fare> fares = transfer.getFares();
        if(fares == null || CollectionUtils.isEmpty(fares)) {
            return Collections.EMPTY_LIST;
        }
        List<RouteResult.TransportationClass> classes = new ArrayList<>();
        for(SaveATrainBookApiSubRouteRSDTO.Fare fare : fares) {
            RouteResult.TransportationClass transportationClass = new RouteResult.TransportationClass();
            transportationClass.setClassCode(fare.getName());
            transportationClass.setReference(fare.getName());
            transportationClass.setAdultRrp(new BigDecimal(fare.getPrice()));
            transportationClass.setAdultNett(new BigDecimal(fare.getPrice()));
            transportationClass.setChildRrp(new BigDecimal(fare.getPrice()));
            transportationClass.setChildNett(new BigDecimal(fare.getPrice()));

            String bookingCode = new StringBuilder(searchIdentifier)
                    .append("|").append(resultId)
                    .append("|").append(transfer.getId())
                    .append("|").append(fare.getId()).toString();
            transportationClass.setBookingCode(bookingCode);
            classes.add(transportationClass);
        }
        return classes;
    }

    private static String makeTrainNumbersByTransfer(SaveATrainBookApiSubRouteRSDTO.Transfer transfer) {
        List<String> trainNumbers = transfer.getChanges().stream().map(c -> c.getTrain().category + "_" + c.getTrain().getNumber()).collect(Collectors.toList());
        return trainNumbers.stream().collect(Collectors.joining(", "));
    }

    private static LocalDateTime makeDateTime(String date, String time) {
        String dateTime = date + " " + time;
        return LocalDateTime.parse(dateTime, SaveATrainService.df2YYYYMMDDHHMM);
    }

    private final static String CURRENCY_DEFAULT = "EUR";
    private final static String WORK_TO_STATION = "Walk";
    private final static LocalDateTime LOCAL_DATE_TIME_WALK = LocalDateTime.parse("9999-01-01 00:01", SaveATrainService.df2YYYYMMDDHHMM);
    private final static String TRAIN_NUMBER_WALK = "Walk";
    public final static String CLASS_CODE_FIRST_CLASS = "First Class";
    public final static String CLASS_CODE_SECOND_CLASS = "Second Class";
}
