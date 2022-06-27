package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlType(propOrder = {
        "isoCodes",
        "resorts",
        "name",
        "id"
})
public class Destination {
    ISO_Codes isoCodes;
    List<Resort> resorts;
    String name;
    String id;
    @XmlElement(name="ISO_Codes")
    public void setIsoCodes(ISO_Codes isoCodes) {
        this.isoCodes = isoCodes;
    }
//    @XmlElementWrapper
    @XmlElement(name="Resort")
    public List<Resort> getResorts() {
        return resorts;
    }

    @XmlAttribute(name="Code")
    public void setName(String name) {
        this.name = name;
    }
    @XmlAttribute(name="ID")
    public void setId(String id) {
        this.id = id;
    }
}
