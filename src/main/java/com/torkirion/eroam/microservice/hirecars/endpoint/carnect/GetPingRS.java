package com.torkirion.eroam.microservice.hirecars.endpoint.carnect;

import com.carnect.schemas.message.PingRS;
import lombok.Data;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Envelope")
@Data
public class GetPingRS {

    @XmlAttribute(name = "Body")
    private Body body;

    @Data
    private static class Body {

        @XmlAttribute(name = "PingRS")
        PingRS pingRS;
    }
}
