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
 *         &lt;element name="from" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="to" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="total" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="auditData" type="{http://www.hotelbeds.com/schemas/messages}auditData"/>
 *         &lt;element name="facilityGroups" type="{http://www.hotelbeds.com/schemas/messages}facilityGroups"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "from",
    "to",
    "total",
    "auditData",
    "facilityGroups"
})
@XmlRootElement(name = "facilityGroupsRS")
public class FacilityGroupsRS {

    @XmlElement(required = true)
    protected BigInteger from;
    @XmlElement(required = true)
    protected BigInteger to;
    @XmlElement(required = true)
    protected BigInteger total;
    @XmlElement(required = true)
    protected AuditData auditData;
    @XmlElement(required = true)
    protected FacilityGroups facilityGroups;

    /**
     * Gets the value of the from property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFrom(BigInteger value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTo(BigInteger value) {
        this.to = value;
    }

    /**
     * Gets the value of the total property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotal() {
        return total;
    }

    /**
     * Sets the value of the total property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotal(BigInteger value) {
        this.total = value;
    }

    /**
     * Gets the value of the auditData property.
     * 
     * @return
     *     possible object is
     *     {@link AuditData }
     *     
     */
    public AuditData getAuditData() {
        return auditData;
    }

    /**
     * Sets the value of the auditData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AuditData }
     *     
     */
    public void setAuditData(AuditData value) {
        this.auditData = value;
    }

    /**
     * Gets the value of the facilityGroups property.
     * 
     * @return
     *     possible object is
     *     {@link FacilityGroups }
     *     
     */
    public FacilityGroups getFacilityGroups() {
        return facilityGroups;
    }

    /**
     * Sets the value of the facilityGroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link FacilityGroups }
     *     
     */
    public void setFacilityGroups(FacilityGroups value) {
        this.facilityGroups = value;
    }

}
