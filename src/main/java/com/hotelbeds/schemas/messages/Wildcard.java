//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.03.11 at 11:37:11 am ICT 
//


package com.hotelbeds.schemas.messages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for wildcard complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="wildcard">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="hotelRoomDescription" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="roomCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="roomType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="characteristicCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wildcard", propOrder = {
    "hotelRoomDescription"
})
public class Wildcard {

    @XmlElement(required = true)
    protected String hotelRoomDescription;
    @XmlAttribute
    protected String roomCode;
    @XmlAttribute
    protected String roomType;
    @XmlAttribute
    protected String characteristicCode;

    /**
     * Gets the value of the hotelRoomDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHotelRoomDescription() {
        return hotelRoomDescription;
    }

    /**
     * Sets the value of the hotelRoomDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHotelRoomDescription(String value) {
        this.hotelRoomDescription = value;
    }

    /**
     * Gets the value of the roomCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomCode() {
        return roomCode;
    }

    /**
     * Sets the value of the roomCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomCode(String value) {
        this.roomCode = value;
    }

    /**
     * Gets the value of the roomType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomType() {
        return roomType;
    }

    /**
     * Sets the value of the roomType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomType(String value) {
        this.roomType = value;
    }

    /**
     * Gets the value of the characteristicCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharacteristicCode() {
        return characteristicCode;
    }

    /**
     * Sets the value of the characteristicCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharacteristicCode(String value) {
        this.characteristicCode = value;
    }

}
