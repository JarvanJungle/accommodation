package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlType(propOrder = {
        "code",
        "name",
        "id",
        "destinations"
})
public class Country {
    protected String code;
    protected String name;
    protected String id;
    List<Destination> destinations;
    @XmlAttribute(name = "Code")
    public void setCode(String code) {
        this.code = code;
    }
    @XmlAttribute(name = "Name")
    public void setName(String name) {
        this.name = name;
    }
    @XmlAttribute(name = "ID")
    public void setId(String id) {
        this.id = id;
    }
    @XmlElement(name="Destination")
//    public void setDestination(List<Destination> destinations) {
//        this.destinations = destinations;
//    }

    public List<Destination> getDestinations() {
        return destinations;
    }
}
