package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
//@JsonIgnoreProperties(ignoreUnknown = true)
public class SaveATrainMakeBookingRSDTO extends AbstractSaveATrainRSDTO {
//    {
//        "search_identifier": "3JzcKX",
//            "outbound_trip_info": {
//        "origin_station_info": [
//        {
//            "name": "Rome Tiburtina",
//                "language": {
//            "value": "en",
//                    "name": "English"
//        }
//        }
//        ],
//        "destination_station_info": [
//        {
//            "name": "Milan Porta Garibaldi",
//                "language": {
//            "value": "en",
//                    "name": "English"
//        }
//        }
//        ],
//        "departure": "2021-08-01T06:09:00",
//                "arrival": "2021-08-01T16:47:00",
//                "trip_duration": 638,
//                "fare": "Standard, Semi-flexible"
//    },
//        "inbound_trip_info": null,
//            "price": "32.9",
//            "outbound_seat_reservation_fee": "0.0",
//            "inbound_seat_reservation_fee": null,
//            "total_price": "32.9"
//    }


    @JsonProperty("search_identifier")
    private String searchIdentifier;

    @JsonProperty("outbound_trip_info")
    private OutboundTripInfo outboundTripInfo;

    private String price;

    @JsonProperty("outbound_seat_reservation_fee")
    private String outboundSeatReservationFee;

    @JsonProperty("total_price")
    private String totalPrice;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OutboundTripInfo{
        @JsonProperty("origin_station_info")
        private List<SaveATrainStationNameDTO> originStationInfo;

        @JsonProperty("destination_station_info")
        private List<SaveATrainStationNameDTO> destinationStationInfo;

        private Date departure;

        private Date arrival;

        @JsonProperty("trip_duration")
        private int tripDuration;

        private String fare;
    }

    @Override
    public boolean isSuccess() {
        return searchIdentifier != null;
    }
}
