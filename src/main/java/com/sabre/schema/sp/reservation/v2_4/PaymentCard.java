//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.17 at 10:54:05 AM ICT 
//


package com.sabre.schema.sp.reservation.v2_4;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for PaymentCard complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentCard"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PaymentType" type="{http://services.sabre.com/sp/reservation/v2_4}FOP"/&gt;
 *         &lt;element name="TripCategory" type="{http://services.sabre.com/sp/reservation/v2_4}FOP_Trip" minOccurs="0"/&gt;
 *         &lt;element name="CardType" type="{http://services.sabre.com/sp/reservation/v2_4}OTA_Code" minOccurs="0"/&gt;
 *         &lt;element name="CardCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CardNumber"&gt;
 *           &lt;complexType&gt;
 *             &lt;simpleContent&gt;
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *                 &lt;attribute name="tokenized" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                 &lt;attribute name="masked" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                 &lt;attribute name="token" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *               &lt;/extension&gt;
 *             &lt;/simpleContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ExpiryMonth" type="{http://www.w3.org/2001/XMLSchema}gMonth"/&gt;
 *         &lt;element name="ExpiryYear" type="{http://www.w3.org/2001/XMLSchema}gYear"/&gt;
 *         &lt;element name="ExtendPayment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ApprovalList" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Approval" type="{http://services.sabre.com/sp/reservation/v2_4}PaymentCardApproval" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DeferredPaymentID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DefaultExtendPayment" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="SuppressFromInvoice" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="GenerateApprovalAtTicketing" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="CSCResultCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CSCRemark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AVSResultCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="AVSRemark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CardHolderName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CSC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CSCValidatingCarrier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="reconcileAsCash" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentCard", propOrder = {
    "paymentType",
    "tripCategory",
    "cardType",
    "cardCode",
    "cardNumber",
    "expiryMonth",
    "expiryYear",
    "extendPayment",
    "approvalList",
    "deferredPaymentID",
    "defaultExtendPayment",
    "suppressFromInvoice",
    "generateApprovalAtTicketing",
    "cscResultCode",
    "cscRemark",
    "avsResultCode",
    "avsRemark",
    "bNumber",
    "cardHolderName",
    "csc",
    "cscValidatingCarrier"
})
public class PaymentCard {

    @XmlElement(name = "PaymentType", required = true)
    protected String paymentType;
    @XmlElement(name = "TripCategory")
    protected String tripCategory;
    @XmlElement(name = "CardType")
    protected String cardType;
    @XmlElement(name = "CardCode", required = true)
    protected String cardCode;
    @XmlElement(name = "CardNumber", required = true)
    protected PaymentCard.CardNumber cardNumber;
    @XmlElement(name = "ExpiryMonth", required = true)
    @XmlSchemaType(name = "gMonth")
    protected XMLGregorianCalendar expiryMonth;
    @XmlElement(name = "ExpiryYear", required = true)
    @XmlSchemaType(name = "gYear")
    protected XMLGregorianCalendar expiryYear;
    @XmlElement(name = "ExtendPayment")
    protected String extendPayment;
    @XmlElement(name = "ApprovalList")
    protected PaymentCard.ApprovalList approvalList;
    @XmlElement(name = "DeferredPaymentID")
    protected String deferredPaymentID;
    @XmlElement(name = "DefaultExtendPayment")
    protected Boolean defaultExtendPayment;
    @XmlElement(name = "SuppressFromInvoice")
    protected Boolean suppressFromInvoice;
    @XmlElement(name = "GenerateApprovalAtTicketing")
    protected Boolean generateApprovalAtTicketing;
    @XmlElement(name = "CSCResultCode")
    protected String cscResultCode;
    @XmlElement(name = "CSCRemark")
    protected String cscRemark;
    @XmlElement(name = "AVSResultCode")
    protected String avsResultCode;
    @XmlElement(name = "AVSRemark")
    protected String avsRemark;
    @XmlElement(name = "BNumber")
    protected String bNumber;
    @XmlElement(name = "CardHolderName")
    protected String cardHolderName;
    @XmlElement(name = "CSC")
    protected String csc;
    @XmlElement(name = "CSCValidatingCarrier")
    protected String cscValidatingCarrier;
    @XmlAttribute(name = "reconcileAsCash")
    protected Boolean reconcileAsCash;

    /**
     * Gets the value of the paymentType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the value of the paymentType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentType(String value) {
        this.paymentType = value;
    }

    /**
     * Gets the value of the tripCategory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTripCategory() {
        return tripCategory;
    }

    /**
     * Sets the value of the tripCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTripCategory(String value) {
        this.tripCategory = value;
    }

    /**
     * Gets the value of the cardType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the value of the cardType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardType(String value) {
        this.cardType = value;
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

    /**
     * Gets the value of the cardNumber property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCard.CardNumber }
     *     
     */
    public PaymentCard.CardNumber getCardNumber() {
        return cardNumber;
    }

    /**
     * Sets the value of the cardNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCard.CardNumber }
     *     
     */
    public void setCardNumber(PaymentCard.CardNumber value) {
        this.cardNumber = value;
    }

    /**
     * Gets the value of the expiryMonth property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryMonth() {
        return expiryMonth;
    }

    /**
     * Sets the value of the expiryMonth property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryMonth(XMLGregorianCalendar value) {
        this.expiryMonth = value;
    }

    /**
     * Gets the value of the expiryYear property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryYear() {
        return expiryYear;
    }

    /**
     * Sets the value of the expiryYear property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryYear(XMLGregorianCalendar value) {
        this.expiryYear = value;
    }

    /**
     * Gets the value of the extendPayment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtendPayment() {
        return extendPayment;
    }

    /**
     * Sets the value of the extendPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtendPayment(String value) {
        this.extendPayment = value;
    }

    /**
     * Gets the value of the approvalList property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCard.ApprovalList }
     *     
     */
    public PaymentCard.ApprovalList getApprovalList() {
        return approvalList;
    }

    /**
     * Sets the value of the approvalList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCard.ApprovalList }
     *     
     */
    public void setApprovalList(PaymentCard.ApprovalList value) {
        this.approvalList = value;
    }

    /**
     * Gets the value of the deferredPaymentID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeferredPaymentID() {
        return deferredPaymentID;
    }

    /**
     * Sets the value of the deferredPaymentID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeferredPaymentID(String value) {
        this.deferredPaymentID = value;
    }

    /**
     * Gets the value of the defaultExtendPayment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDefaultExtendPayment() {
        return defaultExtendPayment;
    }

    /**
     * Sets the value of the defaultExtendPayment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDefaultExtendPayment(Boolean value) {
        this.defaultExtendPayment = value;
    }

    /**
     * Gets the value of the suppressFromInvoice property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuppressFromInvoice() {
        return suppressFromInvoice;
    }

    /**
     * Sets the value of the suppressFromInvoice property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuppressFromInvoice(Boolean value) {
        this.suppressFromInvoice = value;
    }

    /**
     * Gets the value of the generateApprovalAtTicketing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGenerateApprovalAtTicketing() {
        return generateApprovalAtTicketing;
    }

    /**
     * Sets the value of the generateApprovalAtTicketing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGenerateApprovalAtTicketing(Boolean value) {
        this.generateApprovalAtTicketing = value;
    }

    /**
     * Gets the value of the cscResultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSCResultCode() {
        return cscResultCode;
    }

    /**
     * Sets the value of the cscResultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSCResultCode(String value) {
        this.cscResultCode = value;
    }

    /**
     * Gets the value of the cscRemark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSCRemark() {
        return cscRemark;
    }

    /**
     * Sets the value of the cscRemark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSCRemark(String value) {
        this.cscRemark = value;
    }

    /**
     * Gets the value of the avsResultCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAVSResultCode() {
        return avsResultCode;
    }

    /**
     * Sets the value of the avsResultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAVSResultCode(String value) {
        this.avsResultCode = value;
    }

    /**
     * Gets the value of the avsRemark property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAVSRemark() {
        return avsRemark;
    }

    /**
     * Sets the value of the avsRemark property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAVSRemark(String value) {
        this.avsRemark = value;
    }

    /**
     * Gets the value of the bNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBNumber() {
        return bNumber;
    }

    /**
     * Sets the value of the bNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBNumber(String value) {
        this.bNumber = value;
    }

    /**
     * Gets the value of the cardHolderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardHolderName() {
        return cardHolderName;
    }

    /**
     * Sets the value of the cardHolderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardHolderName(String value) {
        this.cardHolderName = value;
    }

    /**
     * Gets the value of the csc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSC() {
        return csc;
    }

    /**
     * Sets the value of the csc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSC(String value) {
        this.csc = value;
    }

    /**
     * Gets the value of the cscValidatingCarrier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSCValidatingCarrier() {
        return cscValidatingCarrier;
    }

    /**
     * Sets the value of the cscValidatingCarrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSCValidatingCarrier(String value) {
        this.cscValidatingCarrier = value;
    }

    /**
     * Gets the value of the reconcileAsCash property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReconcileAsCash() {
        return reconcileAsCash;
    }

    /**
     * Sets the value of the reconcileAsCash property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReconcileAsCash(Boolean value) {
        this.reconcileAsCash = value;
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
     *         &lt;element name="Approval" type="{http://services.sabre.com/sp/reservation/v2_4}PaymentCardApproval" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "approval"
    })
    public static class ApprovalList {

        @XmlElement(name = "Approval")
        protected List<PaymentCardApproval> approval;

        /**
         * Gets the value of the approval property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the approval property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getApproval().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PaymentCardApproval }
         * 
         * 
         */
        public List<PaymentCardApproval> getApproval() {
            if (approval == null) {
                approval = new ArrayList<PaymentCardApproval>();
            }
            return this.approval;
        }

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
     *       &lt;attribute name="tokenized" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="masked" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="token" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
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
    public static class CardNumber {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "tokenized")
        protected Boolean tokenized;
        @XmlAttribute(name = "masked")
        protected Boolean masked;
        @XmlAttribute(name = "token")
        protected String token;

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
         * Gets the value of the tokenized property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isTokenized() {
            return tokenized;
        }

        /**
         * Sets the value of the tokenized property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setTokenized(Boolean value) {
            this.tokenized = value;
        }

        /**
         * Gets the value of the masked property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isMasked() {
            return masked;
        }

        /**
         * Sets the value of the masked property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setMasked(Boolean value) {
            this.masked = value;
        }

        /**
         * Gets the value of the token property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getToken() {
            return token;
        }

        /**
         * Sets the value of the token property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setToken(String value) {
            this.token = value;
        }

    }

}
