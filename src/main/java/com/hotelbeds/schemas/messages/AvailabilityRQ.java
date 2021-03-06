//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-793 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.03.11 at 11:37:11 am ICT 
//


package com.hotelbeds.schemas.messages;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="stay" type="{http://www.hotelbeds.com/schemas/messages}staySelector"/>
 *         &lt;element name="occupancies" type="{http://www.hotelbeds.com/schemas/messages}occupanciesSelector"/>
 *         &lt;element name="geolocation" type="{http://www.hotelbeds.com/schemas/messages}geolocationSelector"/>
 *         &lt;element name="hotels" type="{http://www.hotelbeds.com/schemas/messages}hotelSelector" minOccurs="0"/>
 *         &lt;element name="keywords" type="{http://www.hotelbeds.com/schemas/messages}keywordSelector" minOccurs="0"/>
 *         &lt;element name="boards" type="{http://www.hotelbeds.com/schemas/messages}boardSelector" minOccurs="0"/>
 *         &lt;element name="rooms" type="{http://www.hotelbeds.com/schemas/messages}roomSelector" minOccurs="0"/>
 *         &lt;element name="accommodations" type="{http://www.hotelbeds.com/schemas/messages}countries" minOccurs="0"/>
 *         &lt;element name="reviews" type="{http://www.hotelbeds.com/schemas/messages}reviewsSelector" minOccurs="0"/>
 *         &lt;element name="filter" type="{http://www.hotelbeds.com/schemas/messages}countries" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="destination" type="{http://www.hotelbeds.com/schemas/messages}destinationSelector" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sourceMarket" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "stay",
    "occupancies",
    "geolocation",
    "hotels",
    "keywords",
    "boards",
    "rooms",
    "accommodations",
    "reviews",
    "filter",
    "destination"
})
@XmlRootElement(name = "availabilityRQ")
public class AvailabilityRQ {

    @XmlElement(required = true)
    protected StaySelector stay;
    @XmlElement(required = true)
    protected OccupanciesSelector occupancies;
    @XmlElement(required = true)
    protected GeolocationSelector geolocation;
    protected HotelSelector hotels;
    protected KeywordSelector keywords;
    protected BoardSelector boards;
    protected RoomSelector rooms;
    protected Countries accommodations;
    protected ReviewsSelector reviews;
    protected List<Countries> filter;
    protected DestinationSelector destination;
    @XmlAttribute
    protected String sourceMarket;

    /**
     * Gets the value of the stay property.
     * 
     * @return
     *     possible object is
     *     {@link StaySelector }
     *     
     */
    public StaySelector getStay() {
        return stay;
    }

    /**
     * Sets the value of the stay property.
     * 
     * @param value
     *     allowed object is
     *     {@link StaySelector }
     *     
     */
    public void setStay(StaySelector value) {
        this.stay = value;
    }

    /**
     * Gets the value of the occupancies property.
     * 
     * @return
     *     possible object is
     *     {@link OccupanciesSelector }
     *     
     */
    public OccupanciesSelector getOccupancies() {
        return occupancies;
    }

    /**
     * Sets the value of the occupancies property.
     * 
     * @param value
     *     allowed object is
     *     {@link OccupanciesSelector }
     *     
     */
    public void setOccupancies(OccupanciesSelector value) {
        this.occupancies = value;
    }

    /**
     * Gets the value of the geolocation property.
     * 
     * @return
     *     possible object is
     *     {@link GeolocationSelector }
     *     
     */
    public GeolocationSelector getGeolocation() {
        return geolocation;
    }

    /**
     * Sets the value of the geolocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeolocationSelector }
     *     
     */
    public void setGeolocation(GeolocationSelector value) {
        this.geolocation = value;
    }

    /**
     * Gets the value of the hotels property.
     * 
     * @return
     *     possible object is
     *     {@link HotelSelector }
     *     
     */
    public HotelSelector getHotels() {
        return hotels;
    }

    /**
     * Sets the value of the hotels property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelSelector }
     *     
     */
    public void setHotels(HotelSelector value) {
        this.hotels = value;
    }

    /**
     * Gets the value of the keywords property.
     * 
     * @return
     *     possible object is
     *     {@link KeywordSelector }
     *     
     */
    public KeywordSelector getKeywords() {
        return keywords;
    }

    /**
     * Sets the value of the keywords property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeywordSelector }
     *     
     */
    public void setKeywords(KeywordSelector value) {
        this.keywords = value;
    }

    /**
     * Gets the value of the boards property.
     * 
     * @return
     *     possible object is
     *     {@link BoardSelector }
     *     
     */
    public BoardSelector getBoards() {
        return boards;
    }

    /**
     * Sets the value of the boards property.
     * 
     * @param value
     *     allowed object is
     *     {@link BoardSelector }
     *     
     */
    public void setBoards(BoardSelector value) {
        this.boards = value;
    }

    /**
     * Gets the value of the rooms property.
     * 
     * @return
     *     possible object is
     *     {@link RoomSelector }
     *     
     */
    public RoomSelector getRooms() {
        return rooms;
    }

    /**
     * Sets the value of the rooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link RoomSelector }
     *     
     */
    public void setRooms(RoomSelector value) {
        this.rooms = value;
    }

    /**
     * Gets the value of the accommodations property.
     * 
     * @return
     *     possible object is
     *     {@link Countries }
     *     
     */
    public Countries getAccommodations() {
        return accommodations;
    }

    /**
     * Sets the value of the accommodations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Countries }
     *     
     */
    public void setAccommodations(Countries value) {
        this.accommodations = value;
    }

    /**
     * Gets the value of the reviews property.
     * 
     * @return
     *     possible object is
     *     {@link ReviewsSelector }
     *     
     */
    public ReviewsSelector getReviews() {
        return reviews;
    }

    /**
     * Sets the value of the reviews property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReviewsSelector }
     *     
     */
    public void setReviews(ReviewsSelector value) {
        this.reviews = value;
    }

    /**
     * Gets the value of the filter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Countries }
     * 
     * 
     */
    public List<Countries> getFilter() {
        if (filter == null) {
            filter = new ArrayList<Countries>();
        }
        return this.filter;
    }

    /**
     * Gets the value of the destination property.
     * 
     * @return
     *     possible object is
     *     {@link DestinationSelector }
     *     
     */
    public DestinationSelector getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     * 
     * @param value
     *     allowed object is
     *     {@link DestinationSelector }
     *     
     */
    public void setDestination(DestinationSelector value) {
        this.destination = value;
    }

    /**
     * Gets the value of the sourceMarket property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceMarket() {
        return sourceMarket;
    }

    /**
     * Sets the value of the sourceMarket property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceMarket(String value) {
        this.sourceMarket = value;
    }

}
