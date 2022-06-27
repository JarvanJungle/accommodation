package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType(propOrder = {
        "token"})
public class CanxPolicy {
    String token;

    @XmlAttribute(name = "token")
    public void setToken(String token) {
        this.token = token;
    }
}
