package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.*;


@Data
@XmlType(propOrder = {
        "hotelId",
        "hotelName",
        "mapping"
})
public class Hotel {
    Integer hotelId;
    String hotelName;
    Mapping mapping;
    @XmlElement(name="Hotel_ID")
    public void setHotelId(Integer hotelId) {
        this.hotelId = hotelId;
    }
    @XmlElement(name="Hotel_Name")
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
    @XmlElement(name="Mapping")
    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }
}
