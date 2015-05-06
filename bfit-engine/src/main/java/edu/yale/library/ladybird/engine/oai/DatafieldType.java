package edu.yale.library.ladybird.engine.oai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "datafieldType", namespace = "http://www.loc.gov/MARC21/slim", propOrder = { "subfield" })
public class DatafieldType {
    @XmlElement(namespace = "http://www.loc.gov/MARC21/slim")
    protected List<SubfieldType> subfield;

    @XmlAttribute(name = "tag")
    protected String tag;

    @XmlAttribute(name = "ind1")
    protected String ind1;

    @XmlAttribute(name = "ind2")
    protected String ind2;

    public List<SubfieldType> getSubfield() {
        if (subfield == null) {
            subfield = new ArrayList<SubfieldType>();
        }
        return this.subfield;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String value) {
        this.tag = value;
    }

    public String getInd1() {
        return ind1;
    }

    public void setInd1(String value) {
        this.ind1 = value;
    }

    public String getInd2() {
        return ind2;
    }

    public void setInd2(String value) {
        this.ind2 = value;
    }

    @Override
    public String toString() {
        return "DatafieldType{"
                + "subfield=" + subfield
                + ", tag='" + tag + '\''
                + ", ind1='" + ind1 + '\''
                + ", ind2='" + ind2 + '\''
                + '}';
    }
}
