package com.torkirion.eroam.microservice.transport.datadomain;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;
import java.util.Locale;

@Data
@Entity
@Table(name = "saveatrain_vendor_station")
public class SaveATrainVendorStation {
    @Id
    private String uid;
    private String name;
    private Boolean searchable;
    @Embedded
    private GeoCoordinates geoCoordinates;

    private String city;

    @Column(name = "recommended_search")
    private Boolean recommendedSearch = false;

    public void setName(String name) {
        if(name == null || "".equals(name)) {
            this.recommendedSearch = false;
            return;
        }
        this.name = name.trim();
        for(String tag : RECOMMENDED_SEARCH_TAGS) {
            if(name.toLowerCase().contains(tag)) {
                this.recommendedSearch = true;
                break;
            }
        }
        String[] s = name.trim().split(" ");
        this.city = s[0]; // city's name is first word of station's name
    }

    private static List<String> RECOMMENDED_SEARCH_TAGS = List.of("all station", "midi", "central");
}
