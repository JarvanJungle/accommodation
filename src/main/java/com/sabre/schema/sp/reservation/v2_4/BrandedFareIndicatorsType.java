//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.17 at 10:54:05 AM ICT 
//


package com.sabre.schema.sp.reservation.v2_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BrandedFareIndicatorsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BrandedFareIndicatorsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ReturnCheapestUnbrandedFare" type="{http://services.sabre.com/sp/reservation/v2_4}IndRequiredType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="singleBrandedFare" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="multipleBrandedFares" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="returnBrandAncillaries" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BrandedFareIndicatorsType", propOrder = {
    "returnCheapestUnbrandedFare"
})
public class BrandedFareIndicatorsType {

    @XmlElement(name = "ReturnCheapestUnbrandedFare")
    protected IndRequiredType returnCheapestUnbrandedFare;
    @XmlAttribute(name = "singleBrandedFare")
    protected Boolean singleBrandedFare;
    @XmlAttribute(name = "multipleBrandedFares")
    protected Boolean multipleBrandedFares;
    @XmlAttribute(name = "returnBrandAncillaries")
    protected Boolean returnBrandAncillaries;

    /**
     * Gets the value of the returnCheapestUnbrandedFare property.
     * 
     * @return
     *     possible object is
     *     {@link IndRequiredType }
     *     
     */
    public IndRequiredType getReturnCheapestUnbrandedFare() {
        return returnCheapestUnbrandedFare;
    }

    /**
     * Sets the value of the returnCheapestUnbrandedFare property.
     * 
     * @param value
     *     allowed object is
     *     {@link IndRequiredType }
     *     
     */
    public void setReturnCheapestUnbrandedFare(IndRequiredType value) {
        this.returnCheapestUnbrandedFare = value;
    }

    /**
     * Gets the value of the singleBrandedFare property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSingleBrandedFare() {
        return singleBrandedFare;
    }

    /**
     * Sets the value of the singleBrandedFare property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSingleBrandedFare(Boolean value) {
        this.singleBrandedFare = value;
    }

    /**
     * Gets the value of the multipleBrandedFares property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMultipleBrandedFares() {
        return multipleBrandedFares;
    }

    /**
     * Sets the value of the multipleBrandedFares property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMultipleBrandedFares(Boolean value) {
        this.multipleBrandedFares = value;
    }

    /**
     * Gets the value of the returnBrandAncillaries property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReturnBrandAncillaries() {
        return returnBrandAncillaries;
    }

    /**
     * Sets the value of the returnBrandAncillaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReturnBrandAncillaries(Boolean value) {
        this.returnBrandAncillaries = value;
    }

}
