//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 12:36:19 AM ICT 
//


package com.sabre.schema.hotel.avail.v4_0_0;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Collection of Hotel Refs in the request search Hotel Availability
 * 
 * <p>Java class for HotelRefs complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HotelRefs"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence maxOccurs="200"&gt;
 *         &lt;element name="HotelRef"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="HotelCode" use="required" type="{http://services.sabre.com/hotel/avail/v4_0_0}StringLength1to20" /&gt;
 *                 &lt;attribute name="CodeContext" type="{http://services.sabre.com/hotel/avail/v4_0_0}CodeContextType" default="SABRE" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HotelRefs", propOrder = {
    "hotelReves"
})
public class HotelRefs {

    @XmlElement(name = "HotelRef", required = true)
    protected List<HotelRefs.HotelRef> hotelReves;

    /**
     * Gets the value of the hotelReves property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the hotelReves property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHotelReves().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HotelRefs.HotelRef }
     * 
     * 
     */
    public List<HotelRefs.HotelRef> getHotelReves() {
        if (hotelReves == null) {
            hotelReves = new ArrayList<HotelRefs.HotelRef>();
        }
        return this.hotelReves;
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
     *       &lt;attribute name="HotelCode" use="required" type="{http://services.sabre.com/hotel/avail/v4_0_0}StringLength1to20" /&gt;
     *       &lt;attribute name="CodeContext" type="{http://services.sabre.com/hotel/avail/v4_0_0}CodeContextType" default="SABRE" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class HotelRef {

        @XmlAttribute(name = "HotelCode", required = true)
        protected String hotelCode;
        @XmlAttribute(name = "CodeContext")
        protected CodeContextType codeContext;

        /**
         * Gets the value of the hotelCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHotelCode() {
            return hotelCode;
        }

        /**
         * Sets the value of the hotelCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHotelCode(String value) {
            this.hotelCode = value;
        }

        /**
         * Gets the value of the codeContext property.
         * 
         * @return
         *     possible object is
         *     {@link CodeContextType }
         *     
         */
        public CodeContextType getCodeContext() {
            if (codeContext == null) {
                return CodeContextType.SABRE;
            } else {
                return codeContext;
            }
        }

        /**
         * Sets the value of the codeContext property.
         * 
         * @param value
         *     allowed object is
         *     {@link CodeContextType }
         *     
         */
        public void setCodeContext(CodeContextType value) {
            this.codeContext = value;
        }

    }

}
