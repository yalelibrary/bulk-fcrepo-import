package edu.yale.library.engine.oai;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "record", namespace = "http://www.loc.gov/MARC21/slim")
public class Record
{
    protected String leader;
    protected List<ControlfieldType> controlfield;
    protected List<DatafieldType> datafield;
    protected String type;

    public String getLeader()
    {
        return leader;
    }

    @XmlElement(namespace = "http://www.loc.gov/MARC21/slim", required = true)
    public void setLeader(String value)
    {
        this.leader = value;
    }

    public List<ControlfieldType> getControlfield()
    {
        if (controlfield == null)
        {
            controlfield = new ArrayList<ControlfieldType>();
        }
        return this.controlfield;
    }

    @XmlElement(namespace = "http://www.loc.gov/MARC21/slim")
    public void setControlfield(List<ControlfieldType> controlfield)
    {
        this.controlfield = controlfield;
    }

    @XmlElement(namespace = "http://www.loc.gov/MARC21/slim")
    public void setDatafield(List<DatafieldType> datafield)
    {
        this.datafield = datafield;
    }

    public List<DatafieldType> getDatafield()
    {
        if (datafield == null)
        {
            datafield = new ArrayList<DatafieldType>();
        }
        return this.datafield;
    }

    public String getType()
    {
        return type;
    }

    @XmlAttribute(name = "type")
    public void setType(String value)
    {
        this.type = value;
    }

    @Override
    public String toString()
    {
        return "Record{" +
                "leader='" + leader + '\'' +
                ", controlfield=" + controlfield +
                ", datafield=" + datafield +
                ", type='" + type + '\'' +
                '}';
    }

}
