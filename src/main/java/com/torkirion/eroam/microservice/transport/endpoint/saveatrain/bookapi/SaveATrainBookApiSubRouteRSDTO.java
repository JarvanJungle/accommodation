package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveATrainBookApiSubRouteRSDTO extends AbstractSaveATrainRSDTO {

    @JsonProperty("result_id")
    private int resultId;

    @JsonProperty("search_identifier")
    private String searchIdentifier;

    private List<Transfer> transfers;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Transfer{
        private int id;
        private List<Change> changes;
        private List<Fare> fares;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Change{

        @JsonProperty("origin_station_names")
        private List<SaveATrainStationNameDTO> originStationNames;

        @JsonProperty("destination_station_names")
        private List<SaveATrainStationNameDTO> destinationStationNames;

        @JsonProperty("origin_station_sat_uid")
        private String originStationSatUid;

        @JsonProperty("destination_station_sat_uid")
        private String destinationStationSatUid;

        @JsonProperty("departure_datetime")
        private String departureDatetime;

        @JsonProperty("arrival_datetime")
        private String arrivalDatetime;

        private Train train;

        private String type;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Train{
        public String category;
        public String number;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fare{
        public int id;
        public String name;
        public String price;
    }

    @Override
    public boolean isSuccess() {
        return errors == null || "".equals(errors);
    }
}
