
package com.carnect.schemas.message;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opentravel.org/OTA/2003/05}OtaResponse">
 *       &lt;sequence>
 *         &lt;element name="Success" type="{http://www.opentravel.org/OTA/2003/05}SuccessType" minOccurs="0"/>
 *         &lt;element name="Warnings" type="{http://www.opentravel.org/OTA/2003/05}ArrayOfWarningType" minOccurs="0"/>
 *         &lt;element name="VehRentalCore" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleRentalCoreType">
 *                 &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
 *                 &lt;attribute name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="CompanyShortName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="TravelSector" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="Code" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="CodeContext" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Vehicle" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleType">
 *                 &lt;sequence>
 *                   &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="LowMileage" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                 &lt;attribute name="HighMileage" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RentalRate" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleRentalRateType">
 *                 &lt;attribute name="WrittenConfInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TotalCharge" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="RateTotalAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                 &lt;attribute name="EstimatedTotalAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="RateRules" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="AdvanceBooking" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
 *                           &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                           &lt;attribute name="OffsetDropTime">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="BeforeArrival"/>
 *                                 &lt;enumeration value="AfterBooking"/>
 *                                 &lt;enumeration value="AfterConfirmation"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="RequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="PickupReturnRules" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="EarliestPickup" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
 *                                     &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="LatestPickup" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
 *                                     &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="LatestReturn" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
 *                                     &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="RateGuarantee" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
 *                           &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                           &lt;attribute name="OffsetDropTime">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="BeforeArrival"/>
 *                                 &lt;enumeration value="AfterBooking"/>
 *                                 &lt;enumeration value="AfterConfirmation"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="PaymentRules" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://www.opentravel.org/OTA/2003/05}PaymentRulesType">
 *                           &lt;sequence>
 *                             &lt;element name="AcceptablePayments" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="AcceptablePayment" maxOccurs="unbounded" minOccurs="0">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="CreditCardCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                     &lt;attribute name="PaymentTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/extension>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="CancelPenaltyInfo" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="Deadline" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
 *                                     &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                                     &lt;attribute name="OffsetDropTime">
 *                                       &lt;simpleType>
 *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                           &lt;enumeration value="BeforeArrival"/>
 *                                           &lt;enumeration value="AfterBooking"/>
 *                                           &lt;enumeration value="AfterConfirmation"/>
 *                                         &lt;/restriction>
 *                                       &lt;/simpleType>
 *                                     &lt;/attribute>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="PenaltyFee" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="GuaranteeRequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                           &lt;attribute name="ModifyPenaltyInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="RateDeposit" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
 *                           &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *                           &lt;attribute name="OffsetDropTime">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="BeforeArrival"/>
 *                                 &lt;enumeration value="AfterBooking"/>
 *                                 &lt;enumeration value="AfterConfirmation"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="DepositRequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="MinimumKeep" type="{http://www.w3.org/2001/XMLSchema}duration" />
 *                 &lt;attribute name="MaximumKeep" type="{http://www.w3.org/2001/XMLSchema}duration" />
 *                 &lt;attribute name="MaximumRental" type="{http://www.w3.org/2001/XMLSchema}duration" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="PricedEquips" type="{http://www.opentravel.org/OTA/2003/05}ArrayOfVehicleEquipmentPricedType" minOccurs="0"/>
 *         &lt;element name="Fees" type="{http://www.opentravel.org/OTA/2003/05}ArrayOfVehicleChargePurposeType1" minOccurs="0"/>
 *         &lt;element name="PricedCoverages" type="{http://www.opentravel.org/OTA/2003/05}ArrayOfCoveragePricedType" minOccurs="0"/>
 *         &lt;element name="LocationDetails" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleLocationDetailsType">
 *                 &lt;attribute name="DropOffIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="VendorMessages" type="{http://www.opentravel.org/OTA/2003/05}ArrayOfVendorMessageType" minOccurs="0"/>
 *         &lt;element name="TPA_Extensions" type="{http://www.opentravel.org/OTA/2003/05}TPA_ExtensionsType" minOccurs="0"/>
 *         &lt;element name="Errors" type="{http://www.opentravel.org/OTA/2003/05}ArrayOfErrorType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="EchoToken" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="TimeStamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="Target" default="Production">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Test"/>
 *             &lt;enumeration value="Production"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="Version" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *       &lt;attribute name="TransactionIdentifier" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="SequenceNmbr" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="TransactionStatusCode">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="Start"/>
 *             &lt;enumeration value="End"/>
 *             &lt;enumeration value="Rollback"/>
 *             &lt;enumeration value="InSeries"/>
 *             &lt;enumeration value="Continuation"/>
 *             &lt;enumeration value="Subsequent"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="RetransmissionIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "success",
    "warnings",
    "vehRentalCore",
    "vehicle",
    "rentalRate",
    "totalCharge",
    "rateRules",
    "pricedEquips",
    "fees",
    "pricedCoverages",
    "locationDetails",
    "vendorMessages",
    "tpaExtensions",
    "errors"
})
@XmlRootElement(name = "VehRateRuleRS")
public class VehRateRuleRS
    extends OtaResponse
{

    @XmlElement(name = "Success")
    protected SuccessType success;
    @XmlElement(name = "Warnings")
    protected ArrayOfWarningType warnings;
    @XmlElement(name = "VehRentalCore")
    protected VehRentalCore vehRentalCore;
    @XmlElement(name = "Vehicle")
    protected Vehicle vehicle;
    @XmlElement(name = "RentalRate")
    protected List<RentalRate> rentalRate;
    @XmlElement(name = "TotalCharge")
    protected TotalCharge totalCharge;
    @XmlElement(name = "RateRules")
    protected RateRules rateRules;
    @XmlElement(name = "PricedEquips")
    protected ArrayOfVehicleEquipmentPricedType pricedEquips;
    @XmlElement(name = "Fees")
    protected ArrayOfVehicleChargePurposeType1 fees;
    @XmlElement(name = "PricedCoverages")
    protected ArrayOfCoveragePricedType pricedCoverages;
    @XmlElement(name = "LocationDetails")
    protected List<LocationDetails> locationDetails;
    @XmlElement(name = "VendorMessages")
    protected ArrayOfVendorMessageType vendorMessages;
    @XmlElement(name = "TPA_Extensions")
    protected TPAExtensionsType tpaExtensions;
    @XmlElement(name = "Errors")
    protected ArrayOfErrorType errors;
    @XmlAttribute(name = "EchoToken")
    protected String echoToken;
    @XmlAttribute(name = "TimeStamp")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timeStamp;
    @XmlAttribute(name = "Target")
    protected String target;
    @XmlAttribute(name = "Version", required = true)
    protected BigDecimal version;
    @XmlAttribute(name = "TransactionIdentifier")
    protected String transactionIdentifier;
    @XmlAttribute(name = "SequenceNmbr")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sequenceNmbr;
    @XmlAttribute(name = "TransactionStatusCode")
    protected String transactionStatusCode;
    @XmlAttribute(name = "RetransmissionIndicator")
    protected Boolean retransmissionIndicator;

    /**
     * Gets the value of the success property.
     * 
     * @return
     *     possible object is
     *     {@link SuccessType }
     *     
     */
    public SuccessType getSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     * @param value
     *     allowed object is
     *     {@link SuccessType }
     *     
     */
    public void setSuccess(SuccessType value) {
        this.success = value;
    }

    /**
     * Gets the value of the warnings property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWarningType }
     *     
     */
    public ArrayOfWarningType getWarnings() {
        return warnings;
    }

    /**
     * Sets the value of the warnings property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWarningType }
     *     
     */
    public void setWarnings(ArrayOfWarningType value) {
        this.warnings = value;
    }

    /**
     * Gets the value of the vehRentalCore property.
     * 
     * @return
     *     possible object is
     *     {@link VehRentalCore }
     *     
     */
    public VehRentalCore getVehRentalCore() {
        return vehRentalCore;
    }

    /**
     * Sets the value of the vehRentalCore property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehRentalCore }
     *     
     */
    public void setVehRentalCore(VehRentalCore value) {
        this.vehRentalCore = value;
    }

    /**
     * Gets the value of the vehicle property.
     * 
     * @return
     *     possible object is
     *     {@link Vehicle }
     *     
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * Sets the value of the vehicle property.
     * 
     * @param value
     *     allowed object is
     *     {@link Vehicle }
     *     
     */
    public void setVehicle(Vehicle value) {
        this.vehicle = value;
    }

    /**
     * Gets the value of the rentalRate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rentalRate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRentalRate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RentalRate }
     * 
     * 
     */
    public List<RentalRate> getRentalRate() {
        if (rentalRate == null) {
            rentalRate = new ArrayList<RentalRate>();
        }
        return this.rentalRate;
    }

    /**
     * Gets the value of the totalCharge property.
     * 
     * @return
     *     possible object is
     *     {@link TotalCharge }
     *     
     */
    public TotalCharge getTotalCharge() {
        return totalCharge;
    }

    /**
     * Sets the value of the totalCharge property.
     * 
     * @param value
     *     allowed object is
     *     {@link TotalCharge }
     *     
     */
    public void setTotalCharge(TotalCharge value) {
        this.totalCharge = value;
    }

    /**
     * Gets the value of the rateRules property.
     * 
     * @return
     *     possible object is
     *     {@link RateRules }
     *     
     */
    public RateRules getRateRules() {
        return rateRules;
    }

    /**
     * Sets the value of the rateRules property.
     * 
     * @param value
     *     allowed object is
     *     {@link RateRules }
     *     
     */
    public void setRateRules(RateRules value) {
        this.rateRules = value;
    }

    /**
     * Gets the value of the pricedEquips property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVehicleEquipmentPricedType }
     *     
     */
    public ArrayOfVehicleEquipmentPricedType getPricedEquips() {
        return pricedEquips;
    }

    /**
     * Sets the value of the pricedEquips property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVehicleEquipmentPricedType }
     *     
     */
    public void setPricedEquips(ArrayOfVehicleEquipmentPricedType value) {
        this.pricedEquips = value;
    }

    /**
     * Gets the value of the fees property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVehicleChargePurposeType1 }
     *     
     */
    public ArrayOfVehicleChargePurposeType1 getFees() {
        return fees;
    }

    /**
     * Sets the value of the fees property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVehicleChargePurposeType1 }
     *     
     */
    public void setFees(ArrayOfVehicleChargePurposeType1 value) {
        this.fees = value;
    }

    /**
     * Gets the value of the pricedCoverages property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCoveragePricedType }
     *     
     */
    public ArrayOfCoveragePricedType getPricedCoverages() {
        return pricedCoverages;
    }

    /**
     * Sets the value of the pricedCoverages property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCoveragePricedType }
     *     
     */
    public void setPricedCoverages(ArrayOfCoveragePricedType value) {
        this.pricedCoverages = value;
    }

    /**
     * Gets the value of the locationDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the locationDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocationDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocationDetails }
     * 
     * 
     */
    public List<LocationDetails> getLocationDetails() {
        if (locationDetails == null) {
            locationDetails = new ArrayList<LocationDetails>();
        }
        return this.locationDetails;
    }

    /**
     * Gets the value of the vendorMessages property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfVendorMessageType }
     *     
     */
    public ArrayOfVendorMessageType getVendorMessages() {
        return vendorMessages;
    }

    /**
     * Sets the value of the vendorMessages property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfVendorMessageType }
     *     
     */
    public void setVendorMessages(ArrayOfVendorMessageType value) {
        this.vendorMessages = value;
    }

    /**
     * Gets the value of the tpaExtensions property.
     * 
     * @return
     *     possible object is
     *     {@link TPAExtensionsType }
     *     
     */
    public TPAExtensionsType getTPAExtensions() {
        return tpaExtensions;
    }

    /**
     * Sets the value of the tpaExtensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TPAExtensionsType }
     *     
     */
    public void setTPAExtensions(TPAExtensionsType value) {
        this.tpaExtensions = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfErrorType }
     *     
     */
    public ArrayOfErrorType getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfErrorType }
     *     
     */
    public void setErrors(ArrayOfErrorType value) {
        this.errors = value;
    }

    /**
     * Gets the value of the echoToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEchoToken() {
        return echoToken;
    }

    /**
     * Sets the value of the echoToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEchoToken(String value) {
        this.echoToken = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimeStamp(XMLGregorianCalendar value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the target property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTarget() {
        if (target == null) {
            return "Production";
        } else {
            return target;
        }
    }

    /**
     * Sets the value of the target property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTarget(String value) {
        this.target = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVersion(BigDecimal value) {
        this.version = value;
    }

    /**
     * Gets the value of the transactionIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionIdentifier() {
        return transactionIdentifier;
    }

    /**
     * Sets the value of the transactionIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionIdentifier(String value) {
        this.transactionIdentifier = value;
    }

    /**
     * Gets the value of the sequenceNmbr property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNmbr() {
        return sequenceNmbr;
    }

    /**
     * Sets the value of the sequenceNmbr property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNmbr(BigInteger value) {
        this.sequenceNmbr = value;
    }

    /**
     * Gets the value of the transactionStatusCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionStatusCode() {
        return transactionStatusCode;
    }

    /**
     * Sets the value of the transactionStatusCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionStatusCode(String value) {
        this.transactionStatusCode = value;
    }

    /**
     * Gets the value of the retransmissionIndicator property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRetransmissionIndicator() {
        return retransmissionIndicator;
    }

    /**
     * Sets the value of the retransmissionIndicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRetransmissionIndicator(Boolean value) {
        this.retransmissionIndicator = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleLocationDetailsType">
     *       &lt;attribute name="DropOffIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class LocationDetails
        extends VehicleLocationDetailsType
    {

        @XmlAttribute(name = "DropOffIndicator")
        protected Boolean dropOffIndicator;

        /**
         * Gets the value of the dropOffIndicator property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isDropOffIndicator() {
            return dropOffIndicator;
        }

        /**
         * Sets the value of the dropOffIndicator property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setDropOffIndicator(Boolean value) {
            this.dropOffIndicator = value;
        }

    }


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
     *         &lt;element name="AdvanceBooking" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
     *                 &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *                 &lt;attribute name="OffsetDropTime">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="BeforeArrival"/>
     *                       &lt;enumeration value="AfterBooking"/>
     *                       &lt;enumeration value="AfterConfirmation"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="RequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="PickupReturnRules" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="EarliestPickup" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
     *                           &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="LatestPickup" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
     *                           &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="LatestReturn" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
     *                           &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="RateGuarantee" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
     *                 &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *                 &lt;attribute name="OffsetDropTime">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="BeforeArrival"/>
     *                       &lt;enumeration value="AfterBooking"/>
     *                       &lt;enumeration value="AfterConfirmation"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="PaymentRules" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://www.opentravel.org/OTA/2003/05}PaymentRulesType">
     *                 &lt;sequence>
     *                   &lt;element name="AcceptablePayments" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="AcceptablePayment" maxOccurs="unbounded" minOccurs="0">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="CreditCardCode" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                           &lt;attribute name="PaymentTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/extension>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="CancelPenaltyInfo" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="Deadline" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
     *                           &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *                           &lt;attribute name="OffsetDropTime">
     *                             &lt;simpleType>
     *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                 &lt;enumeration value="BeforeArrival"/>
     *                                 &lt;enumeration value="AfterBooking"/>
     *                                 &lt;enumeration value="AfterConfirmation"/>
     *                               &lt;/restriction>
     *                             &lt;/simpleType>
     *                           &lt;/attribute>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="PenaltyFee" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="GuaranteeRequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *                 &lt;attribute name="ModifyPenaltyInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="RateDeposit" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
     *                 &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *                 &lt;attribute name="OffsetDropTime">
     *                   &lt;simpleType>
     *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                       &lt;enumeration value="BeforeArrival"/>
     *                       &lt;enumeration value="AfterBooking"/>
     *                       &lt;enumeration value="AfterConfirmation"/>
     *                     &lt;/restriction>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="DepositRequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="MinimumKeep" type="{http://www.w3.org/2001/XMLSchema}duration" />
     *       &lt;attribute name="MaximumKeep" type="{http://www.w3.org/2001/XMLSchema}duration" />
     *       &lt;attribute name="MaximumRental" type="{http://www.w3.org/2001/XMLSchema}duration" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "advanceBooking",
        "pickupReturnRules",
        "rateGuarantee",
        "paymentRules",
        "cancelPenaltyInfo",
        "rateDeposit"
    })
    public static class RateRules {

        @XmlElement(name = "AdvanceBooking")
        protected AdvanceBooking advanceBooking;
        @XmlElement(name = "PickupReturnRules")
        protected PickupReturnRules pickupReturnRules;
        @XmlElement(name = "RateGuarantee")
        protected RateGuarantee rateGuarantee;
        @XmlElement(name = "PaymentRules")
        protected PaymentRules paymentRules;
        @XmlElement(name = "CancelPenaltyInfo")
        protected List<CancelPenaltyInfo> cancelPenaltyInfo;
        @XmlElement(name = "RateDeposit")
        protected RateDeposit rateDeposit;
        @XmlAttribute(name = "MinimumKeep")
        protected Duration minimumKeep;
        @XmlAttribute(name = "MaximumKeep")
        protected Duration maximumKeep;
        @XmlAttribute(name = "MaximumRental")
        protected Duration maximumRental;

        /**
         * Gets the value of the advanceBooking property.
         * 
         * @return
         *     possible object is
         *     {@link AdvanceBooking }
         *     
         */
        public AdvanceBooking getAdvanceBooking() {
            return advanceBooking;
        }

        /**
         * Sets the value of the advanceBooking property.
         * 
         * @param value
         *     allowed object is
         *     {@link AdvanceBooking }
         *     
         */
        public void setAdvanceBooking(AdvanceBooking value) {
            this.advanceBooking = value;
        }

        /**
         * Gets the value of the pickupReturnRules property.
         * 
         * @return
         *     possible object is
         *     {@link PickupReturnRules }
         *     
         */
        public PickupReturnRules getPickupReturnRules() {
            return pickupReturnRules;
        }

        /**
         * Sets the value of the pickupReturnRules property.
         * 
         * @param value
         *     allowed object is
         *     {@link PickupReturnRules }
         *     
         */
        public void setPickupReturnRules(PickupReturnRules value) {
            this.pickupReturnRules = value;
        }

        /**
         * Gets the value of the rateGuarantee property.
         * 
         * @return
         *     possible object is
         *     {@link RateGuarantee }
         *     
         */
        public RateGuarantee getRateGuarantee() {
            return rateGuarantee;
        }

        /**
         * Sets the value of the rateGuarantee property.
         * 
         * @param value
         *     allowed object is
         *     {@link RateGuarantee }
         *     
         */
        public void setRateGuarantee(RateGuarantee value) {
            this.rateGuarantee = value;
        }

        /**
         * Gets the value of the paymentRules property.
         * 
         * @return
         *     possible object is
         *     {@link PaymentRules }
         *     
         */
        public PaymentRules getPaymentRules() {
            return paymentRules;
        }

        /**
         * Sets the value of the paymentRules property.
         * 
         * @param value
         *     allowed object is
         *     {@link PaymentRules }
         *     
         */
        public void setPaymentRules(PaymentRules value) {
            this.paymentRules = value;
        }

        /**
         * Gets the value of the cancelPenaltyInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the cancelPenaltyInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCancelPenaltyInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CancelPenaltyInfo }
         * 
         * 
         */
        public List<CancelPenaltyInfo> getCancelPenaltyInfo() {
            if (cancelPenaltyInfo == null) {
                cancelPenaltyInfo = new ArrayList<CancelPenaltyInfo>();
            }
            return this.cancelPenaltyInfo;
        }

        /**
         * Gets the value of the rateDeposit property.
         * 
         * @return
         *     possible object is
         *     {@link RateDeposit }
         *     
         */
        public RateDeposit getRateDeposit() {
            return rateDeposit;
        }

        /**
         * Sets the value of the rateDeposit property.
         * 
         * @param value
         *     allowed object is
         *     {@link RateDeposit }
         *     
         */
        public void setRateDeposit(RateDeposit value) {
            this.rateDeposit = value;
        }

        /**
         * Gets the value of the minimumKeep property.
         * 
         * @return
         *     possible object is
         *     {@link Duration }
         *     
         */
        public Duration getMinimumKeep() {
            return minimumKeep;
        }

        /**
         * Sets the value of the minimumKeep property.
         * 
         * @param value
         *     allowed object is
         *     {@link Duration }
         *     
         */
        public void setMinimumKeep(Duration value) {
            this.minimumKeep = value;
        }

        /**
         * Gets the value of the maximumKeep property.
         * 
         * @return
         *     possible object is
         *     {@link Duration }
         *     
         */
        public Duration getMaximumKeep() {
            return maximumKeep;
        }

        /**
         * Sets the value of the maximumKeep property.
         * 
         * @param value
         *     allowed object is
         *     {@link Duration }
         *     
         */
        public void setMaximumKeep(Duration value) {
            this.maximumKeep = value;
        }

        /**
         * Gets the value of the maximumRental property.
         * 
         * @return
         *     possible object is
         *     {@link Duration }
         *     
         */
        public Duration getMaximumRental() {
            return maximumRental;
        }

        /**
         * Sets the value of the maximumRental property.
         * 
         * @param value
         *     allowed object is
         *     {@link Duration }
         *     
         */
        public void setMaximumRental(Duration value) {
            this.maximumRental = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
         *       &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
         *       &lt;attribute name="OffsetDropTime">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="BeforeArrival"/>
         *             &lt;enumeration value="AfterBooking"/>
         *             &lt;enumeration value="AfterConfirmation"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="RequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class AdvanceBooking {

            @XmlAttribute(name = "AbsoluteDeadline")
            protected String absoluteDeadline;
            @XmlAttribute(name = "OffsetTimeUnit")
            protected TimeUnitType offsetTimeUnit;
            @XmlAttribute(name = "OffsetUnitMultiplier")
            protected BigInteger offsetUnitMultiplier;
            @XmlAttribute(name = "OffsetDropTime")
            protected String offsetDropTime;
            @XmlAttribute(name = "RequiredInd")
            protected Boolean requiredInd;

            /**
             * Gets the value of the absoluteDeadline property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAbsoluteDeadline() {
                return absoluteDeadline;
            }

            /**
             * Sets the value of the absoluteDeadline property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAbsoluteDeadline(String value) {
                this.absoluteDeadline = value;
            }

            /**
             * Gets the value of the offsetTimeUnit property.
             * 
             * @return
             *     possible object is
             *     {@link TimeUnitType }
             *     
             */
            public TimeUnitType getOffsetTimeUnit() {
                return offsetTimeUnit;
            }

            /**
             * Sets the value of the offsetTimeUnit property.
             * 
             * @param value
             *     allowed object is
             *     {@link TimeUnitType }
             *     
             */
            public void setOffsetTimeUnit(TimeUnitType value) {
                this.offsetTimeUnit = value;
            }

            /**
             * Gets the value of the offsetUnitMultiplier property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getOffsetUnitMultiplier() {
                return offsetUnitMultiplier;
            }

            /**
             * Sets the value of the offsetUnitMultiplier property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setOffsetUnitMultiplier(BigInteger value) {
                this.offsetUnitMultiplier = value;
            }

            /**
             * Gets the value of the offsetDropTime property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOffsetDropTime() {
                return offsetDropTime;
            }

            /**
             * Sets the value of the offsetDropTime property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOffsetDropTime(String value) {
                this.offsetDropTime = value;
            }

            /**
             * Gets the value of the requiredInd property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isRequiredInd() {
                return requiredInd;
            }

            /**
             * Sets the value of the requiredInd property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setRequiredInd(Boolean value) {
                this.requiredInd = value;
            }

        }


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
         *         &lt;element name="Deadline" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
         *                 &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
         *                 &lt;attribute name="OffsetDropTime">
         *                   &lt;simpleType>
         *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                       &lt;enumeration value="BeforeArrival"/>
         *                       &lt;enumeration value="AfterBooking"/>
         *                       &lt;enumeration value="AfterConfirmation"/>
         *                     &lt;/restriction>
         *                   &lt;/simpleType>
         *                 &lt;/attribute>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="PenaltyFee" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
         *       &lt;/sequence>
         *       &lt;attribute name="GuaranteeRequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *       &lt;attribute name="ModifyPenaltyInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "deadline",
            "penaltyFee",
            "description"
        })
        public static class CancelPenaltyInfo {

            @XmlElement(name = "Deadline")
            protected List<Deadline> deadline;
            @XmlElement(name = "PenaltyFee")
            protected PenaltyFee penaltyFee;
            @XmlElement(name = "Description")
            protected FormattedTextTextType description;
            @XmlAttribute(name = "GuaranteeRequiredInd")
            protected Boolean guaranteeRequiredInd;
            @XmlAttribute(name = "ModifyPenaltyInd")
            protected Boolean modifyPenaltyInd;

            /**
             * Gets the value of the deadline property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the deadline property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getDeadline().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Deadline }
             * 
             * 
             */
            public List<Deadline> getDeadline() {
                if (deadline == null) {
                    deadline = new ArrayList<Deadline>();
                }
                return this.deadline;
            }

            /**
             * Gets the value of the penaltyFee property.
             * 
             * @return
             *     possible object is
             *     {@link PenaltyFee }
             *     
             */
            public PenaltyFee getPenaltyFee() {
                return penaltyFee;
            }

            /**
             * Sets the value of the penaltyFee property.
             * 
             * @param value
             *     allowed object is
             *     {@link PenaltyFee }
             *     
             */
            public void setPenaltyFee(PenaltyFee value) {
                this.penaltyFee = value;
            }

            /**
             * Gets the value of the description property.
             * 
             * @return
             *     possible object is
             *     {@link FormattedTextTextType }
             *     
             */
            public FormattedTextTextType getDescription() {
                return description;
            }

            /**
             * Sets the value of the description property.
             * 
             * @param value
             *     allowed object is
             *     {@link FormattedTextTextType }
             *     
             */
            public void setDescription(FormattedTextTextType value) {
                this.description = value;
            }

            /**
             * Gets the value of the guaranteeRequiredInd property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isGuaranteeRequiredInd() {
                return guaranteeRequiredInd;
            }

            /**
             * Sets the value of the guaranteeRequiredInd property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setGuaranteeRequiredInd(Boolean value) {
                this.guaranteeRequiredInd = value;
            }

            /**
             * Gets the value of the modifyPenaltyInd property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isModifyPenaltyInd() {
                return modifyPenaltyInd;
            }

            /**
             * Sets the value of the modifyPenaltyInd property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setModifyPenaltyInd(Boolean value) {
                this.modifyPenaltyInd = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
             *       &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
             *       &lt;attribute name="OffsetDropTime">
             *         &lt;simpleType>
             *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *             &lt;enumeration value="BeforeArrival"/>
             *             &lt;enumeration value="AfterBooking"/>
             *             &lt;enumeration value="AfterConfirmation"/>
             *           &lt;/restriction>
             *         &lt;/simpleType>
             *       &lt;/attribute>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Deadline {

                @XmlAttribute(name = "AbsoluteDeadline")
                protected String absoluteDeadline;
                @XmlAttribute(name = "OffsetTimeUnit")
                protected TimeUnitType offsetTimeUnit;
                @XmlAttribute(name = "OffsetUnitMultiplier")
                protected BigInteger offsetUnitMultiplier;
                @XmlAttribute(name = "OffsetDropTime")
                protected String offsetDropTime;

                /**
                 * Gets the value of the absoluteDeadline property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getAbsoluteDeadline() {
                    return absoluteDeadline;
                }

                /**
                 * Sets the value of the absoluteDeadline property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setAbsoluteDeadline(String value) {
                    this.absoluteDeadline = value;
                }

                /**
                 * Gets the value of the offsetTimeUnit property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link TimeUnitType }
                 *     
                 */
                public TimeUnitType getOffsetTimeUnit() {
                    return offsetTimeUnit;
                }

                /**
                 * Sets the value of the offsetTimeUnit property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link TimeUnitType }
                 *     
                 */
                public void setOffsetTimeUnit(TimeUnitType value) {
                    this.offsetTimeUnit = value;
                }

                /**
                 * Gets the value of the offsetUnitMultiplier property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigInteger }
                 *     
                 */
                public BigInteger getOffsetUnitMultiplier() {
                    return offsetUnitMultiplier;
                }

                /**
                 * Sets the value of the offsetUnitMultiplier property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigInteger }
                 *     
                 */
                public void setOffsetUnitMultiplier(BigInteger value) {
                    this.offsetUnitMultiplier = value;
                }

                /**
                 * Gets the value of the offsetDropTime property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getOffsetDropTime() {
                    return offsetDropTime;
                }

                /**
                 * Sets the value of the offsetDropTime property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setOffsetDropTime(String value) {
                    this.offsetDropTime = value;
                }

            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="Amount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class PenaltyFee {

                @XmlAttribute(name = "Amount")
                protected BigDecimal amount;

                /**
                 * Gets the value of the amount property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link BigDecimal }
                 *     
                 */
                public BigDecimal getAmount() {
                    return amount;
                }

                /**
                 * Sets the value of the amount property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link BigDecimal }
                 *     
                 */
                public void setAmount(BigDecimal value) {
                    this.amount = value;
                }

            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{http://www.opentravel.org/OTA/2003/05}PaymentRulesType">
         *       &lt;sequence>
         *         &lt;element name="AcceptablePayments" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="AcceptablePayment" maxOccurs="unbounded" minOccurs="0">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="CreditCardCode" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *                 &lt;attribute name="PaymentTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "acceptablePayments"
        })
        public static class PaymentRules
            extends PaymentRulesType
        {

            @XmlElement(name = "AcceptablePayments")
            protected List<AcceptablePayments> acceptablePayments;

            /**
             * Gets the value of the acceptablePayments property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the acceptablePayments property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAcceptablePayments().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link AcceptablePayments }
             * 
             * 
             */
            public List<AcceptablePayments> getAcceptablePayments() {
                if (acceptablePayments == null) {
                    acceptablePayments = new ArrayList<AcceptablePayments>();
                }
                return this.acceptablePayments;
            }


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
             *         &lt;element name="AcceptablePayment" maxOccurs="unbounded" minOccurs="0">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="CreditCardCode" type="{http://www.w3.org/2001/XMLSchema}string" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *       &lt;attribute name="PaymentTypeCode" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "acceptablePayment"
            })
            public static class AcceptablePayments {

                @XmlElement(name = "AcceptablePayment")
                protected List<AcceptablePayment> acceptablePayment;
                @XmlAttribute(name = "PaymentTypeCode")
                protected String paymentTypeCode;

                /**
                 * Gets the value of the acceptablePayment property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the acceptablePayment property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getAcceptablePayment().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link AcceptablePayment }
                 * 
                 * 
                 */
                public List<AcceptablePayment> getAcceptablePayment() {
                    if (acceptablePayment == null) {
                        acceptablePayment = new ArrayList<AcceptablePayment>();
                    }
                    return this.acceptablePayment;
                }

                /**
                 * Gets the value of the paymentTypeCode property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getPaymentTypeCode() {
                    return paymentTypeCode;
                }

                /**
                 * Sets the value of the paymentTypeCode property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setPaymentTypeCode(String value) {
                    this.paymentTypeCode = value;
                }


                /**
                 * <p>Java class for anonymous complex type.
                 * 
                 * <p>The following schema fragment specifies the expected content contained within this class.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;attribute name="CreditCardCode" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class AcceptablePayment {

                    @XmlAttribute(name = "CreditCardCode")
                    protected String creditCardCode;

                    /**
                     * Gets the value of the creditCardCode property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getCreditCardCode() {
                        return creditCardCode;
                    }

                    /**
                     * Sets the value of the creditCardCode property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setCreditCardCode(String value) {
                        this.creditCardCode = value;
                    }

                }

            }

        }


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
         *         &lt;element name="EarliestPickup" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
         *                 &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="LatestPickup" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
         *                 &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="LatestReturn" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
         *                 &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
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
            "earliestPickup",
            "latestPickup",
            "latestReturn"
        })
        public static class PickupReturnRules {

            @XmlElement(name = "EarliestPickup")
            protected EarliestPickup earliestPickup;
            @XmlElement(name = "LatestPickup")
            protected LatestPickup latestPickup;
            @XmlElement(name = "LatestReturn")
            protected LatestReturn latestReturn;

            /**
             * Gets the value of the earliestPickup property.
             * 
             * @return
             *     possible object is
             *     {@link EarliestPickup }
             *     
             */
            public EarliestPickup getEarliestPickup() {
                return earliestPickup;
            }

            /**
             * Sets the value of the earliestPickup property.
             * 
             * @param value
             *     allowed object is
             *     {@link EarliestPickup }
             *     
             */
            public void setEarliestPickup(EarliestPickup value) {
                this.earliestPickup = value;
            }

            /**
             * Gets the value of the latestPickup property.
             * 
             * @return
             *     possible object is
             *     {@link LatestPickup }
             *     
             */
            public LatestPickup getLatestPickup() {
                return latestPickup;
            }

            /**
             * Sets the value of the latestPickup property.
             * 
             * @param value
             *     allowed object is
             *     {@link LatestPickup }
             *     
             */
            public void setLatestPickup(LatestPickup value) {
                this.latestPickup = value;
            }

            /**
             * Gets the value of the latestReturn property.
             * 
             * @return
             *     possible object is
             *     {@link LatestReturn }
             *     
             */
            public LatestReturn getLatestReturn() {
                return latestReturn;
            }

            /**
             * Sets the value of the latestReturn property.
             * 
             * @param value
             *     allowed object is
             *     {@link LatestReturn }
             *     
             */
            public void setLatestReturn(LatestReturn value) {
                this.latestReturn = value;
            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
             *       &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class EarliestPickup {

                @XmlAttribute(name = "DayOfWeek")
                protected DayOfWeekType dayOfWeek;
                @XmlAttribute(name = "Time")
                protected String time;

                /**
                 * Gets the value of the dayOfWeek property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link DayOfWeekType }
                 *     
                 */
                public DayOfWeekType getDayOfWeek() {
                    return dayOfWeek;
                }

                /**
                 * Sets the value of the dayOfWeek property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link DayOfWeekType }
                 *     
                 */
                public void setDayOfWeek(DayOfWeekType value) {
                    this.dayOfWeek = value;
                }

                /**
                 * Gets the value of the time property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getTime() {
                    return time;
                }

                /**
                 * Sets the value of the time property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setTime(String value) {
                    this.time = value;
                }

            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
             *       &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class LatestPickup {

                @XmlAttribute(name = "DayOfWeek")
                protected DayOfWeekType dayOfWeek;
                @XmlAttribute(name = "Time")
                protected String time;

                /**
                 * Gets the value of the dayOfWeek property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link DayOfWeekType }
                 *     
                 */
                public DayOfWeekType getDayOfWeek() {
                    return dayOfWeek;
                }

                /**
                 * Sets the value of the dayOfWeek property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link DayOfWeekType }
                 *     
                 */
                public void setDayOfWeek(DayOfWeekType value) {
                    this.dayOfWeek = value;
                }

                /**
                 * Gets the value of the time property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getTime() {
                    return time;
                }

                /**
                 * Sets the value of the time property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setTime(String value) {
                    this.time = value;
                }

            }


            /**
             * <p>Java class for anonymous complex type.
             * 
             * <p>The following schema fragment specifies the expected content contained within this class.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
             *       &lt;attribute name="Time" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class LatestReturn {

                @XmlAttribute(name = "DayOfWeek")
                protected DayOfWeekType dayOfWeek;
                @XmlAttribute(name = "Time")
                protected String time;

                /**
                 * Gets the value of the dayOfWeek property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link DayOfWeekType }
                 *     
                 */
                public DayOfWeekType getDayOfWeek() {
                    return dayOfWeek;
                }

                /**
                 * Sets the value of the dayOfWeek property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link DayOfWeekType }
                 *     
                 */
                public void setDayOfWeek(DayOfWeekType value) {
                    this.dayOfWeek = value;
                }

                /**
                 * Gets the value of the time property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getTime() {
                    return time;
                }

                /**
                 * Sets the value of the time property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setTime(String value) {
                    this.time = value;
                }

            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
         *       &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
         *       &lt;attribute name="OffsetDropTime">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="BeforeArrival"/>
         *             &lt;enumeration value="AfterBooking"/>
         *             &lt;enumeration value="AfterConfirmation"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="DepositRequiredInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class RateDeposit {

            @XmlAttribute(name = "AbsoluteDeadline")
            protected String absoluteDeadline;
            @XmlAttribute(name = "OffsetTimeUnit")
            protected TimeUnitType offsetTimeUnit;
            @XmlAttribute(name = "OffsetUnitMultiplier")
            protected BigInteger offsetUnitMultiplier;
            @XmlAttribute(name = "OffsetDropTime")
            protected String offsetDropTime;
            @XmlAttribute(name = "DepositRequiredInd")
            protected Boolean depositRequiredInd;

            /**
             * Gets the value of the absoluteDeadline property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAbsoluteDeadline() {
                return absoluteDeadline;
            }

            /**
             * Sets the value of the absoluteDeadline property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAbsoluteDeadline(String value) {
                this.absoluteDeadline = value;
            }

            /**
             * Gets the value of the offsetTimeUnit property.
             * 
             * @return
             *     possible object is
             *     {@link TimeUnitType }
             *     
             */
            public TimeUnitType getOffsetTimeUnit() {
                return offsetTimeUnit;
            }

            /**
             * Sets the value of the offsetTimeUnit property.
             * 
             * @param value
             *     allowed object is
             *     {@link TimeUnitType }
             *     
             */
            public void setOffsetTimeUnit(TimeUnitType value) {
                this.offsetTimeUnit = value;
            }

            /**
             * Gets the value of the offsetUnitMultiplier property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getOffsetUnitMultiplier() {
                return offsetUnitMultiplier;
            }

            /**
             * Sets the value of the offsetUnitMultiplier property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setOffsetUnitMultiplier(BigInteger value) {
                this.offsetUnitMultiplier = value;
            }

            /**
             * Gets the value of the offsetDropTime property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOffsetDropTime() {
                return offsetDropTime;
            }

            /**
             * Sets the value of the offsetDropTime property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOffsetDropTime(String value) {
                this.offsetDropTime = value;
            }

            /**
             * Gets the value of the depositRequiredInd property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isDepositRequiredInd() {
                return depositRequiredInd;
            }

            /**
             * Sets the value of the depositRequiredInd property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setDepositRequiredInd(Boolean value) {
                this.depositRequiredInd = value;
            }

        }


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
         *         &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
         *       &lt;/sequence>
         *       &lt;attribute name="AbsoluteDeadline" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="OffsetTimeUnit" type="{http://www.opentravel.org/OTA/2003/05}TimeUnitType" />
         *       &lt;attribute name="OffsetUnitMultiplier" type="{http://www.w3.org/2001/XMLSchema}integer" />
         *       &lt;attribute name="OffsetDropTime">
         *         &lt;simpleType>
         *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *             &lt;enumeration value="BeforeArrival"/>
         *             &lt;enumeration value="AfterBooking"/>
         *             &lt;enumeration value="AfterConfirmation"/>
         *           &lt;/restriction>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "description"
        })
        public static class RateGuarantee {

            @XmlElement(name = "Description")
            protected FormattedTextTextType description;
            @XmlAttribute(name = "AbsoluteDeadline")
            protected String absoluteDeadline;
            @XmlAttribute(name = "OffsetTimeUnit")
            protected TimeUnitType offsetTimeUnit;
            @XmlAttribute(name = "OffsetUnitMultiplier")
            protected BigInteger offsetUnitMultiplier;
            @XmlAttribute(name = "OffsetDropTime")
            protected String offsetDropTime;

            /**
             * Gets the value of the description property.
             * 
             * @return
             *     possible object is
             *     {@link FormattedTextTextType }
             *     
             */
            public FormattedTextTextType getDescription() {
                return description;
            }

            /**
             * Sets the value of the description property.
             * 
             * @param value
             *     allowed object is
             *     {@link FormattedTextTextType }
             *     
             */
            public void setDescription(FormattedTextTextType value) {
                this.description = value;
            }

            /**
             * Gets the value of the absoluteDeadline property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAbsoluteDeadline() {
                return absoluteDeadline;
            }

            /**
             * Sets the value of the absoluteDeadline property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAbsoluteDeadline(String value) {
                this.absoluteDeadline = value;
            }

            /**
             * Gets the value of the offsetTimeUnit property.
             * 
             * @return
             *     possible object is
             *     {@link TimeUnitType }
             *     
             */
            public TimeUnitType getOffsetTimeUnit() {
                return offsetTimeUnit;
            }

            /**
             * Sets the value of the offsetTimeUnit property.
             * 
             * @param value
             *     allowed object is
             *     {@link TimeUnitType }
             *     
             */
            public void setOffsetTimeUnit(TimeUnitType value) {
                this.offsetTimeUnit = value;
            }

            /**
             * Gets the value of the offsetUnitMultiplier property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getOffsetUnitMultiplier() {
                return offsetUnitMultiplier;
            }

            /**
             * Sets the value of the offsetUnitMultiplier property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setOffsetUnitMultiplier(BigInteger value) {
                this.offsetUnitMultiplier = value;
            }

            /**
             * Gets the value of the offsetDropTime property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOffsetDropTime() {
                return offsetDropTime;
            }

            /**
             * Sets the value of the offsetDropTime property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOffsetDropTime(String value) {
                this.offsetDropTime = value;
            }

        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleRentalRateType">
     *       &lt;attribute name="WrittenConfInd" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class RentalRate
        extends VehicleRentalRateType
    {

        @XmlAttribute(name = "WrittenConfInd")
        protected Boolean writtenConfInd;

        /**
         * Gets the value of the writtenConfInd property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isWrittenConfInd() {
            return writtenConfInd;
        }

        /**
         * Sets the value of the writtenConfInd property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setWrittenConfInd(Boolean value) {
            this.writtenConfInd = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="RateTotalAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *       &lt;attribute name="EstimatedTotalAmount" type="{http://www.w3.org/2001/XMLSchema}decimal" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class TotalCharge {

        @XmlAttribute(name = "CurrencyCode")
        protected String currencyCode;
        @XmlAttribute(name = "RateTotalAmount")
        protected BigDecimal rateTotalAmount;
        @XmlAttribute(name = "EstimatedTotalAmount")
        protected BigDecimal estimatedTotalAmount;

        /**
         * Gets the value of the currencyCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCurrencyCode() {
            return currencyCode;
        }

        /**
         * Sets the value of the currencyCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCurrencyCode(String value) {
            this.currencyCode = value;
        }

        /**
         * Gets the value of the rateTotalAmount property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getRateTotalAmount() {
            return rateTotalAmount;
        }

        /**
         * Sets the value of the rateTotalAmount property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setRateTotalAmount(BigDecimal value) {
            this.rateTotalAmount = value;
        }

        /**
         * Gets the value of the estimatedTotalAmount property.
         * 
         * @return
         *     possible object is
         *     {@link BigDecimal }
         *     
         */
        public BigDecimal getEstimatedTotalAmount() {
            return estimatedTotalAmount;
        }

        /**
         * Sets the value of the estimatedTotalAmount property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigDecimal }
         *     
         */
        public void setEstimatedTotalAmount(BigDecimal value) {
            this.estimatedTotalAmount = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleRentalCoreType">
     *       &lt;attribute name="DayOfWeek" type="{http://www.opentravel.org/OTA/2003/05}DayOfWeekType" />
     *       &lt;attribute name="CurrencyCode" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="CompanyShortName" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="TravelSector" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="Code" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="CodeContext" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class VehRentalCore
        extends VehicleRentalCoreType
    {

        @XmlAttribute(name = "DayOfWeek")
        protected DayOfWeekType dayOfWeek;
        @XmlAttribute(name = "CurrencyCode")
        protected String currencyCode;
        @XmlAttribute(name = "CompanyShortName")
        protected String companyShortName;
        @XmlAttribute(name = "TravelSector")
        protected String travelSector;
        @XmlAttribute(name = "Code")
        protected String code;
        @XmlAttribute(name = "CodeContext")
        protected String codeContext;

        /**
         * Gets the value of the dayOfWeek property.
         * 
         * @return
         *     possible object is
         *     {@link DayOfWeekType }
         *     
         */
        public DayOfWeekType getDayOfWeek() {
            return dayOfWeek;
        }

        /**
         * Sets the value of the dayOfWeek property.
         * 
         * @param value
         *     allowed object is
         *     {@link DayOfWeekType }
         *     
         */
        public void setDayOfWeek(DayOfWeekType value) {
            this.dayOfWeek = value;
        }

        /**
         * Gets the value of the currencyCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCurrencyCode() {
            return currencyCode;
        }

        /**
         * Sets the value of the currencyCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCurrencyCode(String value) {
            this.currencyCode = value;
        }

        /**
         * Gets the value of the companyShortName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCompanyShortName() {
            return companyShortName;
        }

        /**
         * Sets the value of the companyShortName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCompanyShortName(String value) {
            this.companyShortName = value;
        }

        /**
         * Gets the value of the travelSector property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTravelSector() {
            return travelSector;
        }

        /**
         * Sets the value of the travelSector property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTravelSector(String value) {
            this.travelSector = value;
        }

        /**
         * Gets the value of the code property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCode() {
            return code;
        }

        /**
         * Sets the value of the code property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCode(String value) {
            this.code = value;
        }

        /**
         * Gets the value of the codeContext property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCodeContext() {
            return codeContext;
        }

        /**
         * Sets the value of the codeContext property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCodeContext(String value) {
            this.codeContext = value;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://www.opentravel.org/OTA/2003/05}VehicleType">
     *       &lt;sequence>
     *         &lt;element name="Description" type="{http://www.opentravel.org/OTA/2003/05}FormattedTextTextType" minOccurs="0"/>
     *       &lt;/sequence>
     *       &lt;attribute name="LowMileage" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *       &lt;attribute name="HighMileage" type="{http://www.w3.org/2001/XMLSchema}integer" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "description"
    })
    public static class Vehicle
        extends VehicleType
    {

        @XmlElement(name = "Description")
        protected FormattedTextTextType description;
        @XmlAttribute(name = "LowMileage")
        protected BigInteger lowMileage;
        @XmlAttribute(name = "HighMileage")
        protected BigInteger highMileage;

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link FormattedTextTextType }
         *     
         */
        public FormattedTextTextType getDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link FormattedTextTextType }
         *     
         */
        public void setDescription(FormattedTextTextType value) {
            this.description = value;
        }

        /**
         * Gets the value of the lowMileage property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getLowMileage() {
            return lowMileage;
        }

        /**
         * Sets the value of the lowMileage property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setLowMileage(BigInteger value) {
            this.lowMileage = value;
        }

        /**
         * Gets the value of the highMileage property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getHighMileage() {
            return highMileage;
        }

        /**
         * Sets the value of the highMileage property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setHighMileage(BigInteger value) {
            this.highMileage = value;
        }

    }

}
