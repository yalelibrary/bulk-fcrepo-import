package edu.yale.library.ladybird.engine.oai;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "controlfieldType", namespace = "http://www.loc.gov/MARC21/slim", propOrder = { "value" })
public class ControlfieldType {
    @XmlValue
    protected String value;

    @XmlAttribute(name = "tag")
    protected String tag;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String value) {
        this.tag = value;
    }

    @Override
    public String toString() {
        return "ControlfieldType{" + "value='" + value + '\''
                + ", tag='" + tag + '\'' + '}';
    }
}
