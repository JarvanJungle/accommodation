package com.youtravel.schemas.messages;


import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType(propOrder = {
        "id", "refundable", "adv", "canxPolicy",
        "type",
        "board",
        "rate",
        "offers"
})
public class RoomElement {
    String id;
    boolean refundable;
    boolean adv;
    CanxPolicy canxPolicy;
    String type;
    String board;
    Rates rate;
    Offers offers;

    @XmlAttribute(name = "Id")
    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name = "Refundable")
    public void setRefundable(boolean refundable) {
        this.refundable = refundable;
    }

    @XmlAttribute(name = "ADV")
    public void setAdv(boolean adv) {
        this.adv = adv;
    }

    @XmlElement(name = "CanxPolicy")
    public void setCanxPolicy(CanxPolicy canxPolicy) {
        this.canxPolicy = canxPolicy;
    }

    @XmlElement(name = "Type")
    public void setType(String type) {
        this.type = type;
    }

    @XmlElement(name = "Board")
    public void setBoard(String board) {
        this.board = board;
    }

    @XmlElement(name = "Rates")
    public void setRate(Rates rate) {
        this.rate = rate;
    }

    @XmlElement(name = "Offers")
    public void setOffers(Offers offers) {
        this.offers = offers;
    }
}
