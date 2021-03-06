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
import javax.xml.bind.annotation.XmlValue;


/**
 * 
 *                 An enumerated type defining the guarantee to be applied to this reservation.
 *             
 * 
 * <p>Java class for GuaranteeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GuaranteeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GuaranteesAccepted"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="GuaranteeAccepted" maxOccurs="unbounded"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="PaymentCards" minOccurs="0"&gt;
 *                               &lt;complexType&gt;
 *                                 &lt;complexContent&gt;
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                                     &lt;sequence&gt;
 *                                       &lt;element name="PaymentCard" maxOccurs="unbounded"&gt;
 *                                         &lt;complexType&gt;
 *                                           &lt;simpleContent&gt;
 *                                             &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                                               &lt;attribute name="CardCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                                             &lt;/extension&gt;
 *                                           &lt;/simpleContent&gt;
 *                                         &lt;/complexType&gt;
 *                                       &lt;/element&gt;
 *                                     &lt;/sequence&gt;
 *                                     &lt;attribute name="CVVRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                                   &lt;/restriction&gt;
 *                                 &lt;/complexContent&gt;
 *                               &lt;/complexType&gt;
 *                             &lt;/element&gt;
 *                           &lt;/sequence&gt;
 *                           &lt;attribute name="GuaranteeTypeCode" type="{http://services.sabre.com/hotel/avail/v4_0_0}OTACodeType" /&gt;
 *                           &lt;attribute name="GuaranteeTypeDescription" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DepositPolicies" type="{http://services.sabre.com/hotel/avail/v4_0_0}DepositPolicies" minOccurs="0"/&gt;
 *         &lt;element name="GuaranteeDescription" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="GuaranteeType" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GuaranteeType", propOrder = {
    "guaranteesAccepted",
    "depositPolicies",
    "guaranteeDescription"
})
public class GuaranteeType {

    @XmlElement(name = "GuaranteesAccepted", required = true)
    protected GuaranteeType.GuaranteesAccepted guaranteesAccepted;
    @XmlElement(name = "DepositPolicies")
    protected DepositPolicies depositPolicies;
    @XmlElement(name = "GuaranteeDescription")
    protected GuaranteeType.GuaranteeDescription guaranteeDescription;
    @XmlAttribute(name = "GuaranteeType")
    protected String guaranteeType;

    /**
     * Gets the value of the guaranteesAccepted property.
     * 
     * @return
     *     possible object is
     *     {@link GuaranteeType.GuaranteesAccepted }
     *     
     */
    public GuaranteeType.GuaranteesAccepted getGuaranteesAccepted() {
        return guaranteesAccepted;
    }

    /**
     * Sets the value of the guaranteesAccepted property.
     * 
     * @param value
     *     allowed object is
     *     {@link GuaranteeType.GuaranteesAccepted }
     *     
     */
    public void setGuaranteesAccepted(GuaranteeType.GuaranteesAccepted value) {
        this.guaranteesAccepted = value;
    }

    /**
     * Gets the value of the depositPolicies property.
     * 
     * @return
     *     possible object is
     *     {@link DepositPolicies }
     *     
     */
    public DepositPolicies getDepositPolicies() {
        return depositPolicies;
    }

    /**
     * Sets the value of the depositPolicies property.
     * 
     * @param value
     *     allowed object is
     *     {@link DepositPolicies }
     *     
     */
    public void setDepositPolicies(DepositPolicies value) {
        this.depositPolicies = value;
    }

    /**
     * Gets the value of the guaranteeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link GuaranteeType.GuaranteeDescription }
     *     
     */
    public GuaranteeType.GuaranteeDescription getGuaranteeDescription() {
        return guaranteeDescription;
    }

    /**
     * Sets the value of the guaranteeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link GuaranteeType.GuaranteeDescription }
     *     
     */
    public void setGuaranteeDescription(GuaranteeType.GuaranteeDescription value) {
        this.guaranteeDescription = value;
    }

    /**
     * Gets the value of the guaranteeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuaranteeType() {
        return guaranteeType;
    }

    /**
     * Sets the value of the guaranteeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuaranteeType(String value) {
        this.guaranteeType = value;
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
     *         &lt;element name="Text" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/&gt;
     *       &lt;/sequence&gt;
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
    public static class GuaranteeDescription {

        @XmlElement(name = "Text", required = true)
        protected List<String> texts;

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

    }


    /**
     * 
     *                             An enumerated type defining the guarantee to be applied to this reservation.
     *                         
     * 
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="GuaranteeAccepted" maxOccurs="unbounded"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="PaymentCards" minOccurs="0"&gt;
     *                     &lt;complexType&gt;
     *                       &lt;complexContent&gt;
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                           &lt;sequence&gt;
     *                             &lt;element name="PaymentCard" maxOccurs="unbounded"&gt;
     *                               &lt;complexType&gt;
     *                                 &lt;simpleContent&gt;
     *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
     *                                     &lt;attribute name="CardCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *                                   &lt;/extension&gt;
     *                                 &lt;/simpleContent&gt;
     *                               &lt;/complexType&gt;
     *                             &lt;/element&gt;
     *                           &lt;/sequence&gt;
     *                           &lt;attribute name="CVVRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *                         &lt;/restriction&gt;
     *                       &lt;/complexContent&gt;
     *                     &lt;/complexType&gt;
     *                   &lt;/element&gt;
     *                 &lt;/sequence&gt;
     *                 &lt;attribute name="GuaranteeTypeCode" type="{http://services.sabre.com/hotel/avail/v4_0_0}OTACodeType" /&gt;
     *                 &lt;attribute name="GuaranteeTypeDescription" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
    @XmlType(name = "", propOrder = {
        "guaranteeAccepteds"
    })
    public static class GuaranteesAccepted {

        @XmlElement(name = "GuaranteeAccepted", required = true)
        protected List<GuaranteeType.GuaranteesAccepted.GuaranteeAccepted> guaranteeAccepteds;

        /**
         * Gets the value of the guaranteeAccepteds property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the guaranteeAccepteds property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGuaranteeAccepteds().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GuaranteeType.GuaranteesAccepted.GuaranteeAccepted }
         * 
         * 
         */
        public List<GuaranteeType.GuaranteesAccepted.GuaranteeAccepted> getGuaranteeAccepteds() {
            if (guaranteeAccepteds == null) {
                guaranteeAccepteds = new ArrayList<GuaranteeType.GuaranteesAccepted.GuaranteeAccepted>();
            }
            return this.guaranteeAccepteds;
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
         *         &lt;element name="PaymentCards" minOccurs="0"&gt;
         *           &lt;complexType&gt;
         *             &lt;complexContent&gt;
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *                 &lt;sequence&gt;
         *                   &lt;element name="PaymentCard" maxOccurs="unbounded"&gt;
         *                     &lt;complexType&gt;
         *                       &lt;simpleContent&gt;
         *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
         *                           &lt;attribute name="CardCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *                         &lt;/extension&gt;
         *                       &lt;/simpleContent&gt;
         *                     &lt;/complexType&gt;
         *                   &lt;/element&gt;
         *                 &lt;/sequence&gt;
         *                 &lt;attribute name="CVVRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
         *               &lt;/restriction&gt;
         *             &lt;/complexContent&gt;
         *           &lt;/complexType&gt;
         *         &lt;/element&gt;
         *       &lt;/sequence&gt;
         *       &lt;attribute name="GuaranteeTypeCode" type="{http://services.sabre.com/hotel/avail/v4_0_0}OTACodeType" /&gt;
         *       &lt;attribute name="GuaranteeTypeDescription" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "paymentCards"
        })
        public static class GuaranteeAccepted {

            @XmlElement(name = "PaymentCards")
            protected GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards paymentCards;
            @XmlAttribute(name = "GuaranteeTypeCode")
            protected Integer guaranteeTypeCode;
            @XmlAttribute(name = "GuaranteeTypeDescription")
            protected String guaranteeTypeDescription;

            /**
             * Gets the value of the paymentCards property.
             * 
             * @return
             *     possible object is
             *     {@link GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards }
             *     
             */
            public GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards getPaymentCards() {
                return paymentCards;
            }

            /**
             * Sets the value of the paymentCards property.
             * 
             * @param value
             *     allowed object is
             *     {@link GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards }
             *     
             */
            public void setPaymentCards(GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards value) {
                this.paymentCards = value;
            }

            /**
             * Gets the value of the guaranteeTypeCode property.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getGuaranteeTypeCode() {
                return guaranteeTypeCode;
            }

            /**
             * Sets the value of the guaranteeTypeCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setGuaranteeTypeCode(Integer value) {
                this.guaranteeTypeCode = value;
            }

            /**
             * Gets the value of the guaranteeTypeDescription property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getGuaranteeTypeDescription() {
                return guaranteeTypeDescription;
            }

            /**
             * Sets the value of the guaranteeTypeDescription property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setGuaranteeTypeDescription(String value) {
                this.guaranteeTypeDescription = value;
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
             *         &lt;element name="PaymentCard" maxOccurs="unbounded"&gt;
             *           &lt;complexType&gt;
             *             &lt;simpleContent&gt;
             *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
             *                 &lt;attribute name="CardCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
             *               &lt;/extension&gt;
             *             &lt;/simpleContent&gt;
             *           &lt;/complexType&gt;
             *         &lt;/element&gt;
             *       &lt;/sequence&gt;
             *       &lt;attribute name="CVVRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
             *     &lt;/restriction&gt;
             *   &lt;/complexContent&gt;
             * &lt;/complexType&gt;
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "paymentCards"
            })
            public static class PaymentCards {

                @XmlElement(name = "PaymentCard", required = true)
                protected List<GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards.PaymentCard> paymentCards;
                @XmlAttribute(name = "CVVRequired")
                protected Boolean cvvRequired;

                /**
                 * Gets the value of the paymentCards property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the paymentCards property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getPaymentCards().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards.PaymentCard }
                 * 
                 * 
                 */
                public List<GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards.PaymentCard> getPaymentCards() {
                    if (paymentCards == null) {
                        paymentCards = new ArrayList<GuaranteeType.GuaranteesAccepted.GuaranteeAccepted.PaymentCards.PaymentCard>();
                    }
                    return this.paymentCards;
                }

                /**
                 * Gets the value of the cvvRequired property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Boolean }
                 *     
                 */
                public Boolean isCVVRequired() {
                    return cvvRequired;
                }

                /**
                 * Sets the value of the cvvRequired property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Boolean }
                 *     
                 */
                public void setCVVRequired(Boolean value) {
                    this.cvvRequired = value;
                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType&gt;
                 *   &lt;simpleContent&gt;
                 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
                 *       &lt;attribute name="CardCode" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
                 *     &lt;/extension&gt;
                 *   &lt;/simpleContent&gt;
                 * &lt;/complexType&gt;
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "value"
                })
                public static class PaymentCard {

                    @XmlValue
                    protected String value;
                    @XmlAttribute(name = "CardCode", required = true)
                    protected String cardCode;

                    /**
                     * Gets the value of the value property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getValue() {
                        return value;
                    }

                    /**
                     * Sets the value of the value property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setValue(String value) {
                        this.value = value;
                    }

                    /**
                     * Gets the value of the cardCode property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getCardCode() {
                        return cardCode;
                    }

                    /**
                     * Sets the value of the cardCode property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setCardCode(String value) {
                        this.cardCode = value;
                    }

                }

            }

        }

    }

}
