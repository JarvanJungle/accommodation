//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2021.06.17 at 10:54:05 AM ICT 
//


package com.sabre.schema.stl_payload.v02_01;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import com.sabre.schema.sp.reservation.v2_4.CreatePassengerNameRecordRQ;


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
 *     &lt;extension base="{http://services.sabre.com/STL_Payload/v02_01}STL_Payload"&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "v02_01", name = "STL_Request_Payload")
@XmlSeeAlso({
    CreatePassengerNameRecordRQ.class
})
public class STLRequestPayload
    extends STLPayload
{


}