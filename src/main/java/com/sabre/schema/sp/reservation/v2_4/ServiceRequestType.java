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
 * <p>Java class for ServiceRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FreeText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FullText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PassengerAddressInformation" type="{http://services.sabre.com/sp/reservation/v2_4}ServiceRequestType.PassengerAddressInformation" minOccurs="0"/&gt;
 *         &lt;element name="OtherSupplementaryInformation" type="{http://services.sabre.com/sp/reservation/v2_4}ServiceRequestType.OtherSupplementaryInformation" minOccurs="0"/&gt;
 *         &lt;element name="TravelDocument" type="{http://services.sabre.com/sp/reservation/v2_4}ServiceRequestType.TravelDocument" minOccurs="0"/&gt;
 *         &lt;element name="PassengerProvidedContactForPassenger" type="{http://services.sabre.com/sp/reservation/v2_4}ServiceRequestType.PassengerProvidedContactForPassenger" minOccurs="0"/&gt;
 *         &lt;element name="PassengerContactEmail" type="{http://services.sabre.com/sp/reservation/v2_4}ServiceRequestType.PassengerContactEmail" minOccurs="0"/&gt;
 *         &lt;element name="PassengerContactMobilePhone" type="{http://services.sabre.com/sp/reservation/v2_4}ServiceRequestType.PassengerContactMobilePhone" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="actionCode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="airlineCode" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="serviceCount" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="serviceType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="ssrType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceRequestType", propOrder = {
    "comment",
    "freeText",
    "fullText",
    "passengerAddressInformation",
    "otherSupplementaryInformation",
    "travelDocument",
    "passengerProvidedContactForPassenger",
    "passengerContactEmail",
    "passengerContactMobilePhone"
})
public class ServiceRequestType {

    @XmlElement(name = "Comment")
    protected String comment;
    @XmlElement(name = "FreeText")
    protected String freeText;
    @XmlElement(name = "FullText")
    protected String fullText;
    @XmlElement(name = "PassengerAddressInformation")
    protected ServiceRequestTypePassengerAddressInformation passengerAddressInformation;
    @XmlElement(name = "OtherSupplementaryInformation")
    protected ServiceRequestTypeOtherSupplementaryInformation otherSupplementaryInformation;
    @XmlElement(name = "TravelDocument")
    protected ServiceRequestTypeTravelDocument travelDocument;
    @XmlElement(name = "PassengerProvidedContactForPassenger")
    protected ServiceRequestTypePassengerProvidedContactForPassenger passengerProvidedContactForPassenger;
    @XmlElement(name = "PassengerContactEmail")
    protected ServiceRequestTypePassengerContactEmail passengerContactEmail;
    @XmlElement(name = "PassengerContactMobilePhone")
    protected ServiceRequestTypePassengerContactMobilePhone passengerContactMobilePhone;
    @XmlAttribute(name = "actionCode")
    protected String actionCode;
    @XmlAttribute(name = "airlineCode")
    protected String airlineCode;
    @XmlAttribute(name = "code")
    protected String code;
    @XmlAttribute(name = "serviceCount")
    protected String serviceCount;
    @XmlAttribute(name = "serviceType")
    protected String serviceType;
    @XmlAttribute(name = "ssrType")
    protected String ssrType;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the freeText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFreeText() {
        return freeText;
    }

    /**
     * Sets the value of the freeText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFreeText(String value) {
        this.freeText = value;
    }

    /**
     * Gets the value of the fullText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFullText() {
        return fullText;
    }

    /**
     * Sets the value of the fullText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFullText(String value) {
        this.fullText = value;
    }

    /**
     * Gets the value of the passengerAddressInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceRequestTypePassengerAddressInformation }
     *     
     */
    public ServiceRequestTypePassengerAddressInformation getPassengerAddressInformation() {
        return passengerAddressInformation;
    }

    /**
     * Sets the value of the passengerAddressInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceRequestTypePassengerAddressInformation }
     *     
     */
    public void setPassengerAddressInformation(ServiceRequestTypePassengerAddressInformation value) {
        this.passengerAddressInformation = value;
    }

    /**
     * Gets the value of the otherSupplementaryInformation property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceRequestTypeOtherSupplementaryInformation }
     *     
     */
    public ServiceRequestTypeOtherSupplementaryInformation getOtherSupplementaryInformation() {
        return otherSupplementaryInformation;
    }

    /**
     * Sets the value of the otherSupplementaryInformation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceRequestTypeOtherSupplementaryInformation }
     *     
     */
    public void setOtherSupplementaryInformation(ServiceRequestTypeOtherSupplementaryInformation value) {
        this.otherSupplementaryInformation = value;
    }

    /**
     * Gets the value of the travelDocument property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceRequestTypeTravelDocument }
     *     
     */
    public ServiceRequestTypeTravelDocument getTravelDocument() {
        return travelDocument;
    }

    /**
     * Sets the value of the travelDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceRequestTypeTravelDocument }
     *     
     */
    public void setTravelDocument(ServiceRequestTypeTravelDocument value) {
        this.travelDocument = value;
    }

    /**
     * Gets the value of the passengerProvidedContactForPassenger property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceRequestTypePassengerProvidedContactForPassenger }
     *     
     */
    public ServiceRequestTypePassengerProvidedContactForPassenger getPassengerProvidedContactForPassenger() {
        return passengerProvidedContactForPassenger;
    }

    /**
     * Sets the value of the passengerProvidedContactForPassenger property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceRequestTypePassengerProvidedContactForPassenger }
     *     
     */
    public void setPassengerProvidedContactForPassenger(ServiceRequestTypePassengerProvidedContactForPassenger value) {
        this.passengerProvidedContactForPassenger = value;
    }

    /**
     * Gets the value of the passengerContactEmail property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceRequestTypePassengerContactEmail }
     *     
     */
    public ServiceRequestTypePassengerContactEmail getPassengerContactEmail() {
        return passengerContactEmail;
    }

    /**
     * Sets the value of the passengerContactEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceRequestTypePassengerContactEmail }
     *     
     */
    public void setPassengerContactEmail(ServiceRequestTypePassengerContactEmail value) {
        this.passengerContactEmail = value;
    }

    /**
     * Gets the value of the passengerContactMobilePhone property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceRequestTypePassengerContactMobilePhone }
     *     
     */
    public ServiceRequestTypePassengerContactMobilePhone getPassengerContactMobilePhone() {
        return passengerContactMobilePhone;
    }

    /**
     * Sets the value of the passengerContactMobilePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceRequestTypePassengerContactMobilePhone }
     *     
     */
    public void setPassengerContactMobilePhone(ServiceRequestTypePassengerContactMobilePhone value) {
        this.passengerContactMobilePhone = value;
    }

    /**
     * Gets the value of the actionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionCode() {
        return actionCode;
    }

    /**
     * Sets the value of the actionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionCode(String value) {
        this.actionCode = value;
    }

    /**
     * Gets the value of the airlineCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAirlineCode() {
        return airlineCode;
    }

    /**
     * Sets the value of the airlineCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAirlineCode(String value) {
        this.airlineCode = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the serviceCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceCount() {
        return serviceCount;
    }

    /**
     * Sets the value of the serviceCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceCount(String value) {
        this.serviceCount = value;
    }

    /**
     * Gets the value of the serviceType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * Sets the value of the serviceType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceType(String value) {
        this.serviceType = value;
    }

    /**
     * Gets the value of the ssrType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsrType() {
        return ssrType;
    }

    /**
     * Sets the value of the ssrType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsrType(String value) {
        this.ssrType = value;
    }

}
