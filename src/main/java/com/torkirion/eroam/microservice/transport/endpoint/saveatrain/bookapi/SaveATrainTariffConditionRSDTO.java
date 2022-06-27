package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SaveATrainTariffConditionRSDTO extends AbstractSaveATrainRSDTO {

//    {
//        "result_id": 320220,
//            "search_identifier": "iqWAyj",
//            "result_fare_id": 1266565,
//            "tariff_conditions": {
//        "name": "Standard, Ordinaria",
//                "conditions": [
//        {
//            "name": "Cancellation policy",
//                "description": "Refunds are possible up to the day of departure and incur 20% charge. Non refundable from the day of departure. Tickets costing less than 10 euro are not refundable. Exchanges are possible ( change time/ date only) before the day of departure at the departure station by paying any difference in price.non exchangeable at the day of departure \r\n. Except for agency fee of 20euro for handling."
//        }
//        ]
//    }
//    }

    @JsonProperty("result_id")
    private String resultId;

    @JsonProperty("search_identifier")
    private String search_identifier;

    @JsonProperty("result_fare_id")
    private String result_fare_id;

    @JsonProperty("tariff_conditions")
    private TariffConditions tariffConditions;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class TariffConditions {
        private String name;
        private List<Condition> conditions;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Condition {
        private String name;
        private String description;
    }

    @Override
    public boolean isSuccess() {
        return errors == null || "".equals(errors);
    }
}
