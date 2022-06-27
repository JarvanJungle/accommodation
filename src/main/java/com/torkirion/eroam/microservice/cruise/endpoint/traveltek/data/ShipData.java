package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import com.traveltek.schemas.messages.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "traveltek_ship")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShipData {

    @Id
    private Integer id;
    @Lob
    @Convert(converter = ShipDataMapConverter.class)
    private Ship ship;

    public ShipData(Ship ship) {
        this.id = ship.getId().intValue();
        this.ship = ship;
    }

}
