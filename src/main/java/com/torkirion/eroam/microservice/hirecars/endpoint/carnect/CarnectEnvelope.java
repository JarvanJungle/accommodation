package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@XmlRootElement(name = "Envelope")
public class CarnectEnvelope<D> {

    private Body body = new Body();

    @Data
    public static class Body<D> {
        private D d;
    }
}
