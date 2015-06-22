
package com.github.shell88.bddvideoannotator.stubjava;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für stepResult.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="stepResult">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SUCCESS"/>
 *     &lt;enumeration value="SKIPPED"/>
 *     &lt;enumeration value="FAILURE"/>
 *     &lt;enumeration value="ERROR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "stepResult", namespace = "http://service.bddvideoannotator.shell88.github.com/")
@XmlEnum
public enum StepResult {

    SUCCESS,
    SKIPPED,
    FAILURE,
    ERROR;

    public String value() {
        return name();
    }

    public static StepResult fromValue(String v) {
        return valueOf(v);
    }

}
