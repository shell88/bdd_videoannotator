//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.08.17 at 01:53:04 PM CEST 
//


package com.github.shell88.bddvideoannotator.annotationfile.exporter.eaf;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for constraintType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="constraintType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="STEREOTYPE" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="DESCRIPTION" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "constraintType")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2015-08-17T01:53:04+02:00", comments = "JAXB RI v2.2.11")
public class ConstraintType {

    @XmlAttribute(name = "STEREOTYPE", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-08-17T01:53:04+02:00", comments = "JAXB RI v2.2.11")
    protected String stereotype;
    @XmlAttribute(name = "DESCRIPTION")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-08-17T01:53:04+02:00", comments = "JAXB RI v2.2.11")
    protected String description;

    /**
     * Gets the value of the stereotype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-08-17T01:53:04+02:00", comments = "JAXB RI v2.2.11")
    public String getSTEREOTYPE() {
        return stereotype;
    }

    /**
     * Sets the value of the stereotype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-08-17T01:53:04+02:00", comments = "JAXB RI v2.2.11")
    public void setSTEREOTYPE(String value) {
        this.stereotype = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-08-17T01:53:04+02:00", comments = "JAXB RI v2.2.11")
    public String getDESCRIPTION() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2015-08-17T01:53:04+02:00", comments = "JAXB RI v2.2.11")
    public void setDESCRIPTION(String value) {
        this.description = value;
    }

}
