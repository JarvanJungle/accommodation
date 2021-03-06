//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.17 at 10:54:05 AM ICT 
//


package com.sabre.schema.sp.reservation.v2_4;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Specifies a PTC (Passenger Type Code) and the associated number of PTC`s - for use in specifying passenger lists.
 * 
 * <p>Java class for PassengerTypeQuantityType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PassengerTypeQuantityType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://services.sabre.com/sp/reservation/v2_4}TravelerCountType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BirthDate" type="{http://services.sabre.com/sp/reservation/v2_4}BirthDateType" minOccurs="0"/&gt;
 *         &lt;element name="Age" type="{http://services.sabre.com/sp/reservation/v2_4}AgeType" minOccurs="0"/&gt;
 *         &lt;element name="State" type="{http://services.sabre.com/sp/reservation/v2_4}StateType" minOccurs="0"/&gt;
 *         &lt;element name="TotalNumber" type="{http://services.sabre.com/sp/reservation/v2_4}TotalNumberType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="changeable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="requestedPassengerIndex" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PassengerTypeQuantityType", propOrder = {
    "birthDate",
    "age",
    "state",
    "totalNumber"
})
public class PassengerTypeQuantityType
    extends TravelerCountType
{

    @XmlElement(name = "BirthDate")
    protected BirthDateType birthDate;
    @XmlElement(name = "Age")
    protected AgeType age;
    @XmlElement(name = "State")
    protected StateType state;
    @XmlElement(name = "TotalNumber")
    protected TotalNumberType totalNumber;
    @XmlAttribute(name = "changeable")
    protected Boolean changeable;
    @XmlAttribute(name = "index")
    protected Integer index;
    @XmlAttribute(name = "requestedPassengerIndex")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger requestedPassengerIndex;

    /**
     * Gets the value of the birthDate property.
     * 
     * @return
     *     possible object is
     *     {@link BirthDateType }
     *     
     */
    public BirthDateType getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the value of the birthDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link BirthDateType }
     *     
     */
    public void setBirthDate(BirthDateType value) {
        this.birthDate = value;
    }

    /**
     * Gets the value of the age property.
     * 
     * @return
     *     possible object is
     *     {@link AgeType }
     *     
     */
    public AgeType getAge() {
        return age;
    }

    /**
     * Sets the value of the age property.
     * 
     * @param value
     *     allowed object is
     *     {@link AgeType }
     *     
     */
    public void setAge(AgeType value) {
        this.age = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link StateType }
     *     
     */
    public StateType getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link StateType }
     *     
     */
    public void setState(StateType value) {
        this.state = value;
    }

    /**
     * Gets the value of the totalNumber property.
     * 
     * @return
     *     possible object is
     *     {@link TotalNumberType }
     *     
     */
    public TotalNumberType getTotalNumber() {
        return totalNumber;
    }

    /**
     * Sets the value of the totalNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link TotalNumberType }
     *     
     */
    public void setTotalNumber(TotalNumberType value) {
        this.totalNumber = value;
    }

    /**
     * Gets the value of the changeable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isChangeable() {
        if (changeable == null) {
            return true;
        } else {
            return changeable;
        }
    }

    /**
     * Sets the value of the changeable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setChangeable(Boolean value) {
        this.changeable = value;
    }

    /**
     * Gets the value of the index property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * Sets the value of the index property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setIndex(Integer value) {
        this.index = value;
    }

    /**
     * Gets the value of the requestedPassengerIndex property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRequestedPassengerIndex() {
        return requestedPassengerIndex;
    }

    /**
     * Sets the value of the requestedPassengerIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRequestedPassengerIndex(BigInteger value) {
        this.requestedPassengerIndex = value;
    }

}
