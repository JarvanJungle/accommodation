package com.youtravel.schemas.messages;

import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@Data
@XmlType(propOrder = {
        "originalRate",
        "finalRate"})
public class Rates {
    float originalRate;
    float finalRate;

    @XmlAttribute(name = "Original_Rate")
    public void setOriginalRate(float originalRate) {
        this.originalRate = originalRate;
    }

    @XmlAttribute(name = "Final_Rate")
    public void setFinalRate(float finalRate) {
        this.finalRate = finalRate;
    }
}
