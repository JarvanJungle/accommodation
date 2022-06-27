package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import com.traveltek.schemas.messages.Port;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "traveltek_port")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PortData {
    @Id
    private Integer id;
    private String name;
    public PortData(Port port) {
        this.id = port.getId().intValue();
        this.name = port.getName();
    }
}
