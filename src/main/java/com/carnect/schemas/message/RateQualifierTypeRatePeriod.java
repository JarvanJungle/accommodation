
package com.carnect.schemas.message;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RateQualifierTypeRatePeriod.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RateQualifierTypeRatePeriod">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Hourly"/>
 *     &lt;enumeration value="Daily"/>
 *     &lt;enumeration value="Weekly"/>
 *     &lt;enumeration value="Monthly"/>
 *     &lt;enumeration value="WeekendDay"/>
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="Package"/>
 *     &lt;enumeration value="Bundle"/>
 *     &lt;enumeration value="Total"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RateQualifierTypeRatePeriod")
@XmlEnum
public enum RateQualifierTypeRatePeriod {

    @XmlEnumValue("Hourly")
    HOURLY("Hourly"),
    @XmlEnumValue("Daily")
    DAILY("Daily"),
    @XmlEnumValue("Weekly")
    WEEKLY("Weekly"),
    @XmlEnumValue("Monthly")
    MONTHLY("Monthly"),
    @XmlEnumValue("WeekendDay")
    WEEKEND_DAY("WeekendDay"),
    @XmlEnumValue("Other")
    OTHER("Other"),
    @XmlEnumValue("Package")
    PACKAGE("Package"),
    @XmlEnumValue("Bundle")
    BUNDLE("Bundle"),
    @XmlEnumValue("Total")
    TOTAL("Total");
    private final String value;

    RateQualifierTypeRatePeriod(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RateQualifierTypeRatePeriod fromValue(String v) {
        for (RateQualifierTypeRatePeriod c: RateQualifierTypeRatePeriod.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
