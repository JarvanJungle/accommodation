//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 10:14:00 PM ICT 
//


package com.sabre.schema.hotel.details.v3_0_0;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DepositPolicy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DepositPolicy"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Deadline" type="{http://services.sabre.com/hotel/details/v3_0_0}DeadlineType" minOccurs="0"/&gt;
 *         &lt;element name="AmountPercent" type="{http://services.sabre.com/hotel/details/v3_0_0}AmountPercentType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "details", name = "DepositPolicy", propOrder = {
    "deadline",
    "amountPercent"
})
public class DepositPolicy {

    @XmlElement(name = "Deadline")
    protected DeadlineType deadline;
    @XmlElement(name = "AmountPercent")
    protected AmountPercentType amountPercent;

    /**
     * Gets the value of the deadline property.
     *
     * @return
     *     possible object is
     *     {@link DeadlineType }
     *
     */
    public DeadlineType getDeadline() {
        return deadline;
    }

    /**
     * Sets the value of the deadline property.
     *
     * @param value
     *     allowed object is
     *     {@link DeadlineType }
     *
     */
    public void setDeadline(DeadlineType value) {
        this.deadline = value;
    }

    /**
     * Gets the value of the amountPercent property.
     *
     * @return
     *     possible object is
     *     {@link AmountPercentType }
     *
     */
    public AmountPercentType getAmountPercent() {
        return amountPercent;
    }

    /**
     * Sets the value of the amountPercent property.
     *
     * @param value
     *     allowed object is
     *     {@link AmountPercentType }
     *     
     */
    public void setAmountPercent(AmountPercentType value) {
        this.amountPercent = value;
    }

}
