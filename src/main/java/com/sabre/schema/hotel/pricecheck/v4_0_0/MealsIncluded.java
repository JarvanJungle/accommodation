//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.10 at 10:00:19 PM ICT 
//


package com.sabre.schema.hotel.pricecheck.v4_0_0;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Gives the options to get Meal Information
 * 
 * <p>Java class for MealsIncluded complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MealsIncluded"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="Breakfast" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="Lunch" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="Dinner" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="MealPlanIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="MealPlanCode" type="{http://services.sabre.com/hotel/pricecheck/v4_0_0}OTACodeType" /&gt;
 *       &lt;attribute name="GuestCount" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" /&gt;
 *       &lt;attribute name="MealPlanDescription" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "checkRate", name = "MealsIncluded")
public class MealsIncluded {

    @XmlAttribute(name = "Breakfast")
    protected Boolean breakfast;
    @XmlAttribute(name = "Lunch")
    protected Boolean lunch;
    @XmlAttribute(name = "Dinner")
    protected Boolean dinner;
    @XmlAttribute(name = "MealPlanIndicator")
    protected Boolean mealPlanIndicator;
    @XmlAttribute(name = "MealPlanCode")
    protected Integer mealPlanCode;
    @XmlAttribute(name = "GuestCount")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger guestCount;
    @XmlAttribute(name = "MealPlanDescription")
    protected String mealPlanDescription;

    /**
     * Gets the value of the breakfast property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isBreakfast() {
        return breakfast;
    }

    /**
     * Sets the value of the breakfast property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setBreakfast(Boolean value) {
        this.breakfast = value;
    }

    /**
     * Gets the value of the lunch property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLunch() {
        return lunch;
    }

    /**
     * Sets the value of the lunch property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLunch(Boolean value) {
        this.lunch = value;
    }

    /**
     * Gets the value of the dinner property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDinner() {
        return dinner;
    }

    /**
     * Sets the value of the dinner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDinner(Boolean value) {
        this.dinner = value;
    }

    /**
     * Gets the value of the mealPlanIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMealPlanIndicator() {
        return mealPlanIndicator;
    }

    /**
     * Sets the value of the mealPlanIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMealPlanIndicator(Boolean value) {
        this.mealPlanIndicator = value;
    }

    /**
     * Gets the value of the mealPlanCode property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMealPlanCode() {
        return mealPlanCode;
    }

    /**
     * Sets the value of the mealPlanCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMealPlanCode(Integer value) {
        this.mealPlanCode = value;
    }

    /**
     * Gets the value of the guestCount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGuestCount() {
        return guestCount;
    }

    /**
     * Sets the value of the guestCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGuestCount(BigInteger value) {
        this.guestCount = value;
    }

    /**
     * Gets the value of the mealPlanDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMealPlanDescription() {
        return mealPlanDescription;
    }

    /**
     * Sets the value of the mealPlanDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMealPlanDescription(String value) {
        this.mealPlanDescription = value;
    }

}
