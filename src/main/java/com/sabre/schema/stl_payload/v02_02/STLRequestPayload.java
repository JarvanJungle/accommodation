//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.07 at 12:36:19 AM ICT 
//


package com.sabre.schema.stl_payload.v02_02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.sabre.schema.hotel.avail.v4_0_0.GetHotelAvailRQ;
import com.sabre.schema.hotel.content.v4_0_0.GetHotelContentRQ;


/**
 * Base type for request messages.
 * 
 * <p>Java class for STL_Request_Payload complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="STL_Request_Payload"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://services.sabre.com/STL_Payload/v02_02}STL_Payload"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "STL_Request_Payload")
@XmlSeeAlso({
    GetHotelContentRQ.class,
    GetHotelAvailRQ.class
})
public class STLRequestPayload
    extends STLPayload
{


}
