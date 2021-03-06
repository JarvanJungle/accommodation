//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 10:14:00 PM ICT 
//


package com.sabre.schema.hotel.details.v3_0_0;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RoomType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RoomType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BedTypeOptions" type="{http://services.sabre.com/hotel/details/v3_0_0}BedTypeOptions" minOccurs="0"/&gt;
 *         &lt;element name="RoomDescription" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *                 &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AdditionalDetails" type="{http://services.sabre.com/hotel/details/v3_0_0}AdditionalDetails" minOccurs="0"/&gt;
 *         &lt;element name="Amenities" type="{http://services.sabre.com/hotel/details/v3_0_0}RoomAmenities" minOccurs="0"/&gt;
 *         &lt;element name="Occupancy" type="{http://services.sabre.com/hotel/details/v3_0_0}Occupancy" minOccurs="0"/&gt;
 *         &lt;element name="RatePlans" type="{http://services.sabre.com/hotel/details/v3_0_0}RatePlansRef"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="RoomIndex" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="RoomType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="RoomTypeCode" type="{http://services.sabre.com/hotel/details/v3_0_0}OTACodeType" /&gt;
 *       &lt;attribute name="RoomCategory" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="RoomID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="Floor" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="RoomViewCode" type="{http://services.sabre.com/hotel/details/v3_0_0}OTACodeType" /&gt;
 *       &lt;attribute name="RoomViewDescription" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="NonSmoking" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="SharedRoomInd" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="AccessibleRoom" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="RoomSize" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="NumberOfBedRooms" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "details", name = "RoomType", propOrder = {
    "bedTypeOptions",
    "roomDescription",
    "additionalDetails",
    "amenities",
    "occupancy",
    "ratePlans"
})
public class RoomType {

    @XmlElement(name = "BedTypeOptions")
    protected BedTypeOptions bedTypeOptions;
    @XmlElement(name = "RoomDescription")
    protected RoomType.RoomDescription roomDescription;
    @XmlElement(name = "AdditionalDetails")
    protected AdditionalDetails additionalDetails;
    @XmlElement(name = "Amenities")
    protected RoomAmenities amenities;
    @XmlElement(name = "Occupancy")
    protected Occupancy occupancy;
    @XmlElement(name = "RatePlans", required = true)
    protected RatePlansRef ratePlans;
    @XmlAttribute(name = "RoomIndex", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger roomIndex;
    @XmlAttribute(name = "RoomType")
    protected String roomType;
    @XmlAttribute(name = "RoomTypeCode")
    protected Integer roomTypeCode;
    @XmlAttribute(name = "RoomCategory")
    protected String roomCategory;
    @XmlAttribute(name = "RoomID")
    protected String roomID;
    @XmlAttribute(name = "Floor")
    protected Integer floor;
    @XmlAttribute(name = "RoomViewCode")
    protected Integer roomViewCode;
    @XmlAttribute(name = "RoomViewDescription")
    protected String roomViewDescription;
    @XmlAttribute(name = "NonSmoking")
    protected Boolean nonSmoking;
    @XmlAttribute(name = "SharedRoomInd")
    protected Boolean sharedRoomInd;
    @XmlAttribute(name = "AccessibleRoom")
    protected Boolean accessibleRoom;
    @XmlAttribute(name = "RoomSize")
    protected String roomSize;
    @XmlAttribute(name = "NumberOfBedRooms")
    protected BigInteger numberOfBedRooms;

    /**
     * Gets the value of the bedTypeOptions property.
     *
     * @return
     *     possible object is
     *     {@link BedTypeOptions }
     *
     */
    public BedTypeOptions getBedTypeOptions() {
        return bedTypeOptions;
    }

    /**
     * Sets the value of the bedTypeOptions property.
     *
     * @param value
     *     allowed object is
     *     {@link BedTypeOptions }
     *
     */
    public void setBedTypeOptions(BedTypeOptions value) {
        this.bedTypeOptions = value;
    }

    /**
     * Gets the value of the roomDescription property.
     *
     * @return
     *     possible object is
     *     {@link RoomType.RoomDescription }
     *
     */
    public RoomType.RoomDescription getRoomDescription() {
        return roomDescription;
    }

    /**
     * Sets the value of the roomDescription property.
     *
     * @param value
     *     allowed object is
     *     {@link RoomType.RoomDescription }
     *
     */
    public void setRoomDescription(RoomType.RoomDescription value) {
        this.roomDescription = value;
    }

    /**
     * Gets the value of the additionalDetails property.
     *
     * @return
     *     possible object is
     *     {@link AdditionalDetails }
     *
     */
    public AdditionalDetails getAdditionalDetails() {
        return additionalDetails;
    }

    /**
     * Sets the value of the additionalDetails property.
     *
     * @param value
     *     allowed object is
     *     {@link AdditionalDetails }
     *
     */
    public void setAdditionalDetails(AdditionalDetails value) {
        this.additionalDetails = value;
    }

    /**
     * Gets the value of the amenities property.
     *
     * @return
     *     possible object is
     *     {@link RoomAmenities }
     *
     */
    public RoomAmenities getAmenities() {
        return amenities;
    }

    /**
     * Sets the value of the amenities property.
     *
     * @param value
     *     allowed object is
     *     {@link RoomAmenities }
     *
     */
    public void setAmenities(RoomAmenities value) {
        this.amenities = value;
    }

    /**
     * Gets the value of the occupancy property.
     *
     * @return
     *     possible object is
     *     {@link Occupancy }
     *
     */
    public Occupancy getOccupancy() {
        return occupancy;
    }

    /**
     * Sets the value of the occupancy property.
     *
     * @param value
     *     allowed object is
     *     {@link Occupancy }
     *
     */
    public void setOccupancy(Occupancy value) {
        this.occupancy = value;
    }

    /**
     * Gets the value of the ratePlans property.
     *
     * @return
     *     possible object is
     *     {@link RatePlansRef }
     *
     */
    public RatePlansRef getRatePlans() {
        return ratePlans;
    }

    /**
     * Sets the value of the ratePlans property.
     *
     * @param value
     *     allowed object is
     *     {@link RatePlansRef }
     *     
     */
    public void setRatePlans(RatePlansRef value) {
        this.ratePlans = value;
    }

    /**
     * Gets the value of the roomIndex property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRoomIndex() {
        return roomIndex;
    }

    /**
     * Sets the value of the roomIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRoomIndex(BigInteger value) {
        this.roomIndex = value;
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
     * Gets the value of the roomTypeCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRoomTypeCode() {
        return roomTypeCode;
    }

    /**
     * Sets the value of the roomTypeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRoomTypeCode(Integer value) {
        this.roomTypeCode = value;
    }

    /**
     * Gets the value of the roomCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomCategory() {
        return roomCategory;
    }

    /**
     * Sets the value of the roomCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomCategory(String value) {
        this.roomCategory = value;
    }

    /**
     * Gets the value of the roomID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomID() {
        return roomID;
    }

    /**
     * Sets the value of the roomID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomID(String value) {
        this.roomID = value;
    }

    /**
     * Gets the value of the floor property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFloor() {
        return floor;
    }

    /**
     * Sets the value of the floor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFloor(Integer value) {
        this.floor = value;
    }

    /**
     * Gets the value of the roomViewCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRoomViewCode() {
        return roomViewCode;
    }

    /**
     * Sets the value of the roomViewCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRoomViewCode(Integer value) {
        this.roomViewCode = value;
    }

    /**
     * Gets the value of the roomViewDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomViewDescription() {
        return roomViewDescription;
    }

    /**
     * Sets the value of the roomViewDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomViewDescription(String value) {
        this.roomViewDescription = value;
    }

    /**
     * Gets the value of the nonSmoking property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isNonSmoking() {
        return nonSmoking;
    }

    /**
     * Sets the value of the nonSmoking property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setNonSmoking(Boolean value) {
        this.nonSmoking = value;
    }

    /**
     * Gets the value of the sharedRoomInd property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSharedRoomInd() {
        return sharedRoomInd;
    }

    /**
     * Sets the value of the sharedRoomInd property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSharedRoomInd(Boolean value) {
        this.sharedRoomInd = value;
    }

    /**
     * Gets the value of the accessibleRoom property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAccessibleRoom() {
        return accessibleRoom;
    }

    /**
     * Sets the value of the accessibleRoom property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAccessibleRoom(Boolean value) {
        this.accessibleRoom = value;
    }

    /**
     * Gets the value of the roomSize property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoomSize() {
        return roomSize;
    }

    /**
     * Sets the value of the roomSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoomSize(String value) {
        this.roomSize = value;
    }

    /**
     * Gets the value of the numberOfBedRooms property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfBedRooms() {
        return numberOfBedRooms;
    }

    /**
     * Sets the value of the numberOfBedRooms property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfBedRooms(BigInteger value) {
        this.numberOfBedRooms = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "texts"
    })
    public static class RoomDescription {

        @XmlElement(name = "Text")
        protected List<String> texts;
        @XmlAttribute(name = "Name")
        protected String name;

        /**
         * Gets the value of the texts property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the texts property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTexts().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getTexts() {
            if (texts == null) {
                texts = new ArrayList<String>();
            }
            return this.texts;
        }

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

    }

}
