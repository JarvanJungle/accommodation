package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType(propOrder = {
        "currency",
        "hotelRateCheck"

})
public class Session {
    String currency;
    HotelRateCheck hotelRateCheck;
    String id;
    @XmlElement(name="Currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    @XmlElement(name="Hotel")
    public void setHotelRateCheck(HotelRateCheck hotelRateCheck) {
        this.hotelRateCheck = hotelRateCheck;
    }
    @XmlAttribute(name="id")
    public void setId(String id) {
        this.id = id;
    }
}
