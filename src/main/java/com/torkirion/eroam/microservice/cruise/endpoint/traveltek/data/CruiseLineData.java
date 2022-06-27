package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import com.traveltek.schemas.messages.Line;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "traveltek_cruise_line")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CruiseLineData {
    @Id
    private Integer id;
    private String logoUrl;
    private String name;
    private String code;

    public CruiseLineData(Line line) {
        this.id = line.getId().intValue();
        this.logoUrl = line.getLogourl();
        this.name = line.getName();
        this.code = line.getCode();
    }
}
