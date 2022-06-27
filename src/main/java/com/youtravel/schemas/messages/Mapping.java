package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlType(propOrder = {
        "latitude",
        "longitude"
})
public class Mapping {
    String latitude;
    String longitude;
    @XmlElement(name="Latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    @XmlElement(name="Longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
