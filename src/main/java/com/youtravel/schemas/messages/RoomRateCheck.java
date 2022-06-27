package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@Data
@XmlType(propOrder = {
        "passenger", "roomElement"})

public class RoomRateCheck {
    Passenger passenger;
    List<RoomElement> roomElement;
    @XmlElement(name="Passengers")
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }
    @XmlElement(name="Room")
    public void setRoomElement(List<RoomElement> roomElement) {
        this.roomElement = roomElement;
    }
}
