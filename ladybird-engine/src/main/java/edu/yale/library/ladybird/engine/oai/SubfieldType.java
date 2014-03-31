package edu.yale.library.ladybird.engine.oai;


import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subfieldType", namespace = "http://www.loc.gov/MARC21/slim", propOrder = { "value" })
public class SubfieldType {
    @XmlValue
    protected String value;

    @XmlAttribute(name = "code")
    protected String code;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    @Override
    public String toString() {
        return "SubfieldType{" + "value='" + value + '\'' + ", code='" + code + '\''
                + '}';
    }
}
