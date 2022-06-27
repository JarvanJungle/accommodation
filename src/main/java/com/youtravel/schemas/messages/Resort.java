package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;
@Data
@XmlType(propOrder = {
        "resortName",
        "hotels",
        "id"
})
public class Resort {
    String resortName;
    List<Hotel> hotels;
    String id;
    @XmlElement(name="Resort_Name")
    public void setResortName(String resortName) {
        this.resortName = resortName;
    }
//    @XmlElementWrapper
    @XmlElement(name="Hotel")
//    public void setHotels(List<Hotel> hotels) {
//        this.hotels = hotels;
//    }

    public List<Hotel> getHotels() {
        return hotels;
    }

    @XmlAttribute(name="ID")
    public void setId(String id) {
        this.id = id;
    }
}
