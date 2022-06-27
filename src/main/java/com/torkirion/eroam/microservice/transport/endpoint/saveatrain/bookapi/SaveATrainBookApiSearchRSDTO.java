package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveATrainBookApiSearchRSDTO extends AbstractSaveATrainRSDTO {

    private String identifier;
    private Boolean complete;
    private Route route;

    @JsonProperty("departure_datetime")
    private String departureDatetime;

    @JsonProperty("expiration_time_left")
    private String expirationTimeLeft;

    private List<Result> results;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private Integer id;

        private Route route;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonProperty("departure_datetime")
        private LocalDateTime departureDatetime;
        //arrival_datetime

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonProperty("arrival_datetime")
        private LocalDateTime arrivalDatetime;

        private Long duration;

        @JsonProperty("best_price")
        private BigDecimal bestPrice;

        @JsonProperty("kind_off")
        private String kindOf;

        @JsonProperty("changes_count")
        private Integer changesCount;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Route {
        @JsonProperty("origin_station")
        private Station originStation;

        @JsonProperty("destination_station")
        private Station destinationStation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Station {
        private String uid;
        private List<Name> names;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        private String name;
    }

    @Override
    public boolean isSuccess() {
        return complete != null && complete;
    }
}
