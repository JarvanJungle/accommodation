package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.torkirion.eroam.microservice.apidomain.TravellerMix;
import com.torkirion.eroam.microservice.transport.endpoint.saveatrain.SaveATrainService;
import com.torkirion.eroam.microservice.util.JsonUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Builder
@Data
public class SaveATrainBookApiSearchRQDTO implements Serializable {

    @Builder.Default
    private Search search = Search.builder().build();

    @Builder
    @Data
    public static class StationAttribute {
        private String uid;
    }

    @Builder
    @Data
    public static class RouteAttributes {

        @Builder.Default
        @JsonProperty("origin_station_attributes")
        private StationAttribute originStationAttributes = StationAttribute.builder().build();

        @Builder.Default
        @JsonProperty("destination_station_attributes")
        private StationAttribute destinationStationAttributes = StationAttribute.builder().build();

    }

    @Builder
    @Data
    public static class Search {
        @JsonProperty("departure_datetime")
        private String departureDatetime;

        @JsonProperty("return_departure_datetime")
        private String returnDepartureDatetime;

        @Builder.Default
        @JsonProperty("route_attributes")
        private RouteAttributes routeAttributes = RouteAttributes.builder().build();

        @Builder.Default
        @JsonProperty("searches_passengers_attributes")
        private Map<Integer, SearchesPassengersAttribute> searchesPassengersAttributes = new HashMap<>();
    }

    @Builder
    @Data
    public static class SearchesPassengersAttribute {
        private int age;

        @Builder.Default
        @JsonProperty("passenger_type_attributes")
        private PassengerTypeAttribute passengerTypeAttributes = PassengerTypeAttribute.builder().build();
    }

    @Builder
    @Data
    public static class PassengerTypeAttribute {
        private String type;
    }

    private static enum PassengerType {
        Adult, Youth, Senior
    }

    @JsonIgnore
    public static SaveATrainBookApiSearchRQDTO makeSaveATrainBookApiSearchRQ(SaveATrainStartChooseRQDTO chooseRQ) {
        if(log.isDebugEnabled()) {
            log.debug("makeSaveATrainBookApiSearchRQ::chooseRQ \n{}", JsonUtil.convertToPrettyJson(chooseRQ));
        }
        SaveATrainBookApiSearchRQDTO searchRQ =  SaveATrainBookApiSearchRQDTO.builder().build();
        searchRQ.getSearch().setDepartureDatetime(chooseRQ.getDepartureDatetime());
        searchRQ.getSearch().getRouteAttributes().getOriginStationAttributes().setUid(chooseRQ.getOriginStationUid());
        searchRQ.getSearch().getRouteAttributes().getDestinationStationAttributes().setUid(chooseRQ.getDestinationStationUid());
        searchRQ.getSearch().setReturnDepartureDatetime(null);

        TravellerMix travellers = chooseRQ.getTravellers();
        int passengerIndex = 0;
        for(int i = 0; i < travellers.getAdultCount(); i++) {
            searchRQ.getSearch().getSearchesPassengersAttributes().put(passengerIndex++ , SaveATrainService.SEARCHES_PASSENGERS_ATTRIBUTE_ADULT);
        }
        List<Integer> childAges = travellers.getChildAges();
        if(childAges != null && !CollectionUtils.isEmpty(childAges)) {
            for(int childAge : childAges) {
                SearchesPassengersAttribute childAttribute = SearchesPassengersAttribute.builder().age(childAge)
                        .passengerTypeAttributes(SaveATrainService.SEARCH_PASSENGER_TYPE_CHILD)
                        .build();
                searchRQ.getSearch().getSearchesPassengersAttributes().put(passengerIndex++, childAttribute);
            }
        }
        return searchRQ;
    }


}
