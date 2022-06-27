package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import com.traveltek.schemas.messages.Region;
import lombok.*;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "traveltek_region")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegionData {

    @Id
    private Integer id;
    private String name;

    public RegionData(Region region) {
        this.name = region.getName();
        this.id = region.getId().intValue();
    }
}
