package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "HtSearchRq")
@XmlType(propOrder = {
        "langID",
        "success",
//        "destination",
        "hotelId",
        "hotelDetail"
})
@Data
public class HTDetail {
    private String langID;
    private boolean success;
//    @XmlElement(name="Destination")
//    private String destination;
    private int hotelId;
    private HotelDetail hotelDetail;

    @XmlElement(name = "LangID")
    public void setLangID(String langID) {
        this.langID = langID;
    }

    @XmlElement(name = "Success")
    public void setSuccess(boolean success) {
        this.success = success;
    }
    @XmlElement(name="HID")
    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }
    @XmlElement(name="Hotel")
    public void setHotelDetail(HotelDetail hotelDetail) {
        this.hotelDetail = hotelDetail;
    }
}