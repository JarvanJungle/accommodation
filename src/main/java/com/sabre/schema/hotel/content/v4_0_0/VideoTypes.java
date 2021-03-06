//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 12:36:19 AM ICT 
//


package com.sabre.schema.hotel.content.v4_0_0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for VideoTypes.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="VideoTypes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *     &lt;enumeration value="VIDEO360"/&gt;
 *     &lt;enumeration value="VIDEO720"/&gt;
 *     &lt;enumeration value="VIDEOTHUMBNAIL"/&gt;
 *     &lt;enumeration value="ALL"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "VideoTypes")
@XmlEnum
public enum VideoTypes {

    @XmlEnumValue("VIDEO360")
    VIDEO_360("VIDEO360"),
    @XmlEnumValue("VIDEO720")
    VIDEO_720("VIDEO720"),
    VIDEOTHUMBNAIL("VIDEOTHUMBNAIL"),
    ALL("ALL");
    private final String value;

    VideoTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static VideoTypes fromValue(String v) {
        for (VideoTypes c: VideoTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
