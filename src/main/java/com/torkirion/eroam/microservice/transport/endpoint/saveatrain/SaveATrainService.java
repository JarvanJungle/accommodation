package com.torkirion.eroam.microservice.transport.endpoint.saveatrain;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.transaction.Transactional;


import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.torkirion.eroam.microservice.apidomain.*;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.ChannelType;
import com.torkirion.eroam.microservice.apidomain.SystemPropertiesDescription.FieldType;
import com.torkirion.eroam.microservice.datadomain.Airline;
import com.torkirion.eroam.microservice.merchandise.apidomain.Booking;
import com.torkirion.eroam.microservice.repository.SystemPropertiesDAO;
import com.torkirion.eroam.microservice.transport.apidomain.*;
import com.torkirion.eroam.microservice.transport.datadomain.IataAirport;
import com.torkirion.eroam.microservice.transport.datadomain.SaveATrainVendorStation;
import com.torkirion.eroam.microservice.transport.dto.AvailTransportSearchRQDTO;
import com.torkirion.eroam.microservice.transport.dto.RouteResult;
import com.torkirion.eroam.microservice.transport.dto.TransportChosenRQDTO;
import com.torkirion.eroam.microservice.transport.endpoint.AbstractTransportService;
import com.torkirion.eroam.microservice.transport.endpoint.SaveATrainMapper;
import com.torkirion.eroam.microservice.transport.endpoint.TransportServiceIF;

import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi.*;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi.SaveATrainSearchApiInterface;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi.SearchApiSearchRSDTO;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.seachapi.SearchApiStartSearchRQDTO;
import com.torkirion.eroam.microservice.transport.repository.SaveATrainVendorStationRepository;
import com.torkirion.eroam.microservice.util.JsonUtil;
import com.torkirion.eroam.microservice.util.ListUtil;
import com.torkirion.eroam.microservice.util.TimeZoneUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

@Slf4j
public class SaveATrainService extends AbstractTransportService implements TransportServiceIF
{
	private final static int THREAD_TIMEOUT = 10000;
	private SystemPropertiesDAO propertiesDAO;

	private SaveATrainVendorStationRepository saveATrainVendorStationRepository;

	public static final String CHANNEL = "SAVEATRAIN";

	public static final String CHANNEL_PREFIX = "ST";

	private static final int TRIP_TYPE_SINGLE_TRIP = 1;

	public SaveATrainService(SystemPropertiesDAO propertiesDAO, SaveATrainVendorStationRepository saveATrainVendorStationRepository) {
		this.propertiesDAO = propertiesDAO;
		this.saveATrainVendorStationRepository = saveATrainVendorStationRepository;
	}

	//@Transactional
	public Collection<AvailTransportSearchRS> search(AvailTransportSearchRQDTO availTransportSearchRQDTO) throws Exception {
		log.info("search::search(availTransportSearchRQDTO)=" + availTransportSearchRQDTO);

		long timer1 = System.currentTimeMillis();
		
		//TODO
		//Search in search
		SaveATrainSearchApiInterface searchApiInterface = new SaveATrainSearchApiInterface(propertiesDAO, availTransportSearchRQDTO.getClient(), SaveATrainService.CHANNEL);
		SaveATrainBookApiInterface bookApiInterface = new SaveATrainBookApiInterface(propertiesDAO, availTransportSearchRQDTO.getClient(), SaveATrainService.CHANNEL);
		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		ListeningExecutorService threadPoolListeningExecutor = MoreExecutors.listeningDecorator(threadPoolExecutor);
		List<ListenableFuture<List<RouteResult>>> listenableFutureList = new ArrayList<>();
		for(AvailTransportSearchRQDTO.Route route : availTransportSearchRQDTO.getRoute()) {
			ListenableFuture<List<RouteResult>> lf = threadPoolListeningExecutor.submit(() -> searchOneRoute(searchApiInterface, bookApiInterface, availTransportSearchRQDTO.getClient(), availTransportSearchRQDTO.getTravellers(), route));
			listenableFutureList.add(lf);
		}
		List<List<RouteResult>> allResults = Futures.allAsList(listenableFutureList).get(30, TimeUnit.SECONDS);
		allResults = allResults.stream().filter(listOfRs -> !CollectionUtils.isEmpty(listOfRs)).collect(Collectors.toList());
		if (log.isDebugEnabled())
			log.debug("search::allResults.size=" + allResults.size());
		List<List<RouteResult>> collector = new ArrayList<>();
		ArrayList<RouteResult> combo = new ArrayList<>();
		combinations(collector, allResults, 0, combo);
		if (log.isDebugEnabled())
			log.debug("search::all combinations.size=" + collector.size());

		Set<AvailTransportSearchRS> results = new HashSet<>();

		int sequence = 0;
		for ( List<RouteResult> routeResultList : collector )
		{
			AvailTransportSearchRS availTransportSearchRS = makeAvailTransportSearchRS(routeResultList, sequence++, availTransportSearchRQDTO.getTravellers());
			if ( availTransportSearchRS != null )
				results.add(availTransportSearchRS);
		}
		return results;
	}

	@Override
	public Collection<AvailTransportSearchRS> choose(String client, TransportChosenRQDTO chooseRQ) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug("choose::start: (client: {}, chooseRQ: \n{})", client, JsonUtil.convertToPrettyJson(chooseRQ));
		}
		//TODO
		SaveATrainBookApiInterface bookApiInterface = new SaveATrainBookApiInterface(propertiesDAO, client, SaveATrainService.CHANNEL);
		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		ListeningExecutorService threadPoolListeningExecutor = MoreExecutors.listeningDecorator(threadPoolExecutor);
		List<ListenableFuture<List<RouteResult>>> listenableFutureList = new ArrayList<>();
		for(TransportChosenRQDTO.ChosenItem chosenItem : chooseRQ.getItems()) {
			ListenableFuture<List<RouteResult>> lf = threadPoolListeningExecutor.submit(() -> chooseOneItem(bookApiInterface, chosenItem, chooseRQ.getTravellers()));
			listenableFutureList.add(lf);
		}
		List<List<RouteResult>> allResults = Futures.allAsList(listenableFutureList).get(30, TimeUnit.SECONDS);
		if(log.isDebugEnabled()) {
			log.debug("choose:: allResults: \n{}", JsonUtil.convertToPrettyJson(allResults));
		}
		if (log.isDebugEnabled())
			log.debug("search::allResults.size=" + allResults.size());
		List<List<RouteResult>> collector = new ArrayList<>();
		ArrayList<RouteResult> combo = new ArrayList<>();
		combinations(collector, allResults, 0, combo);
		if (log.isDebugEnabled())
			log.debug("search::all combinations.size=" + collector.size());

		Set<AvailTransportSearchRS> results = new HashSet<>();

		int sequence = 0;
		for ( List<RouteResult> routeResultList : collector )
		{
			AvailTransportSearchRS availTransportSearchRS = makeAvailTransportSearchRS(routeResultList, sequence++, chooseRQ.getTravellers());
			if ( availTransportSearchRS != null )
				results.add(availTransportSearchRS);
		}
		return results;
	}

	private List<RouteResult> chooseOneItem(SaveATrainBookApiInterface bookApiInterface, TransportChosenRQDTO.ChosenItem chosenItem, TravellerMix travellers) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug("chooseOneItem::startChooseRQ: \n{}", JsonUtil.convertToPrettyJson(chosenItem));
		}
		SaveATrainStartChooseRQDTO startChooseRQ = new SaveATrainStartChooseRQDTO();
		startChooseRQ.setDepartureDatetime(df2YYYYMMDDHHMM.format(chosenItem.getDepartureDatetime()));
		startChooseRQ.setOriginStationUid(chosenItem.getOriginStationUid());
		startChooseRQ.setDestinationStationUid(chosenItem.getDestinationStationUid());
		startChooseRQ.setTravellers(travellers);
		SaveATrainStartChooseRSDTO startChooseRS = bookApiInterface.startChoose(startChooseRQ);
		if(log.isDebugEnabled()) {
			log.debug("chooseOneItem::startChooseRS \n{}", JsonUtil.convertToPrettyJson(startChooseRS));
		}
		return SaveATrainMapper.makeRouteResults(startChooseRS);
	}

	@Transactional
	public TransportBookRS book(String client, TransportBookRQ bookRQ) throws Exception
	{
		if (log.isDebugEnabled())
			log.debug("book::recevied: \n {}", JsonUtil.convertToPrettyJson(bookRQ));
		long timer1 = System.currentTimeMillis();
		TransportBookRS bookRS = new TransportBookRS();
		TransportBookRS combinedResult = new TransportBookRS();
		
		// TODO
		SaveATrainBookApiInterface bookApiInterface = new SaveATrainBookApiInterface(propertiesDAO, client, SaveATrainService.CHANNEL);

		/*
		SaveATrainMakeBookingRQDTO.OrderCustomerAttributes orderCustomerAttributes = makeOrderCustomerAttributes(bookRQ.getBooker());
		Set<TransportBookRQ.TransportRequestItem> transportRequestItems = bookRQ.getItems();
		if(transportRequestItems == null || CollectionUtils.isEmpty(transportRequestItems)) {
			throw new Exception("List of TransportRequestItems is empty");
		}

		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		ListeningExecutorService threadPoolListeningExecutor = MoreExecutors.listeningDecorator(threadPoolExecutor);
		List<ListenableFuture<TransportBookRS>> listenableFutureList = new ArrayList<>();
		for(TransportBookRQ.TransportRequestItem transportRequestItem : transportRequestItems) {
			ListenableFuture<TransportBookRS> lf = threadPoolListeningExecutor.submit(() ->
					bookForOneItem(bookApiInterface, client, transportRequestItem, bookRQ, orderCustomerAttributes)
			);
			listenableFutureList.add(lf);
		}

		List<TransportBookRS> transportBookRSList = Futures.allAsList(listenableFutureList).get();

		combinedResult.setInternalBookingReference(bookRS.getInternalBookingReference());

		combineTransportBookRs(combinedResult, transportBookRSList);
		if (log.isDebugEnabled())
			log.debug("book::combinedResult \n{} ", JsonUtil.convertToPrettyJson(combinedResult)); 
			*/
		return combinedResult;
	}

	private void combineTransportBookRs(TransportBookRS combinedResult, List<TransportBookRS> transportBookRSList) {
		if(transportBookRSList == null || CollectionUtils.isEmpty(transportBookRSList)) {
			return;
		}
		combinedResult.setInternalBookingReference(transportBookRSList.get(0).getInternalBookingReference());
		/*
		for(TransportBookRS bookRS : transportBookRSList) {
			combinedResult.getItems().addAll(bookRS.getItems());
			combinedResult.getRemarks().addAll(bookRS.getRemarks());
			combinedResult.getErrors().addAll(bookRS.getErrors());
			combinedResult.getRemarksDetail().addAll(bookRS.getRemarksDetail());
		} */
	}
/*
	private TransportBookRS bookForOneItem(SaveATrainBookApiInterface bookApiInterface,
													  String client,
													  TransportBookRQ.TransportRequestItem transportRequestItem,
										   			  TransportBookRQ bookRQ,
										              SaveATrainMakeBookingRQDTO.OrderCustomerAttributes orderCustomerAttributes
													  ) {
		TransportBookRS transportBookRs = new TransportBookRS();
		//transportBookRs.setBookingReference(transportRequestItem.getTransportCode());
		transportBookRs.setInternalBookingReference(bookRQ.getInternalBookingReference());
		TransportBookRS.ResponseItem item = new TransportBookRS.ResponseItem();
		item.setInternalItemReference(transportRequestItem.getInternalItemReference());
		item.setBookingItemReference(transportRequestItem.getTransportCode());
		item.setChannel(CHANNEL);
		transportBookRs.setInternalBookingReference(bookRQ.getInternalBookingReference());
		transportBookRs.getItems().add(item);
		try {
			SaveATrainStartBookRQDTO saveATrainBookApiBookRQDTO = makeSaveATrainBookApiBookRQ(client, orderCustomerAttributes,
					transportRequestItem, bookRQ.getTravellers(), bookRQ.getCountryCodeOfOrigin());
			SaveATrainStartBookRSDTO saveATrainBookApiBookRs = bookApiInterface.startBook(saveATrainBookApiBookRQDTO);
			transportBookRs.getItems().get(0).setItemStatus(saveATrainBookApiBookRs.isSuccess() ? Booking.ItemStatus.BOOKED : Booking.ItemStatus.FAILED);
			transportBookRs.setRemarks(List.of(saveATrainBookApiBookRs.getRemark()));
			transportBookRs.setRemarksDetail(List.of(saveATrainBookApiBookRs.getRemarkDetail()));
		} catch (Exception e) {
			String error = "internalItemReference: " + transportRequestItem.getInternalItemReference() + ", error: " + e.getMessage();
			transportBookRs.getItems().get(0).setItemStatus(Booking.ItemStatus.FAILED);
			transportBookRs.setErrors(List.of(new ResponseExtraInformation("-1", error)));
		}
		if (log.isDebugEnabled())
			log.debug("book::transportBookRs: \n {}", JsonUtil.convertToPrettyJson(transportBookRs));
		return transportBookRs;
	}
*/


	public TransportCancelRS cancel(String site, TransportCancelRQ cancelRQ) throws Exception
	{
		SaveATrainBookApiInterface bookApiInterface = new SaveATrainBookApiInterface(propertiesDAO, site, SaveATrainService.CHANNEL);
		float totalCancelFee = 0;
		List<ResponseExtraInformation> errors = new ArrayList<>();
		for(TransportCancelRQ.RequestItem item : cancelRQ.getItems()) {
			SaveATrainStartCancelRSDTO startCancelRS = bookApiInterface.startCancel(new SaveATrainStartCancelRQDTO(item.getBookingCode()));
			totalCancelFee += startCancelRS.getCancelFee();
			errors.add(new ResponseExtraInformation("", startCancelRS.getErrors()));
		}
		TransportCancelRS transportCancelRs = new TransportCancelRS("TODO", new CurrencyValue("EUR", new BigDecimal(totalCancelFee)));
		transportCancelRs.setErrors(errors);
		return transportCancelRs;
	}


	public static ChannelType getSystemPropertiesDescription()
	{
		ChannelType channelType = new ChannelType();
		channelType.getFields().add(new SystemPropertiesDescription.Field("If this channel is enabled", "enabled", FieldType.BOOLEAN, false, "false"));
		return channelType;
	}

	private List<RouteResult> searchOneRoute(SaveATrainSearchApiInterface searchApiInterface, SaveATrainBookApiInterface bookApiInterface, String client, TravellerMix travellerMix, AvailTransportSearchRQDTO.Route route)  {
		ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(propertiesDAO.getProperty(null, null, "threadPoolLimit", 10));
		ListeningExecutorService threadPoolListeningExecutor = MoreExecutors.listeningDecorator(threadPoolExecutor);
		List<SaveATrainBookApiSearchRQDTO> searchRQs = null;
		try {
			searchRQs = makeSaveATrainSearchRQListByOneRoute(client, travellerMix, route);
		} catch (Exception e) {
			log.error("searchByRoute::error: {}", e.getMessage());
			return Collections.EMPTY_LIST;
		}
		List<ListenableFuture<SearchApiSearchRSDTO>> listenableFutureList = new ArrayList<>();
		for(SaveATrainBookApiSearchRQDTO searchRQ : searchRQs) {
			SearchApiStartSearchRQDTO searchApiStartSearchRQ = SearchApiStartSearchRQDTO.builder()
					.departureDate(searchRQ.getSearch().getDepartureDatetime())
					.origin(searchRQ.getSearch().getRouteAttributes().getOriginStationAttributes().getUid())
					.destination(searchRQ.getSearch().getRouteAttributes().getDestinationStationAttributes().getUid())
					.passengers("1") // get 1 one passenger
					.tripType(TRIP_TYPE_SINGLE_TRIP)
					.build();
			ListenableFuture<SearchApiSearchRSDTO> f = threadPoolListeningExecutor.submit(() -> searchApiInterface.startSearchInSearchApi(searchApiStartSearchRQ));
			listenableFutureList.add(f);
		}
		try {
			List<SearchApiSearchRSDTO> saveATrainSearchRSs = Futures.allAsList(listenableFutureList).get(60, TimeUnit.SECONDS);
			if(log.isDebugEnabled()) {
				log.debug("searchByRoute::futures get saveATrainSearchRSs: {} \n", JsonUtil.convertToPrettyJson(saveATrainSearchRSs));
			}

			List<SearchApiSearchRSDTO.Route> routes = saveATrainSearchRSs.stream()
					.map(rs -> rs.getResult())
					.flatMap(result -> result.getOutbound().stream())
					.filter(ListUtil.distinctByKeys(SearchApiSearchRSDTO.Route::getOriginStation,
													SearchApiSearchRSDTO.Route::getDestinStation,
													SearchApiSearchRSDTO.Route::getDepartureDate,
													SearchApiSearchRSDTO.Route::getDepartureTime
							)).collect(Collectors.toList());

//			if(log.isDebugEnabled()) {
//				log.debug("searchOneRoute::list of routes response from saveATrain: \n{}", JsonUtil.convertToPrettyJson(routes));
//			}
			if(routes == null || CollectionUtils.isEmpty(routes)) {
				return Collections.EMPTY_LIST;
			}
			List<String> originStationUids = routes.stream().map(r -> r.getOriginStation()).collect(Collectors.toList());
			List<String> destinStationUids = routes.stream().map(r -> r.getDestinStation()).collect(Collectors.toList());
			List<String> uids = new ArrayList<>();
			uids.addAll(originStationUids);
			uids.addAll(destinStationUids);

			List<SaveATrainVendorStation> stations = saveATrainVendorStationRepository.findDistinctByUidIn(uids);
			cacheStations(stations);
			if(stations == null || CollectionUtils.isEmpty(stations)) {
				return Collections.EMPTY_LIST;
			}
			Map<String, SaveATrainVendorStation> stationMap = stations.stream().collect(Collectors.toMap(SaveATrainVendorStation::getUid, Function.identity()));
			return SaveATrainMapper.makeRouteResults(routes, stationMap);
		} catch (Exception e) {
			log.error("searchByRoute::futures get::error: {}", e.getMessage());
		}
		return Collections.EMPTY_LIST;
	}

	private List<SaveATrainBookApiSearchRQDTO> makeSaveATrainSearchRQListByOneRoute(String client, TravellerMix travellerMix, AvailTransportSearchRQDTO.Route route) throws Exception{
		List<SaveATrainVendorStation> departureStations = getStationsByGeo(route.getDepartureNorthwest(), route.getDepartureSoutheast());
		List<SaveATrainVendorStation> departureStationsForSearching = departureStations.stream()
				.filter(s -> s.getSearchable() == true && s.getRecommendedSearch() == true).collect(Collectors.toList());
		if(departureStationsForSearching == null || CollectionUtils.isEmpty(departureStationsForSearching)) {
			throw new Exception("Can't find departure stations");
		}
		List<SaveATrainVendorStation> arrivalStations = getStationsByGeo(route.getArrivalNorthwest(), route.getArrivalSoutheast());
		List<SaveATrainVendorStation> arrivalStationsForSearching = arrivalStations.stream()
				.filter(s -> s.getSearchable() == true && s.getRecommendedSearch() == true).collect(Collectors.toList());
		if(arrivalStationsForSearching == null || CollectionUtils.isEmpty(arrivalStationsForSearching)) {
			throw new Exception("Can't find arrival stations");
		}
		List<SaveATrainBookApiSearchRQDTO.RouteAttributes> routeAttributesList = makeLRouteAttributesList(departureStationsForSearching, arrivalStationsForSearching);
		if(log.isDebugEnabled()) {
			log.debug("makeSaveATrainSearchRQList::routeAttributesList::(size: {}, data: {})", routeAttributesList.size(), routeAttributesList);
		}
		String departureDateStr = df2YYYYMMDD.format(route.getTravelDate()) + " " + START_TIME;
		Map<Integer, SaveATrainBookApiSearchRQDTO.SearchesPassengersAttribute> passengersAttributeMap = makeSearchPassengerAttributes(travellerMix);
		List<SaveATrainBookApiSearchRQDTO> result = new ArrayList<>();
		for(SaveATrainBookApiSearchRQDTO.RouteAttributes routeAttributes : routeAttributesList) {
			SaveATrainBookApiSearchRQDTO.Search search = SaveATrainBookApiSearchRQDTO.Search.builder()
					.departureDatetime(departureDateStr)
					.routeAttributes(routeAttributes)
					.searchesPassengersAttributes(passengersAttributeMap)
					.build();
			SaveATrainBookApiSearchRQDTO searchRQ = SaveATrainBookApiSearchRQDTO.builder()
					.search(search)
					.build();
			result.add(searchRQ);
		}
		return result;
	}

	private Map<Integer, SaveATrainBookApiSearchRQDTO.SearchesPassengersAttribute> makeSearchPassengerAttributes(TravellerMix travellerMix) throws Exception{
		if(log.isDebugEnabled()) {
			log.debug("makeSearchPassengerAttribute::(travellerMix: {})", travellerMix);
		}
		if(travellerMix.getAdultCount() < 1 && CollectionUtils.isEmpty(travellerMix.getChildAges())) {
			throw new Exception("Missing input travellerMix");
		}
		Map<Integer, SaveATrainBookApiSearchRQDTO.SearchesPassengersAttribute> passengersAttributeMap = new HashMap<>();
		int index = 0;

		// adult type
		for(int indexOfAdult = index; indexOfAdult < travellerMix.getAdultCount(); indexOfAdult++) {
			passengersAttributeMap.put(index, SEARCHES_PASSENGERS_ATTRIBUTE_ADULT);
			index++;
		}

		for(Integer childAge : travellerMix.getChildAges()) {
			SaveATrainBookApiSearchRQDTO.SearchesPassengersAttribute attribute = SaveATrainBookApiSearchRQDTO.SearchesPassengersAttribute.builder()
					.age(childAge)
					.passengerTypeAttributes(SEARCH_PASSENGER_TYPE_CHILD)
					.build();
			passengersAttributeMap.put(index, attribute);
			index++;
		}
		//TODO
		//	for()
	//	SaveATrainSearchRQDTO.SearchesPassengersAttribute passengersAttribute = SaveATrainSearchRQDTO.SearchesPassengersAttribute.builder().build();
		if(log.isDebugEnabled()) {
			log.debug("makeSearchPassengerAttributes::passengersAttributeMap");
			log.debug(JsonUtil.convertToPrettyJson(passengersAttributeMap));
		}
		return passengersAttributeMap;
	}

	private List<SaveATrainBookApiSearchRQDTO.RouteAttributes> makeLRouteAttributesList(List<SaveATrainVendorStation> departureStations,
																						List<SaveATrainVendorStation> arrivalStations) {
		List<SaveATrainBookApiSearchRQDTO.RouteAttributes> routeAttributesList = new ArrayList<>();
		for(SaveATrainVendorStation departureStation : departureStations) {
			for(SaveATrainVendorStation arrivalStation : arrivalStations) {
				SaveATrainBookApiSearchRQDTO.RouteAttributes route = SaveATrainBookApiSearchRQDTO.RouteAttributes.builder()
						.originStationAttributes(SaveATrainBookApiSearchRQDTO.StationAttribute.builder().uid(departureStation.getUid()).build())
						.destinationStationAttributes(SaveATrainBookApiSearchRQDTO.StationAttribute.builder().uid(arrivalStation.getUid()).build())
						.build();
				routeAttributesList.add(route);
			}
		}
		return routeAttributesList;
	}

	private List<SaveATrainVendorStation> getStationsByGeo(LatitudeLongitude northwest, LatitudeLongitude southeast) {
		if(log.isDebugEnabled()) {
			log.debug("getVendorStationsByGeo::start::(northwest: {}, southeast:{})", northwest, southeast);
		}
		List<SaveATrainVendorStation> stations = saveATrainVendorStationRepository.findAllInnerNorthwestAndSoutheast(northwest.getLatitude(), northwest.getLongitude(),
				southeast.getLatitude(), southeast.getLongitude());
		if(log.isDebugEnabled()) {
			log.debug("getVendorStationsByGeo::stations {}", stations);
		}
		if(stations == null) {
			return Collections.EMPTY_LIST;
		}
		//TODO limit
		if(stations.size() > 5) {
			stations.subList(0, 5);
		}
		return stations;
	}

	private String getVendorStationUidFromGeo(LatitudeLongitude northwest, LatitudeLongitude southeast) throws Exception {
		if(log.isDebugEnabled()) {
			log.debug("getVendorStationUidFromGeo::start::(northwest: {}, southeast:{})", northwest, southeast);
		}
		List<SaveATrainVendorStation> innerStations = saveATrainVendorStationRepository
				.findAllInnerNorthwestAndSoutheast(northwest.getLatitude(), northwest.getLongitude(),
													southeast.getLatitude(), southeast.getLongitude());
		if(log.isDebugEnabled()) {
			log.debug("getVendorStationUidFromGeo::innerStations: {}", innerStations);
		}
		if(innerStations == null || CollectionUtils.isEmpty(innerStations)) {
			throw new Exception("Can't find any stations by Geo northwest: " + northwest + " - southeast: " + southeast);
		}
		LatitudeLongitude center = getCenterOfNorthwestAndSoutheast(northwest, northwest);
		String nearestStationUid = getStationNearestCenter(innerStations, center);
		if(log.isDebugEnabled()) {
			log.debug("getVendorStationUidFromGeo::(nearstStationUid: {})", nearestStationUid);
		}
		return nearestStationUid;
	}

	private String getStationNearestCenter(List<SaveATrainVendorStation> innerStations, LatitudeLongitude center) {
		if(log.isDebugEnabled()) {
			log.debug("getStationNearestCenter::start::(center: {})", center);
		}
		if(log.isDebugEnabled()) {
			log.debug("getStationNearestCenter::innerStations::(size: {})", innerStations.size());
		}
		AtomicReference<SaveATrainVendorStation> nearestStation = new AtomicReference<>(innerStations.get(0));
		if(innerStations.size() < 2) {
			return nearestStation.get().getUid();
		}

		IntStream stationRage = IntStream.rangeClosed(1, innerStations.size() - 1);
		stationRage.parallel().forEach(x -> {
			if(log.isDebugEnabled()) {
				log.debug("getStationNearestCenter::parallel::forEach::index: {}", innerStations);
			}
			BigDecimal nearestStationAndCenterDistance = center.getLatitude().subtract(nearestStation.get().getGeoCoordinates().getLatitude()).pow(2)
					.add(center.getLongitude().subtract(nearestStation.get().getGeoCoordinates().getLongitude()).pow(2));
			SaveATrainVendorStation station = innerStations.get(x);
			BigDecimal distanceToCenter = center.getLatitude().subtract(station.getGeoCoordinates().getLatitude()).pow(2)
					.add(center.getLongitude().subtract(station.getGeoCoordinates().getLongitude()).pow(2));
			if(distanceToCenter.compareTo(nearestStationAndCenterDistance) == -1) {
				nearestStation.set(station);
			}
		});
		return nearestStation.get().getUid();
	}

	private LatitudeLongitude getCenterOfNorthwestAndSoutheast(LatitudeLongitude northwest, LatitudeLongitude southeast) {
		if(log.isDebugEnabled()) {
			log.debug("getCenterOfNorthwestAndSoutheast::start::(northwest: {}, southeast:{})", northwest, southeast);
		}
		BigDecimal centerLatitude = northwest.getLatitude()
				.add(southeast.getLatitude())
				.divide(NUMBER_2);
		BigDecimal centerLongitude = northwest.getLongitude()
				.add(southeast.getLongitude())
				.divide(NUMBER_2);
		if(log.isDebugEnabled()) {
			log.debug("getCenterOfNorthwestAndSoutheast::center::(latitude: {}, longitude: {})", centerLatitude, centerLongitude);
		}
		return new LatitudeLongitude(centerLatitude, centerLongitude);
	}

	private static Map<String, SaveATrainVendorStation> stationCache = new HashMap<>();
	private void cacheStations(List<SaveATrainVendorStation> list) {
		for(SaveATrainVendorStation station : list) {
			stationCache.put(station.getUid(), station);
		}
	}

	private SaveATrainVendorStation getStationByUid(String uid) {
		if ( stationCache.get(uid) != null ) {
			return stationCache.get(uid);
		}
		Optional<SaveATrainVendorStation> stationOptional = saveATrainVendorStationRepository.findById(uid);
		if (stationOptional.isPresent())
		{
			SaveATrainVendorStation station = stationOptional.get();
			stationCache.put(station.getUid(), station);
			return station;
		}
		else
		{
			log.warn("getAirport::unknown airport " + uid);
			return null;
		}
	}

	@Override
	protected IataAirport getAirport(String iataCode) {
		SaveATrainVendorStation station = getStationByUid(iataCode);
		IataAirport airport = new IataAirport();

		airport.setAirportName(station.getName());
		airport.setCityname(station.getCity());
		airport.setIataCode(station.getUid());
		airport.setLatitude(station.getGeoCoordinates().getLatitude());
		airport.setLongitude(station.getGeoCoordinates().getLongitude());
		airport.setTimezone(TimeZoneUtil.getTimeZoneOffsetByLongitude(station.getGeoCoordinates().getLongitude()));
		return airport;
	}

	@Override
	protected Airline getAirline(String iataCode) {
		return new Airline();
	}

	@Override
	protected String getType() {
		return "rail";
	}

	@Override
	protected String getProvider() {
		return "Save A Train";
	}

	@Override
	protected String makeTransportSearchRSId(List<RouteResult> routeResults) {
		if(CollectionUtils.isEmpty(routeResults)) {
			return "";
		}
		//StringBuffer id = new StringBuffer(routeResult.getDepartureDateTime().format(yyyymmddHHmm));
		StringBuffer id = new StringBuffer();
		id = id.append(CHANNEL_PREFIX);
		for(RouteResult routeResult : routeResults) {
			id = id.append("_").append(routeResult.getDepartureDateTime().format(yyyymmddHHmm))
					.append("|" + routeResult.getSearchIataFrom())
					.append("|" + routeResult.getSearchIataTo());
		}
		return id.toString();
	}

	@Override
	protected RouteResult.TransportationClass findCheapestClass(RouteResult routeResult) {
		List<RouteResult.TransportationClass> classes = routeResult.getClasses();
		if(classes.size() == 1) {
			return classes.get(0);
		}
		Optional<RouteResult.TransportationClass> optional = classes.stream()
				.filter(c -> SaveATrainMapper.CLASS_CODE_SECOND_CLASS.equals(c.getClassCode())).findFirst();
		if(!optional.isEmpty()) {
			return optional.get();
		}
		return new RouteResult.TransportationClass();
	}
/*
	private SaveATrainMakeBookingRQDTO.Booking makeSaveATrainBooking(String client,
																	 SaveATrainMakeBookingRQDTO.OrderCustomerAttributes orderCustomerAttributes,
																	 TransportBookRQ.TransportRequestItem item,
																	 List<Traveller> travellers,
																	 String countryCodeOfOrigin) throws Exception {

		return SaveATrainMakeBookingRQDTO.Booking.builder()
				.orderCustomerAttributes(orderCustomerAttributes)
				.passengersAttributes(makePassengersAttributes(travellers, item.getTransportDate(), countryCodeOfOrigin))
				.seatPreferenceAttributes(new SaveATrainMakeBookingRQDTO.SeatPreferenceAttributes())
				.build();
	}
*/
	private SaveATrainMakeBookingRQDTO.OrderCustomerAttributes makeOrderCustomerAttributes(TransportBookRQ.Booker booker) {
		SaveATrainMakeBookingRQDTO.OrderCustomerAttributes orderCustomerAttributes = SaveATrainMakeBookingRQDTO.OrderCustomerAttributes
				.builder()
				.email(booker.getEmail())
				.fname(booker.getGivenName())
				.lname(booker.getSurname())
				.mobile(booker.getTelephone())
				.build();
		orderCustomerAttributes.setGenderFromTitle(booker.getTitle());
		return orderCustomerAttributes;
	}

	private Map<Integer, SaveATrainMakeBookingRQDTO.PassengersAttribute> makePassengersAttributes(List<Traveller> travellers,
																								  LocalDate transportDate,
																								  String countryCodeOfOrigin) throws Exception {

		if(travellers == null || CollectionUtils.isEmpty(travellers)) {
			return Collections.EMPTY_MAP;
		}
		Map<Integer, SaveATrainMakeBookingRQDTO.PassengersAttribute> passengersAttributes = new HashMap<>();
		int i = 0;
		for(Traveller traveller : travellers) {
			SaveATrainMakeBookingRQDTO.PassengersAttribute passengersAttribute = new SaveATrainMakeBookingRQDTO.PassengersAttribute();
			passengersAttribute.setTitle(traveller.getTitle());
			passengersAttribute.setFname(traveller.getGivenName());
			passengersAttribute.setLname(traveller.getSurname());
			passengersAttribute.setBirthdate(dateFormatYYYYMMDD.format(traveller.getBirthDate()));
			//TODO: need update country later
			passengersAttribute.setCountry("Germany");
			int age = traveller.getAge(transportDate);
			String passageType;
			if(age >= 18) {
				passageType = "Search::PassengerType::Adult";
			} else {
				passageType = "Search::PassengerType::Youth";
			}
			SaveATrainMakeBookingRQDTO.PassengerTypeAttributes passengerTypeAttributes = SaveATrainMakeBookingRQDTO.PassengerTypeAttributes
					.builder()
					.age(age)
					.type(passageType)
					.build();
			passengersAttribute.setPassengerTypeAttributes(passengerTypeAttributes);
			passengersAttributes.put(i++, passengersAttribute);
		}
		return passengersAttributes;
	}

	@Override
	public TransportRateCheckRS rateCheck(String client, TransportRateCheckRQ rateCheckRQ) throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	private SaveATrainStartBookRQDTO makeSaveATrainBookApiBookRQ(String client,
																 SaveATrainMakeBookingRQDTO.OrderCustomerAttributes orderCustomerAttributes,
																 TransportBookRQ.TransportRequestItem item,
																 List<Traveller> travellers,
																 String countryCodeOfOrigin) throws Exception {
		SaveATrainMakeBookingRQDTO.Booking booking = makeSaveATrainBooking(client, orderCustomerAttributes, item, travellers, countryCodeOfOrigin);
		SaveATrainStartBookRQDTO saveATrainBookApiBookRQ = new SaveATrainStartBookRQDTO();
		saveATrainBookApiBookRQ.setBooking(booking);
		saveATrainBookApiBookRQ.serializeTransportCode(item.getTransportCode());
		saveATrainBookApiBookRQ.setInternalItemReference(item.getInternalItemReference());
		return saveATrainBookApiBookRQ;
	}
	*/
}
