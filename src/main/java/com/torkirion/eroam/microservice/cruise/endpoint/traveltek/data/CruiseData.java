package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import com.traveltek.schemas.messages.Cruise;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "traveltek_cruise")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CruiseData {

    @EmbeddedId
    private CruiseId id;
    @Column(updatable = false, insertable = false)
    private Integer codeToCruiseId;
    @Column(updatable = false, insertable = false)
    private String country;
    @Lob
    @Convert(converter = CruiseDataMapConverter.class)
    private Cruise cruise;

    public CruiseData(Integer codeToCruiseId, String country, Cruise cruise) {
        this.id = new CruiseId(codeToCruiseId, country);
        this.codeToCruiseId = codeToCruiseId;
        this.country = country;
        this.cruise = cruise;
    }
}

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class CruiseId implements Serializable {
    private Integer codeToCruiseId;
    private String country;
}

