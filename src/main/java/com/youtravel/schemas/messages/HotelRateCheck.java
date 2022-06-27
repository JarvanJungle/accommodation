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
        "childAge",
        "roomRateCheck1"

})
public class HotelRateCheck {
    String name;
    int youtravelRating;
    String officialRating;
    String boardType;
    String childAge;
    RoomRateCheck roomRateCheck1;


    @XmlAttribute(name="Hotel_Name")
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
    @XmlElement(name="Child_Age")
    public void setChildAge(String childAge) {
        this.childAge = childAge;
    }
    @XmlElement(name="Room_1")
    public void setRoomRateCheck1(RoomRateCheck roomRateCheck1) {
        this.roomRateCheck1 = roomRateCheck1;
    }
}
