package com.torkirion.eroam.microservice.accommodation.endpoint.innstant;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "innstant_destination_static", indexes = {
        @Index(name = "insstant_latlong", columnList="lat, lon", unique = false)
})
@Data
@ToString
@NoArgsConstructor
public class DestinationsStaticData {

    @Id
    private String id;

    private String countryid;

    @Column(columnDefinition = "DECIMAL(19,10)")
    private BigDecimal lat;

    @Column(columnDefinition = "DECIMAL(19,10)")
    private BigDecimal lon;

    private String name;

    private String seoname;

    private String type;
}
