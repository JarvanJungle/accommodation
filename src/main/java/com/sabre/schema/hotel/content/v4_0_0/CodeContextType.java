//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 12:36:19 AM ICT 
//


package com.sabre.schema.hotel.content.v4_0_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CodeContextType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CodeContextType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="GLOBAL"/&gt;
 *     &lt;enumeration value="SABRE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(namespace="content", name = "CodeContextType")
@XmlEnum
public enum CodeContextType {


    /**
     * Code context type is GLOBAL
     * 
     */
    GLOBAL,

    /**
     * Code context type is SABRE
     * 
     */
    SABRE;

    public String value() {
        return name();
    }

    public static CodeContextType fromValue(String v) {
        return valueOf(v);
    }

}
