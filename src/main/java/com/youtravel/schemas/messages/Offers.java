package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType(propOrder = {
        "lastminuteOffer",
        "earlyBookingDiscount",
        "freeStay",
        "freeTransfer",
        "galaMeals",
        "roomsRemaining"})
public class Offers {
    int lastminuteOffer;
    int earlyBookingDiscount;
    int freeStay;
    int freeTransfer;
    int galaMeals;
    int roomsRemaining;
    @XmlAttribute(name="Lastminute_Offer")
    public void setLastminuteOffer(int lastminuteOffer) {
        this.lastminuteOffer = lastminuteOffer;
    }
    @XmlAttribute(name="Early_Booking_Discount")
    public void setEarlyBookingDiscount(int earlyBookingDiscount) {
        this.earlyBookingDiscount = earlyBookingDiscount;
    }
    @XmlAttribute(name="Free_Stay")
    public void setFreeStay(int freeStay) {
        this.freeStay = freeStay;
    }
    @XmlAttribute(name="Free_Transfer")
    public void setFreeTransfer(int freeTransfer) {
        this.freeTransfer = freeTransfer;
    }
    @XmlAttribute(name="Gala_Meals")
    public void setGalaMeals(int galaMeals) {
        this.galaMeals = galaMeals;
    }
    @XmlAttribute(name="Rooms_remaining")
    public void setRoomsRemaining(int roomsRemaining) {
        this.roomsRemaining = roomsRemaining;
    }
}
