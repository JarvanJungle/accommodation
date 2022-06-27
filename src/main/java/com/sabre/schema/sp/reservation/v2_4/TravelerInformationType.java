//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.17 at 10:54:05 AM ICT 
//


package com.sabre.schema.sp.reservation.v2_4;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies passenger numbers and types.
 * 
 * <p>Java class for TravelerInformationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TravelerInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PassengerTypeQuantity" type="{http://services.sabre.com/sp/reservation/v2_4}PassengerTypeQuantityType" maxOccurs="9"/&gt;
 *         &lt;element name="AirTraveler" type="{http://services.sabre.com/sp/reservation/v2_4}AirTravelerType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TravelerInformationType", propOrder = {
    "passengerTypeQuantity",
    "airTraveler"
})
public class TravelerInformationType {

    @XmlElement(name = "PassengerTypeQuantity", required = true)
    protected List<PassengerTypeQuantityType> passengerTypeQuantity;
    @XmlElement(name = "AirTraveler")
    protected AirTravelerType airTraveler;

    /**
     * Gets the value of the passengerTypeQuantity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the passengerTypeQuantity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPassengerTypeQuantity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PassengerTypeQuantityType }
     * 
     * 
     */
    public List<PassengerTypeQuantityType> getPassengerTypeQuantity() {
        if (passengerTypeQuantity == null) {
            passengerTypeQuantity = new ArrayList<PassengerTypeQuantityType>();
        }
        return this.passengerTypeQuantity;
    }

    /**
     * Gets the value of the airTraveler property.
     * 
     * @return
     *     possible object is
     *     {@link AirTravelerType }
     *     
     */
    public AirTravelerType getAirTraveler() {
        return airTraveler;
    }

    /**
     * Sets the value of the airTraveler property.
     * 
     * @param value
     *     allowed object is
     *     {@link AirTravelerType }
     *     
     */
    public void setAirTraveler(AirTravelerType value) {
        this.airTraveler = value;
    }

}