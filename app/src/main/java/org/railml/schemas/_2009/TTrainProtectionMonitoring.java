//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.06 at 11:34:29 AM CET 
//


package org.railml.schemas._2009;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tTrainProtectionMonitoring.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="tTrainProtectionMonitoring">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="intermittent"/>
 *     &lt;enumeration value="continuous"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "tTrainProtectionMonitoring")
@XmlEnum
public enum TTrainProtectionMonitoring {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("intermittent")
    INTERMITTENT("intermittent"),
    @XmlEnumValue("continuous")
    CONTINUOUS("continuous");
    private final String value;

    TTrainProtectionMonitoring(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TTrainProtectionMonitoring fromValue(String v) {
        for (TTrainProtectionMonitoring c: TTrainProtectionMonitoring.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}