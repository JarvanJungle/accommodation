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
 * Property Information
 * 
 * <p>Java class for PropertyInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PropertyInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence minOccurs="0"&gt;
 *         &lt;element name="PropertyTypeInfo" type="{http://services.sabre.com/hotel/details/v3_0_0}PropertyTypeInfo" minOccurs="0"/&gt;
 *         &lt;element name="Policies" type="{http://services.sabre.com/hotel/details/v3_0_0}Policies" minOccurs="0"/&gt;
 *         &lt;element name="PropertyQualityInfo" type="{http://services.sabre.com/hotel/details/v3_0_0}PropertyQualityInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Floors" type="{http://services.sabre.com/hotel/details/v3_0_0}StringLength0to3" /&gt;
 *       &lt;attribute name="Rooms" type="{http://services.sabre.com/hotel/details/v3_0_0}StringLength0to4" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "details", name = "PropertyInfo", propOrder = {
    "propertyTypeInfo",
    "policies",
    "propertyQualityInfo"
})
public class PropertyInfo {

    @XmlElement(name = "PropertyTypeInfo")
    protected PropertyTypeInfo propertyTypeInfo;
    @XmlElement(name = "Policies")
    protected Policies policies;
    @XmlElement(name = "PropertyQualityInfo")
    protected PropertyQualityInfo propertyQualityInfo;
    @XmlAttribute(name = "Floors")
    protected String floors;
    @XmlAttribute(name = "Rooms")
    protected String rooms;

    /**
     * Gets the value of the propertyTypeInfo property.
     *
     * @return
     *     possible object is
     *     {@link PropertyTypeInfo }
     *
     */
    public PropertyTypeInfo getPropertyTypeInfo() {
        return propertyTypeInfo;
    }

    /**
     * Sets the value of the propertyTypeInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link PropertyTypeInfo }
     *
     */
    public void setPropertyTypeInfo(PropertyTypeInfo value) {
        this.propertyTypeInfo = value;
    }

    /**
     * Gets the value of the policies property.
     *
     * @return
     *     possible object is
     *     {@link Policies }
     *
     */
    public Policies getPolicies() {
        return policies;
    }

    /**
     * Sets the value of the policies property.
     *
     * @param value
     *     allowed object is
     *     {@link Policies }
     *
     */
    public void setPolicies(Policies value) {
        this.policies = value;
    }

    /**
     * Gets the value of the propertyQualityInfo property.
     *
     * @return
     *     possible object is
     *     {@link PropertyQualityInfo }
     *
     */
    public PropertyQualityInfo getPropertyQualityInfo() {
        return propertyQualityInfo;
    }

    /**
     * Sets the value of the propertyQualityInfo property.
     *
     * @param value
     *     allowed object is
     *     {@link PropertyQualityInfo }
     *     
     */
    public void setPropertyQualityInfo(PropertyQualityInfo value) {
        this.propertyQualityInfo = value;
    }

    /**
     * Gets the value of the floors property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFloors() {
        return floors;
    }

    /**
     * Sets the value of the floors property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFloors(String value) {
        this.floors = value;
    }

    /**
     * Gets the value of the rooms property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRooms() {
        return rooms;
    }

    /**
     * Sets the value of the rooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRooms(String value) {
        this.rooms = value;
    }

}
