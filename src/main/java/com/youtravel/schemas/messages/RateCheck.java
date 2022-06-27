package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "HtSearchRq")
@XmlType(propOrder = {
        "session"
})
@Data
public class RateCheck {
    @XmlAttribute(name="session")
    Session session;
}
