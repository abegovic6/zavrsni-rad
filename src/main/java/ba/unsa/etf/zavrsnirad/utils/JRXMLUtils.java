package ba.unsa.etf.zavrsnirad.utils;

import ba.unsa.etf.zavrsnirad.dto.JRXMLAttribute;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Iterator;
import java.util.List;

public class JRXMLUtils {

    public static final String TITLE_COLOR = "#B0E298";
    public static final String SUBTITLE_COLOR = "#ffffff";
    public static final int PAGE_SIZE = 535;

    public static final String QUERY_STRING = "queryString";
    public static final String PARAMETAR = "parameter";
    public static final String FIELD = "field";
    public static final String FIELD_DESCRIPTION = "fieldDescription";
    public static final String STYLE = "style";

    public static final String TITLE = "title";
    public static final String PAGE_HEADER = "pageHeader";
    public static final String COLUMN_HEADER = "columnHeader";
    public static final String DETAIL = "detail";

    public static final String BAND = "band";
    public static final String STATIC_TEXT = "staticText";
    public static final String TEXT_FIELD = "textField";
    public static final String REPORT_ELEMENT = "reportElement";
    public static final String TEXT_FIELD_EXPRESSION = "textFieldExpression";
    public static final String PROPERTY = "property";
    public static final String TEXT = "text";

    static XMLEvent createTextField(XMLEventFactory xmlEventFactory, XMLEvent event, XMLEventWriter writer,
                                    Iterator<? extends Attribute> reportIterator, String textFieldExpression,String htmlId)
            throws XMLStreamException {
        if(event.isEndElement()) {
            EndElement oldEndElement = event.asEndElement();

            StartElement startElementStaticText = xmlEventFactory.createStartElement(new QName(TEXT_FIELD),
                    null, oldEndElement.getNamespaces());
            writer.add(startElementStaticText);

            StartElement startElementReportElement = xmlEventFactory.createStartElement(new QName(REPORT_ELEMENT),
                    reportIterator, startElementStaticText.getNamespaces());
            writer.add(startElementReportElement);

            var iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name", "net.sf.jasperreports.export.html.id"),
                            new JRXMLAttribute("value", htmlId)
                    ),
                    xmlEventFactory);
            StartElement startPropertyElement = xmlEventFactory.createStartElement(new QName(PROPERTY), iterator, startElementReportElement.getNamespaces());
            writer.add(startPropertyElement);

            EndElement endElement = xmlEventFactory.createEndElement(new QName(PROPERTY), startPropertyElement.getNamespaces());
            writer.add(endElement);

            EndElement endElementReportElement
                    = xmlEventFactory.createEndElement(new QName(REPORT_ELEMENT), endElement.getNamespaces());
            writer.add(endElementReportElement);

            StartElement startElementText = xmlEventFactory.createStartElement(new QName(TEXT_FIELD_EXPRESSION), null,
                    endElementReportElement.getNamespaces());
            writer.add(startElementText);
            writer.add(xmlEventFactory.createCharacters(textFieldExpression));

            EndElement endElementText = xmlEventFactory.createEndElement(new QName(TEXT_FIELD_EXPRESSION), startElementText.getNamespaces());
            writer.add(endElementText);
            return xmlEventFactory.createEndElement(new QName(TEXT_FIELD),
                    endElementText.getNamespaces());
        } else {
            StartElement oldEndElement = event.asStartElement();

            StartElement startElementStaticText = xmlEventFactory.createStartElement(new QName(TEXT_FIELD),
                    null, oldEndElement.getNamespaces());
            writer.add(startElementStaticText);

            StartElement startElementReportElement = xmlEventFactory.createStartElement(new QName(REPORT_ELEMENT),
                    reportIterator, startElementStaticText.getNamespaces());
            writer.add(startElementReportElement);

            var iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name", "net.sf.jasperreports.export.html.id"),
                            new JRXMLAttribute("value", htmlId)
                    ),
                    xmlEventFactory);
            StartElement startPropertyElement = xmlEventFactory.createStartElement(new QName(PROPERTY), iterator, startElementReportElement.getNamespaces());
            writer.add(startPropertyElement);

            EndElement endElement = xmlEventFactory.createEndElement(new QName(PROPERTY), startPropertyElement.getNamespaces());
            writer.add(endElement);

            EndElement endElementReportElement
                    = xmlEventFactory.createEndElement(new QName(REPORT_ELEMENT), endElement.getNamespaces());
            writer.add(endElementReportElement);

            StartElement startElementText = xmlEventFactory.createStartElement(new QName(TEXT_FIELD_EXPRESSION), null,
                    endElementReportElement.getNamespaces());
            writer.add(startElementText);
            writer.add(xmlEventFactory.createCharacters(textFieldExpression));

            EndElement endElementText = xmlEventFactory.createEndElement(new QName(TEXT_FIELD_EXPRESSION), startElementText.getNamespaces());
            writer.add(endElementText);
            return xmlEventFactory.createEndElement(new QName(TEXT_FIELD),
                    endElementText.getNamespaces());
        }

    }

    public static XMLEvent createStaticText(XMLEventFactory xmlEventFactory, XMLEvent event, XMLEventWriter writer,
                                      Iterator<? extends Attribute> reportIterator, String textFieldExpression, String htmlId)
            throws XMLStreamException {
        if(event.isEndElement()) {
            EndElement oldEndElement = event.asEndElement();

            StartElement startElementStaticText = xmlEventFactory.createStartElement(new QName(STATIC_TEXT),
                    null, oldEndElement.getNamespaces());
            writer.add(startElementStaticText);

            StartElement startElementReportElement = xmlEventFactory.createStartElement(new QName(REPORT_ELEMENT),
                    reportIterator, startElementStaticText.getNamespaces());
            writer.add(startElementReportElement);


            var iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name", "net.sf.jasperreports.export.html.id"),
                            new JRXMLAttribute("value", htmlId)
                    ),
                    xmlEventFactory);
            StartElement startPropertyElement = xmlEventFactory.createStartElement(new QName(PROPERTY), iterator, startElementReportElement.getNamespaces());
            writer.add(startPropertyElement);

            EndElement endElement = xmlEventFactory.createEndElement(new QName(PROPERTY), startPropertyElement.getNamespaces());
            writer.add(endElement);

            EndElement endElementReportElement
                    = xmlEventFactory.createEndElement(new QName(REPORT_ELEMENT), endElement.getNamespaces());
            writer.add(endElementReportElement);

            StartElement startElementText = xmlEventFactory.createStartElement(new QName(TEXT), null,
                    endElementReportElement.getNamespaces());
            writer.add(startElementText);

            writer.add(xmlEventFactory.createCharacters(textFieldExpression));

            EndElement endElementText = xmlEventFactory.createEndElement(new QName(TEXT), startElementText.getNamespaces());
            writer.add(endElementText);
            return xmlEventFactory.createEndElement(new QName(STATIC_TEXT),
                    endElementText.getNamespaces());
        } else {
            StartElement oldStartElement = event.asStartElement();

            StartElement startElementStaticText = xmlEventFactory.createStartElement(new QName(STATIC_TEXT),
                    null, oldStartElement.getNamespaces());
            writer.add(startElementStaticText);

            StartElement startElementReportElement = xmlEventFactory.createStartElement(new QName(REPORT_ELEMENT),
                    reportIterator, startElementStaticText.getNamespaces());
            writer.add(startElementReportElement);


            var iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name", "net.sf.jasperreports.export.html.id"),
                            new JRXMLAttribute("value", htmlId)
                    ),
                    xmlEventFactory);
            StartElement startPropertyElement = xmlEventFactory.createStartElement(new QName(PROPERTY), iterator, startElementReportElement.getNamespaces());
            writer.add(startPropertyElement);

            EndElement endElement = xmlEventFactory.createEndElement(new QName(PROPERTY), startPropertyElement.getNamespaces());
            writer.add(endElement);

            EndElement endElementReportElement
                    = xmlEventFactory.createEndElement(new QName(REPORT_ELEMENT), endElement.getNamespaces());
            writer.add(endElementReportElement);

            StartElement startElementText = xmlEventFactory.createStartElement(new QName(TEXT), null,
                    endElementReportElement.getNamespaces());
            writer.add(startElementText);

            writer.add(xmlEventFactory.createCharacters(textFieldExpression));

            EndElement endElementText = xmlEventFactory.createEndElement(new QName(TEXT), startElementText.getNamespaces());
            writer.add(endElementText);
            return xmlEventFactory.createEndElement(new QName(STATIC_TEXT),
                    endElementText.getNamespaces());
        }


    }

    public static int getX(int index, int arrayLength) {
        return index * getWidth(arrayLength);
    }

    public static int getWidth(int arrayLength) {
        return PAGE_SIZE / arrayLength;
    }
}
