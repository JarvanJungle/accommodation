package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType(propOrder = {
        "adult",
        "children",
        "infant",
})
public class Passenger {
    int adult;
    int children;
    int infant;

    @XmlAttribute(name = "Adults")
    public void setAdult(int adult) {
        this.adult = adult;
    }

    @XmlAttribute(name = "Children")
    public void setChildren(int children) {
        this.children = children;
    }

    @XmlAttribute(name = "Infants")
    public void setInfant(int infant) {
        this.infant = infant;
    }
}
