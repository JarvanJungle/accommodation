//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 12:36:19 AM ICT 
//


package com.sabre.schema.hotel.avail.v4_0_0;

import java.math.BigDecimal;
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
 * 
 *                 List of HotelAvailInfo
 *             
 * 
 * <p>Java class for HotelAvailInfos complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HotelAvailInfos"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0"&gt;
 *         &lt;element name="HotelAvailInfo" type="{http://services.sabre.com/hotel/avail/v4_0_0}HotelAvailInfo"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="OffSet" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="MaxSearchResults" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" /&gt;
 *       &lt;attribute name="ShopKey" type="{http://services.sabre.com/hotel/avail/v4_0_0}StringLength0to240" /&gt;
 *       &lt;attribute name="SearchLatitude" type="{http://www.w3.org/2001/XMLSchema}decimal" /&gt;
 *       &lt;attribute name="SearchLongitude" type="{http://www.w3.org/2001/XMLSchema}decimal" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HotelAvailInfos", propOrder = {
    "hotelAvailInfos"
})
public class HotelAvailInfos {

    @XmlElement(name = "HotelAvailInfo")
    protected List<HotelAvailInfo> hotelAvailInfos;
    @XmlAttribute(name = "OffSet")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger offSet;
    @XmlAttribute(name = "MaxSearchResults")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maxSearchResults;
    @XmlAttribute(name = "ShopKey")
    protected String shopKey;
    @XmlAttribute(name = "SearchLatitude")
    protected BigDecimal searchLatitude;
    @XmlAttribute(name = "SearchLongitude")
    protected BigDecimal searchLongitude;

    /**
     * Gets the value of the hotelAvailInfos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hotelAvailInfos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHotelAvailInfos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HotelAvailInfo }
     * 
     * 
     */
    public List<HotelAvailInfo> getHotelAvailInfos() {
        if (hotelAvailInfos == null) {
            hotelAvailInfos = new ArrayList<HotelAvailInfo>();
        }
        return this.hotelAvailInfos;
    }

    /**
     * Gets the value of the offSet property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOffSet() {
        return offSet;
    }

    /**
     * Sets the value of the offSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOffSet(BigInteger value) {
        this.offSet = value;
    }

    /**
     * Gets the value of the maxSearchResults property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxSearchResults() {
        return maxSearchResults;
    }

    /**
     * Sets the value of the maxSearchResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxSearchResults(BigInteger value) {
        this.maxSearchResults = value;
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

    /**
     * Gets the value of the searchLatitude property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSearchLatitude() {
        return searchLatitude;
    }

    /**
     * Sets the value of the searchLatitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSearchLatitude(BigDecimal value) {
        this.searchLatitude = value;
    }

    /**
     * Gets the value of the searchLongitude property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSearchLongitude() {
        return searchLongitude;
    }

    /**
     * Sets the value of the searchLongitude property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSearchLongitude(BigDecimal value) {
        this.searchLongitude = value;
    }

}
