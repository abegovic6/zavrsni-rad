package ba.unsa.etf.zavrsnirad.dto;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.events.Attribute;
import java.util.Iterator;
import java.util.List;

public class JRXMLElement {
    private JRXMLTypes jrxmlType;
    private Iterator<? extends Attribute> styleAttributes;
    private String styleName;
    private String styleText;
    private int height;


    public JRXMLTypes getJrxmlType() {
        return jrxmlType;
    }

    public void setJrxmlType(JRXMLTypes jrxmlType) {
        this.jrxmlType = jrxmlType;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getStyleText() {
        return styleText;
    }

    public void setStyleText(String styleText) {
        this.styleText = styleText;
    }

    public Iterator<? extends Attribute> getStyleAttributes() {
        return styleAttributes;
    }

    public void setStyleAttributes(List<JRXMLAttribute> jrxmlAttributeList, XMLEventFactory xmlEventFactory) {
        setHeight(jrxmlAttributeList);
        this.styleAttributes = JRXMLAttribute.generateAttributes(jrxmlAttributeList, xmlEventFactory);
    }

    public int getHeight() {
        return height;
    }

    private void setHeight(List<JRXMLAttribute> jrxmlAttributeList) {
        for (var att : jrxmlAttributeList) {
            if (att.getId().equals("fontSize")) {
                height = Integer.parseInt(att.getValue()) + Integer.parseInt(att.getValue()) / 2;
            }
        }
    }
}
