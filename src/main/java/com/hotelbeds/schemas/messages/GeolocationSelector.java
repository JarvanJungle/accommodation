//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.03.11 at 11:37:11 am ICT 
//


package com.hotelbeds.schemas.messages;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for geolocationSelector complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="geolocationSelector">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="longitude" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="latitude" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="radius" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="unit" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="secondaryLongitude" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="secondaryLatitude" type="{http://www.w3.org/2001/XMLSchema}float" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "geolocationSelector")
public class GeolocationSelector {

    @XmlAttribute
    protected Float longitude;
    @XmlAttribute
    protected Float latitude;
    @XmlAttribute
    protected BigInteger radius;
    @XmlAttribute
    protected String unit;
    @XmlAttribute
    protected Float secondaryLongitude;
    @XmlAttribute
    protected Float secondaryLatitude;

    /**
     * Gets the value of the longitude property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLongitude(Float value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the latitude property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLatitude(Float value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the radius property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRadius(BigInteger value) {
        this.radius = value;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
    }

    /**
     * Gets the value of the secondaryLongitude property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getSecondaryLongitude() {
        return secondaryLongitude;
    }

    /**
     * Sets the value of the secondaryLongitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setSecondaryLongitude(Float value) {
        this.secondaryLongitude = value;
    }

    /**
     * Gets the value of the secondaryLatitude property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getSecondaryLatitude() {
        return secondaryLatitude;
    }

    /**
     * Sets the value of the secondaryLatitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setSecondaryLatitude(Float value) {
        this.secondaryLatitude = value;
    }

}