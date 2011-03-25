//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.13 at 09:30:34 AM PST 
//


package uk.org.siri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Response with Lines available to make requests.
 * 
 * <p>Java class for DirectionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DirectionStructure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DirectionRef" type="{http://www.siri.org.uk/}DirectionRefStructure" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="DirectionName" type="{http://www.siri.org.uk/}NaturalLanguageStringStructure"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DirectionStructure", propOrder = {
    "directionRef",
    "directionName"
})
public class DirectionStructure {

    @XmlElement(name = "DirectionRef")
    protected List<DirectionRefStructure> directionRef;
    @XmlElement(name = "DirectionName", required = true)
    protected NaturalLanguageStringStructure directionName;

    /**
     * Gets the value of the directionRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the directionRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDirectionRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DirectionRefStructure }
     * 
     * 
     */
    public List<DirectionRefStructure> getDirectionRef() {
        if (directionRef == null) {
            directionRef = new ArrayList<DirectionRefStructure>();
        }
        return this.directionRef;
    }

    /**
     * Gets the value of the directionName property.
     * 
     * @return
     *     possible object is
     *     {@link NaturalLanguageStringStructure }
     *     
     */
    public NaturalLanguageStringStructure getDirectionName() {
        return directionName;
    }

    /**
     * Sets the value of the directionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link NaturalLanguageStringStructure }
     *     
     */
    public void setDirectionName(NaturalLanguageStringStructure value) {
        this.directionName = value;
    }

}