package com.torkirion.eroam.microservice.transport.endpoint.saveatrain.bookapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Builder
public class SaveATrainMakeBookingRQDTO {
//    {
//        "booking": {
//        "search_identifier": "{{identifier}}",
//                "order_customer_attributes": {
//            "email": "dennisa@saveatrain.com",
//                    "fname": "dennisa",
//                    "lname": "dennisaa",
//                    "gender": "M"
//        },
//        "passengers_attributes": {
//            "0": {
//                "title": "Mr",
//                        "fname": "dennisb",
//                        "lname": "dennisbb",
//                        "birthdate": "1986-05-19",
//                        "country": "Germany",
//                        "passenger_type_attributes": {
//                    "type": "Search::PassengerType::Adult",
//                            "age": 34
//                }
//            }
//        },
//        "seat_preference_attributes": {
//            "seat_preference_outbound": null
//        }
//    }
//    }

    // import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString), Root.class); */

    private Booking booking;

    @Getter
    @Builder
    public static class Booking {

        @Setter
        @JsonProperty("search_identifier")
        private String searchIdentifier;

        @JsonProperty("order_customer_attributes")
        private OrderCustomerAttributes orderCustomerAttributes;

        @JsonProperty("passengers_attributes")
        private Map<Integer, PassengersAttribute> passengersAttributes;

        @JsonProperty("seat_preference_attributes")
        private SeatPreferenceAttributes seatPreferenceAttributes;
    }

    @Getter
    @Builder
    public static class OrderCustomerAttributes{
        private String email;
        private String fname;
        private String lname;
        private String gender;
        private String mobile;

        public void setGenderFromTitle(String title) {
            if(TITLE_MR.equals(title)) {
                this.gender = GENDER_MALE;
            } else {
                this.gender = GENDER_FEMALE;
            }
        }
    }

    @Data
    public static class PassengersAttribute {
        private String title;
        private String fname;
        private String lname;
        private String birthdate;
        private String country;
//        private String gender;

        public void setTitle(String title) {
            int index = title.indexOf(".");
            if(index == -1) {
                this.title = title;
            }
            this.title = title.substring(0, index);
        }

        @JsonProperty("passenger_type_attributes")
        private PassengerTypeAttributes passengerTypeAttributes;

//        public void setGenderFromTitle(String title) {
//            if(TITLE_MR.equals(title)) {
//                this.gender = GENDER_MALE;
//            } else {
//                this.gender = GENDER_FEMALE;
//            }
//        }
    }

    @Getter
    @Builder
    public static class PassengerTypeAttributes{
        private String type;
        private int age;
    }

    @Data
    public static class SeatPreferenceAttributes {
        @JsonProperty("seat_preference_outbound")
        private String seatPreferenceOutbound;

        @JsonProperty("seat_preference_inbound")
        private String seatPreferenceInbound;
    }

    private static String TITLE_MR = "Mr.";
    private static String TITLE_MRS = "Mrs.";
    private static String TITLE_MS = "Ms.";
    private static String GENDER_MALE = "M";
    private static String GENDER_FEMALE = "F";

}
