package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType(propOrder = {
        "address",
        "postCode",
        "city",
        "phone"
})
public class HotelAddress {
    String address;
    String postCode;
    String city;
    String phone;
    @XmlElement(name="Address")
    public void setAddress(String address) {
        this.address = address;
    }
    @XmlElement(name="Post_Code")
    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }
    @XmlElement(name="City")
    public void setCity(String city) {
        this.city = city;
    }
    @XmlElement(name="Phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }
}

