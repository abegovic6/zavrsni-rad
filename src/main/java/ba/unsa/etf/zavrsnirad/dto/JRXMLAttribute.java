package ba.unsa.etf.zavrsnirad.dto;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JRXMLAttribute {
    private String id;
    private String value;

    public JRXMLAttribute(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static Iterator<? extends Attribute> generateAttributes
            (List<JRXMLAttribute> jrxmlAttributeList, XMLEventFactory xmlEventFactory) {
        List<Attribute> attributeList = new ArrayList<>();
        for(var attribute : jrxmlAttributeList) {
            attributeList.add(xmlEventFactory.createAttribute(attribute.id, attribute.value));
        }
        return  attributeList.iterator();


    }
}
