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
        "facilities"
})
public class Room {
    String name;
    List<String> facilities;
    @XmlAttribute(name="name")
    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name="Facility")
    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }
}
