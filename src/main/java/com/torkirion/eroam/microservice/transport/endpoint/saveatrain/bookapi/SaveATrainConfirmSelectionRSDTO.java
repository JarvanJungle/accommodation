package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveATrainConfirmSelectionRSDTO extends AbstractSaveATrainRSDTO {

    @JsonProperty("search_identifier")
    private String searchIdentifier;

    @JsonProperty("outbound_selected_result")
    private Result outboundSelectedResult;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private int id;
        private Route route;


        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonProperty("departure_datetime")
        private LocalDateTime departureDatetime;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonProperty("arrival_datetime")
        private LocalDateTime arrivalDatetime;

        private int duration;

        @JsonProperty("best_price")
        private String bestPrice;

        @JsonProperty("kind_of")
        private String kindOf;

        @JsonProperty("changes_count")
        private int changesCount;

        @JsonProperty("seat_preference")
        private Object seatPreference;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        @JsonProperty("origin_station")
        private SaveATrainStationDTO originStation;

        @JsonProperty("destination_station")
        private SaveATrainStationDTO destinationStation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SaveATrainStationDTO {
        private String uid;
        private List<SaveATrainStationNameDTO> names;
    }

    @Override
    public boolean isSuccess() {
        return (searchIdentifier != null);
    }
}
