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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hotel complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hotel">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="zoneCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="coordinates" type="{http://www.hotelbeds.com/schemas/messages}coordinates"/>
 *         &lt;element name="boardCodes" type="{http://www.hotelbeds.com/schemas/messages}boardCodes"/>
 *         &lt;element name="segmentCodes" type="{http://www.hotelbeds.com/schemas/messages}segmentCodes"/>
 *         &lt;element name="interestPoints" type="{http://www.hotelbeds.com/schemas/messages}interestPoints"/>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="postalCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="license" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="phones" type="{http://www.hotelbeds.com/schemas/messages}phones"/>
 *         &lt;element name="rooms" type="{http://www.hotelbeds.com/schemas/messages}rooms"/>
 *         &lt;element name="wildcards" type="{http://www.hotelbeds.com/schemas/messages}wildcards"/>
 *         &lt;element name="facilities" type="{http://www.hotelbeds.com/schemas/messages}facilities"/>
 *         &lt;element name="terminals" type="{http://www.hotelbeds.com/schemas/messages}terminals"/>
 *         &lt;element name="issues" type="{http://www.hotelbeds.com/schemas/messages}hotelIssues"/>
 *         &lt;element name="images" type="{http://www.hotelbeds.com/schemas/messages}images"/>
 *       &lt;/sequence>
 *       &lt;attribute name="code" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="countryCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="destinationCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="categoryCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="categoryGroupCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="chainCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="accommodationTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="web" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="S2C" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="ranking" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="lastUpdate" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hotel", propOrder = {
    "name",
    "description",
    "zoneCode",
    "coordinates",
    "boardCodes",
    "segmentCodes",
    "interestPoints",
    "address",
    "postalCode",
    "city",
    "email",
    "license",
    "phones",
    "rooms",
    "wildcards",
    "facilities",
    "terminals",
    "issues",
    "images"
})
public class Hotel {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(required = true)
    protected String zoneCode;
    @XmlElement(required = true)
    protected Coordinates coordinates;
    @XmlElement(required = true)
    protected BoardCodes boardCodes;
    @XmlElement(required = true)
    protected SegmentCodes segmentCodes;
    @XmlElement(required = true)
    protected InterestPoints interestPoints;
    @XmlElement(required = true)
    protected String address;
    @XmlElement(required = true)
    protected String postalCode;
    @XmlElement(required = true)
    protected String city;
    @XmlElement(required = true)
    protected String email;
    @XmlElement(required = true)
    protected String license;
    @XmlElement(required = true)
    protected Phones phones;
    @XmlElement(required = true)
    protected Rooms rooms;
    @XmlElement(required = true)
    protected Wildcards wildcards;
    @XmlElement(required = true)
    protected Facilities facilities;
    @XmlElement(required = true)
    protected Terminals terminals;
    @XmlElement(required = true)
    protected HotelIssues issues;
    @XmlElement(required = true)
    protected Images images;
    @XmlAttribute
    protected String code;
    @XmlAttribute
    protected String countryCode;
    @XmlAttribute
    protected String destinationCode;
    @XmlAttribute
    protected String categoryCode;
    @XmlAttribute
    protected String categoryGroupCode;
    @XmlAttribute
    protected String chainCode;
    @XmlAttribute
    protected String accommodationTypeCode;
    @XmlAttribute
    protected String web;
    @XmlAttribute(name = "S2C")
    protected String s2C;
    @XmlAttribute
    protected BigInteger ranking;
    @XmlAttribute
    protected String lastUpdate;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the zoneCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZoneCode() {
        return zoneCode;
    }

    /**
     * Sets the value of the zoneCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZoneCode(String value) {
        this.zoneCode = value;
    }

    /**
     * Gets the value of the coordinates property.
     * 
     * @return
     *     possible object is
     *     {@link Coordinates }
     *     
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Coordinates }
     *     
     */
    public void setCoordinates(Coordinates value) {
        this.coordinates = value;
    }

    /**
     * Gets the value of the boardCodes property.
     * 
     * @return
     *     possible object is
     *     {@link BoardCodes }
     *     
     */
    public BoardCodes getBoardCodes() {
        return boardCodes;
    }

    /**
     * Sets the value of the boardCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoardCodes }
     *     
     */
    public void setBoardCodes(BoardCodes value) {
        this.boardCodes = value;
    }

    /**
     * Gets the value of the segmentCodes property.
     * 
     * @return
     *     possible object is
     *     {@link SegmentCodes }
     *     
     */
    public SegmentCodes getSegmentCodes() {
        return segmentCodes;
    }

    /**
     * Sets the value of the segmentCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link SegmentCodes }
     *     
     */
    public void setSegmentCodes(SegmentCodes value) {
        this.segmentCodes = value;
    }

    /**
     * Gets the value of the interestPoints property.
     * 
     * @return
     *     possible object is
     *     {@link InterestPoints }
     *     
     */
    public InterestPoints getInterestPoints() {
        return interestPoints;
    }

    /**
     * Sets the value of the interestPoints property.
     * 
     * @param value
     *     allowed object is
     *     {@link InterestPoints }
     *     
     */
    public void setInterestPoints(InterestPoints value) {
        this.interestPoints = value;
    }

    /**
     * Gets the value of the address property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the value of the address property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddress(String value) {
        this.address = value;
    }

    /**
     * Gets the value of the postalCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the value of the postalCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Gets the value of the city property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the value of the city property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

    /**
     * Gets the value of the license property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLicense() {
        return license;
    }

    /**
     * Sets the value of the license property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLicense(String value) {
        this.license = value;
    }

    /**
     * Gets the value of the phones property.
     * 
     * @return
     *     possible object is
     *     {@link Phones }
     *     
     */
    public Phones getPhones() {
        return phones;
    }

    /**
     * Sets the value of the phones property.
     * 
     * @param value
     *     allowed object is
     *     {@link Phones }
     *     
     */
    public void setPhones(Phones value) {
        this.phones = value;
    }

    /**
     * Gets the value of the rooms property.
     * 
     * @return
     *     possible object is
     *     {@link Rooms }
     *     
     */
    public Rooms getRooms() {
        return rooms;
    }

    /**
     * Sets the value of the rooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link Rooms }
     *     
     */
    public void setRooms(Rooms value) {
        this.rooms = value;
    }

    /**
     * Gets the value of the wildcards property.
     * 
     * @return
     *     possible object is
     *     {@link Wildcards }
     *     
     */
    public Wildcards getWildcards() {
        return wildcards;
    }

    /**
     * Sets the value of the wildcards property.
     * 
     * @param value
     *     allowed object is
     *     {@link Wildcards }
     *     
     */
    public void setWildcards(Wildcards value) {
        this.wildcards = value;
    }

    /**
     * Gets the value of the facilities property.
     * 
     * @return
     *     possible object is
     *     {@link Facilities }
     *     
     */
    public Facilities getFacilities() {
        return facilities;
    }

    /**
     * Sets the value of the facilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link Facilities }
     *     
     */
    public void setFacilities(Facilities value) {
        this.facilities = value;
    }

    /**
     * Gets the value of the terminals property.
     * 
     * @return
     *     possible object is
     *     {@link Terminals }
     *     
     */
    public Terminals getTerminals() {
        return terminals;
    }

    /**
     * Sets the value of the terminals property.
     * 
     * @param value
     *     allowed object is
     *     {@link Terminals }
     *     
     */
    public void setTerminals(Terminals value) {
        this.terminals = value;
    }

    /**
     * Gets the value of the issues property.
     * 
     * @return
     *     possible object is
     *     {@link HotelIssues }
     *     
     */
    public HotelIssues getIssues() {
        return issues;
    }

    /**
     * Sets the value of the issues property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelIssues }
     *     
     */
    public void setIssues(HotelIssues value) {
        this.issues = value;
    }

    /**
     * Gets the value of the images property.
     * 
     * @return
     *     possible object is
     *     {@link Images }
     *     
     */
    public Images getImages() {
        return images;
    }

    /**
     * Sets the value of the images property.
     * 
     * @param value
     *     allowed object is
     *     {@link Images }
     *     
     */
    public void setImages(Images value) {
        this.images = value;
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
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
    }

    /**
     * Gets the value of the destinationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationCode() {
        return destinationCode;
    }

    /**
     * Sets the value of the destinationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationCode(String value) {
        this.destinationCode = value;
    }

    /**
     * Gets the value of the categoryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryCode() {
        return categoryCode;
    }

    /**
     * Sets the value of the categoryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryCode(String value) {
        this.categoryCode = value;
    }

    /**
     * Gets the value of the categoryGroupCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoryGroupCode() {
        return categoryGroupCode;
    }

    /**
     * Sets the value of the categoryGroupCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoryGroupCode(String value) {
        this.categoryGroupCode = value;
    }

    /**
     * Gets the value of the chainCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChainCode() {
        return chainCode;
    }

    /**
     * Sets the value of the chainCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChainCode(String value) {
        this.chainCode = value;
    }

    /**
     * Gets the value of the accommodationTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccommodationTypeCode() {
        return accommodationTypeCode;
    }

    /**
     * Sets the value of the accommodationTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccommodationTypeCode(String value) {
        this.accommodationTypeCode = value;
    }

    /**
     * Gets the value of the web property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWeb() {
        return web;
    }

    /**
     * Sets the value of the web property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWeb(String value) {
        this.web = value;
    }

    /**
     * Gets the value of the s2C property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getS2C() {
        return s2C;
    }

    /**
     * Sets the value of the s2C property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setS2C(String value) {
        this.s2C = value;
    }

    /**
     * Gets the value of the ranking property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRanking() {
        return ranking;
    }

    /**
     * Sets the value of the ranking property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRanking(BigInteger value) {
        this.ranking = value;
    }

    /**
     * Gets the value of the lastUpdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Sets the value of the lastUpdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastUpdate(String value) {
        this.lastUpdate = value;
    }

}
