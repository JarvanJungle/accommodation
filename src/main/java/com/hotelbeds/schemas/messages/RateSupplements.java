//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.03.11 at 11:37:11 am ICT 
//


package com.hotelbeds.schemas.messages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for rateSupplements complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="rateSupplements">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="rateSupplement" type="{http://www.hotelbeds.com/schemas/messages}rateSupplement"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rateSupplements", propOrder = {
    "rateSupplement"
})
public class RateSupplements {

    @XmlElement(required = true)
    protected RateSupplement rateSupplement;

    /**
     * Gets the value of the rateSupplement property.
     * 
     * @return
     *     possible object is
     *     {@link RateSupplement }
     *     
     */
    public RateSupplement getRateSupplement() {
        return rateSupplement;
    }

    /**
     * Sets the value of the rateSupplement property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateSupplement }
     *     
     */
    public void setRateSupplement(RateSupplement value) {
        this.rateSupplement = value;
    }

}
