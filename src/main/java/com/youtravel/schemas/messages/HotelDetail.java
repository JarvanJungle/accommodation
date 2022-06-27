package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Data
@XmlType(propOrder = {
        "name",
        "youtravelRating",
        "officialRating",
        "boardType",
        "minChildAge",
        "maxChildAge",
        "hotelDesc",
        "photos",
        "facilities",
        "rooms",
        "aiType",
        "reratas",
        "hotelAddress"

})
public class HotelDetail {
    String name;
    int youtravelRating;
    String officialRating;
    String boardType;
    String hotelDesc;
    int minChildAge;
    int maxChildAge;
    List<String> photos;
    List<String> facilities;
    List<Room> rooms;
    String aiType;
    String reratas;
    HotelAddress hotelAddress;

    @XmlAttribute(name="Name")
    public void setName(String name) {
        this.name = name;
    }
    @XmlElement(name="Youtravel_Rating")
    public void setYoutravelRating(int youtravelRating) {
        this.youtravelRating = youtravelRating;
    }
    @XmlElement(name="Official_Rating")
    public void setOfficialRating(String officialRating) {
        this.officialRating = officialRating;
    }

    @XmlElement(name="Board_Type")
    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }
    @XmlElement(name="Hotel_Desc")
    public void setHotelDesc(String hotelDesc) {
        this.hotelDesc = hotelDesc;
    }
    @XmlElement(name="MinChildAge")
    public void setMinChildAge(int minChildAge) {
        this.minChildAge = minChildAge;
    }

    @XmlElement(name="MaxChildAge")
    public void setMaxChildAge(int maxChildAge) {
        this.maxChildAge = maxChildAge;
    }
    @XmlElementWrapper(name="Hotel_Photos")
    @XmlElement(name="Photo")
    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
    @XmlElementWrapper(name="Hotel_Facilities")
    @XmlElement(name="Facility")
    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }
//
    @XmlElementWrapper(name="Room_Types")
    @XmlElement(name="Room")
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }
    @XmlElement(name="AI_Type")
    public void setAiType(String aiType) {
        this.aiType = aiType;
    }
    @XmlElement(name="Erratas")
    public void setReratas(String reratas) {
        this.reratas = reratas;
    }

    @XmlElement(name="Hotel_Address")
    public void setHotelAddress(HotelAddress hotelAddress) {
        this.hotelAddress = hotelAddress;
    }
}
