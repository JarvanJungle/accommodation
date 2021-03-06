//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 10:14:00 PM ICT 
//


package com.sabre.schema.hotel.details.v3_0_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RateKeyRef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RateKeyRef"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RateRange" type="{http://services.sabre.com/hotel/details/v3_0_0}RateRange" minOccurs="0"/&gt;
 *         &lt;element name="RoomSetTypes" type="{http://services.sabre.com/hotel/details/v3_0_0}RoomSetTypes" minOccurs="0"/&gt;
 *         &lt;element name="RateSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="RateKey" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="PrepaidQualifier" type="{http://services.sabre.com/hotel/details/v3_0_0}PrepaidQualifierType" default="IncludePrepaid" /&gt;
 *       &lt;attribute name="RefundableOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="ConvertedRateInfoOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="ExactMatchOnly" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="TravellerCountry" type="{http://services.sabre.com/hotel/details/v3_0_0}AlphaLength2" /&gt;
 *       &lt;attribute name="ShowNegotiatedRatesFirst" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="ShopKey" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RateKeyRef", propOrder = {
    "rateRange",
    "roomSetTypes",
    "rateSource"
})
public class RateKeyRef {

    @XmlElement(name = "RateRange")
    protected RateRange rateRange;
    @XmlElement(name = "RoomSetTypes")
    protected RoomSetTypes roomSetTypes;
    @XmlElement(name = "RateSource")
    protected String rateSource;
    @XmlAttribute(name = "RateKey", required = true)
    protected String rateKey;
    @XmlAttribute(name = "PrepaidQualifier")
    protected PrepaidQualifierType prepaidQualifier;
    @XmlAttribute(name = "RefundableOnly")
    protected Boolean refundableOnly;
    @XmlAttribute(name = "ConvertedRateInfoOnly")
    protected Boolean convertedRateInfoOnly;
    @XmlAttribute(name = "ExactMatchOnly")
    protected Boolean exactMatchOnly;
    @XmlAttribute(name = "TravellerCountry")
    protected String travellerCountry;
    @XmlAttribute(name = "ShowNegotiatedRatesFirst")
    protected Boolean showNegotiatedRatesFirst;
    @XmlAttribute(name = "ShopKey")
    protected String shopKey;

    /**
     * Gets the value of the rateRange property.
     *
     * @return
     *     possible object is
     *     {@link RateRange }
     *
     */
    public RateRange getRateRange() {
        return rateRange;
    }

    /**
     * Sets the value of the rateRange property.
     *
     * @param value
     *     allowed object is
     *     {@link RateRange }
     *
     */
    public void setRateRange(RateRange value) {
        this.rateRange = value;
    }

    /**
     * Gets the value of the roomSetTypes property.
     *
     * @return
     *     possible object is
     *     {@link RoomSetTypes }
     *
     */
    public RoomSetTypes getRoomSetTypes() {
        return roomSetTypes;
    }

    /**
     * Sets the value of the roomSetTypes property.
     *
     * @param value
     *     allowed object is
     *     {@link RoomSetTypes }
     *
     */
    public void setRoomSetTypes(RoomSetTypes value) {
        this.roomSetTypes = value;
    }

    /**
     * Gets the value of the rateSource property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRateSource() {
        return rateSource;
    }

    /**
     * Sets the value of the rateSource property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRateSource(String value) {
        this.rateSource = value;
    }

    /**
     * Gets the value of the rateKey property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRateKey() {
        return rateKey;
    }

    /**
     * Sets the value of the rateKey property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRateKey(String value) {
        this.rateKey = value;
    }

    /**
     * Gets the value of the prepaidQualifier property.
     *
     * @return
     *     possible object is
     *     {@link PrepaidQualifierType }
     *
     */
    public PrepaidQualifierType getPrepaidQualifier() {
        if (prepaidQualifier == null) {
            return PrepaidQualifierType.INCLUDE_PREPAID;
        } else {
            return prepaidQualifier;
        }
    }

    /**
     * Sets the value of the prepaidQualifier property.
     *
     * @param value
     *     allowed object is
     *     {@link PrepaidQualifierType }
     *     
     */
    public void setPrepaidQualifier(PrepaidQualifierType value) {
        this.prepaidQualifier = value;
    }

    /**
     * Gets the value of the refundableOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRefundableOnly() {
        if (refundableOnly == null) {
            return false;
        } else {
            return refundableOnly;
        }
    }

    /**
     * Sets the value of the refundableOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRefundableOnly(Boolean value) {
        this.refundableOnly = value;
    }

    /**
     * Gets the value of the convertedRateInfoOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isConvertedRateInfoOnly() {
        if (convertedRateInfoOnly == null) {
            return true;
        } else {
            return convertedRateInfoOnly;
        }
    }

    /**
     * Sets the value of the convertedRateInfoOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setConvertedRateInfoOnly(Boolean value) {
        this.convertedRateInfoOnly = value;
    }

    /**
     * Gets the value of the exactMatchOnly property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isExactMatchOnly() {
        if (exactMatchOnly == null) {
            return false;
        } else {
            return exactMatchOnly;
        }
    }

    /**
     * Sets the value of the exactMatchOnly property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExactMatchOnly(Boolean value) {
        this.exactMatchOnly = value;
    }

    /**
     * Gets the value of the travellerCountry property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTravellerCountry() {
        return travellerCountry;
    }

    /**
     * Sets the value of the travellerCountry property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTravellerCountry(String value) {
        this.travellerCountry = value;
    }

    /**
     * Gets the value of the showNegotiatedRatesFirst property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isShowNegotiatedRatesFirst() {
        if (showNegotiatedRatesFirst == null) {
            return false;
        } else {
            return showNegotiatedRatesFirst;
        }
    }

    /**
     * Sets the value of the showNegotiatedRatesFirst property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setShowNegotiatedRatesFirst(Boolean value) {
        this.showNegotiatedRatesFirst = value;
    }

    /**
     * Gets the value of the shopKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShopKey() {
        return shopKey;
    }

    /**
     * Sets the value of the shopKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShopKey(String value) {
        this.shopKey = value;
    }

}
