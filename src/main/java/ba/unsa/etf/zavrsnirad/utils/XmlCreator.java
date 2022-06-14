package ba.unsa.etf.zavrsnirad.utils;

import ba.unsa.etf.zavrsnirad.dto.JRXMLAttribute;
import ba.unsa.etf.zavrsnirad.dto.JRXMLColumn;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class XmlCreator {

    private static final String QUERY_STRING = "queryString";
    private static final String PARAMETAR = "parameter";
    private static final String FIELD = "field";
    private static final String FIELD_DESCRIPTION = "fieldDescription";
    private static final String STYLE = "style";

    private static final String TITLE = "title";
    private static final String PAGE_HEADER = "pageHeader";
    private static final String COLUMN_HEADER = "columnHeader";
    private static final String DETAIL = "detail";

    private static final String BAND = "band";
    private static final String STATIC_TEXT = "staticText";
    private static final String TEXT_FIELD = "textField";
    private static final String REPORT_ELEMENT = "reportElement";
    private static final String TEXT_ELEMENT = "textElement";
    private static final String TEXT_FIELD_EXPRESSION = "textFieldExpression";
    private static final String FONT = "font";
    private static final String TEXT = "text";

    private static final int PAGE_SIZE = 535;

    private final File in;
    private final File out;
    private final XMLInputFactory xif;
    private final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
    private final XMLOutputFactory xof = XMLOutputFactory.newInstance();

    public XmlCreator(String sourceFilePath, String destFilePath) throws IOException, XMLStreamException {
        xif =
                (XMLInputFactory.class.getClassLoader() == null) ?
                        XMLInputFactory.newInstance() :
                        XMLInputFactory.newInstance(XMLInputFactory.class.getName(),
                                XMLInputFactory.class.getClassLoader());
        //xif.setProperty("report-cdata-event", Boolean.TRUE);
        //xif.setProperty("report-cdata-event", Boolean.TRUE);
        xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);

        System.out.println(xif.getProperty(XMLInputFactory.IS_COALESCING));
        in = createCopyOfTemplate(sourceFilePath, FilePath.JRXML_SRC_FILE_NAME.getFullPath());
        out = new File(destFilePath);
    }

    private File createCopyOfTemplate(String sourceFilePath, String destFilePath) throws IOException, XMLStreamException {
        File inCopy = new File(sourceFilePath);
        File outCopy = new File(destFilePath);

        FileInputStream fis = new FileInputStream(inCopy);
        FileOutputStream fos = new FileOutputStream(outCopy);
        XMLEventReader parser = xif.createXMLEventReader(fis);
        XMLEventWriter writer = xof.createXMLEventWriter(fos);

        try {
            while (parser.hasNext()) {
                XMLEvent event = parser.nextEvent();

                writer.add(event);

            }
            writer.flush();
            writer.close();
            parser.close();
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeConnections(fis, fos, parser, writer);
        }

//        FileInputStream inStream = new FileInputStream(inCopy);
//        FileOutputStream outStream = new FileOutputStream(outCopy);
//        try {
//            int n;
//            while ((n = inStream.read()) != -1) {
//                outStream.write(n);
//            }
//        }
//        finally {
//            if (inStream != null) {
//                outStream.close();
//            }
//            if (outStream != null) {
//                outStream.close();
//            }
//        }
//        System.out.println("File Copied");

        return outCopy;
    }

    public XmlCreator createJRXML(String queryString, String title, List<JRXMLColumn> columns) throws FileNotFoundException, XMLStreamException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        XMLEventReader parser = xif.createXMLEventReader(fis);
        XMLEventWriter writer = xof.createXMLEventWriter(fos);

        try {
            while (parser.hasNext()) {
                XMLEvent event = parser.nextEvent();
                if (event.isEndElement()) {
                    String name = event.asEndElement().getName().getLocalPart();
                    if(Objects.equals(name, PARAMETAR)) {
                        writer.add(event);
                        event = addQueryElement(event, writer, queryString).asEndElement();
                        System.out.println("zavrsilo pisanje SQL query");
                        writer.add(event);
                        for (int i = 0; i < columns.size(); i++) {
                            event = createField(event, writer, columns.get(i).getColumnName(), columns.get(i).getColumnType());
                            if(i != columns.size() - 1)
                                writer.add(event);
                            System.out.println("zavrsilo pisanje kolone " + columns.get(i).getColumnName());
                        }

                        if(title != null) {
                            event = parser.nextEvent();
                            event = parser.nextEvent();
                            event = addTitle(event, writer, title);
                        } else {
                            while (parser.hasNext()) {
                                event = parser.nextEvent();
                                if (event.isEndElement()) {
                                    name = event.asEndElement().getName().getLocalPart();
                                    if(Objects.equals(name, TITLE)) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(event.isStartElement()) {
                    String name = event.asStartElement().getName().getLocalPart();
                    if (Objects.equals(name, COLUMN_HEADER)) {
                        writer.add(event);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if (event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, BAND)) {
                                    var copyElement = event;
                                    for(int i = 0; i < columns.size(); i++) {
                                        event = createTextFieldContentHeader(event, writer, columns.get(i).getColumnName(),
                                                getX(i, columns.size()), getWidth(columns.size()));
                                        writer.add(event);
                                        System.out.println("zavrsilo pisanje header " + columns.get(i).getColumnName());
                                    }
                                    event = copyElement;

                                } else if(Objects.equals(name, COLUMN_HEADER)) {
                                    break;
                                }

                            }
                            writer.add(event);
                        }
                    }
                    if (Objects.equals(name, DETAIL)) {
                        writer.add(event);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if (event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, BAND)) {
                                    var copyElement = event;
                                    for(int i = 0; i < columns.size(); i++) {
                                        event = createTextFieldDetail(event, writer, columns.get(i).getColumnName(),
                                                getX(i, columns.size()), getWidth(columns.size()));
                                        writer.add(event);
                                        System.out.println("zavrsilo pisanje detail " + columns.get(i).getColumnName());
                                    }
                                    event = copyElement;

                                } else if(Objects.equals(name, DETAIL)) {
                                    break;
                                }

                            }
                            writer.add(event);
                        }
                    }
                }
                writer.add(event);

            }
            writer.flush();
            writer.close();
            parser.close();
            fis.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            closeConnections(fis, fos, parser, writer);
        }
        return this;

    }

    private XMLEvent addTitle(XMLEvent event, XMLEventWriter writer, String title) throws XMLStreamException {
        return event;

    }

    private XMLEvent addQueryElement(XMLEvent event, XMLEventWriter writer,
                                     String text) throws XMLStreamException {
        EndElement oldEndElement = event.asEndElement();
        StartElement newStartEvent = xmlEventFactory.createStartElement(new QName(QUERY_STRING),
                null, oldEndElement.getNamespaces());
        writer.add(newStartEvent);
        //writer.add(xmlEventFactory.createCData("select * from city"));
        //writer.add(xmlEventFactory.createCData(""));
        writer.add(xmlEventFactory.createCData(text));
        return xmlEventFactory.createEndElement(new QName(QUERY_STRING), newStartEvent.getNamespaces());
    }

    private int getX(int index, int arrayLength) {
        return index * getWidth(arrayLength);
    }

    private int getWidth(int arrayLength) {
        return PAGE_SIZE / arrayLength;
    }

    private XMLEvent createTextField(XMLEvent event, XMLEventWriter writer,
                                     Iterator<? extends Attribute> reportIterator, String textFieldExpression, boolean isCData)
            throws XMLStreamException {
        EndElement oldEndElement = event.asEndElement();

        StartElement startElementStaticText = xmlEventFactory.createStartElement(new QName(TEXT_FIELD),
                null, oldEndElement.getNamespaces());
        writer.add(startElementStaticText);

        StartElement startElementReportElement = xmlEventFactory.createStartElement(new QName(REPORT_ELEMENT),
                reportIterator, startElementStaticText.getNamespaces());
        writer.add(startElementReportElement);

        EndElement endElementReportElement
                = xmlEventFactory.createEndElement(new QName(REPORT_ELEMENT), startElementReportElement.getNamespaces());
        writer.add(endElementReportElement);

        StartElement startElementText = xmlEventFactory.createStartElement(new QName(TEXT_FIELD_EXPRESSION), null,
                endElementReportElement.getNamespaces());
        writer.add(startElementText);
        if (isCData)
            writer.add(xmlEventFactory.createCData(textFieldExpression));
        else writer.add(xmlEventFactory.createCharacters(textFieldExpression));

        //writer.add(xmlEventFactory.create);

        EndElement endElementText = xmlEventFactory.createEndElement(new QName(TEXT_FIELD_EXPRESSION), startElementText.getNamespaces());
        writer.add(endElementText);
        return xmlEventFactory.createEndElement(new QName(TEXT_FIELD),
                endElementText.getNamespaces());


    }

    private XMLEvent createStyles(XMLEvent event, XMLEventWriter writer, String fieldName) throws XMLStreamException {
        EndElement oldStartEvent = event.asEndElement();
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("name", "header_style_" + fieldName)),
                xmlEventFactory);

        StartElement newStartStyleHeaderElement
                = xmlEventFactory.createStartElement(new QName(STYLE), iterator, oldStartEvent.getNamespaces());
        writer.add(newStartStyleHeaderElement);

        EndElement newEndStyleHeaderElement = xmlEventFactory.createEndElement(new QName(STYLE), newStartStyleHeaderElement.getNamespaces());
        writer.add(newEndStyleHeaderElement);

        iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("name", "detail_style_" + fieldName)),
                xmlEventFactory);

        StartElement newStartStyleDetailElement
                = xmlEventFactory.createStartElement(new QName(STYLE), iterator, newEndStyleHeaderElement.getNamespaces());
        writer.add(newStartStyleDetailElement);

        return xmlEventFactory.createEndElement(new QName(STYLE), newStartStyleDetailElement.getNamespaces());

    }

    private XMLEvent createField(XMLEvent event, XMLEventWriter writer, String fieldName, String className) throws XMLStreamException {
        EndElement oldStartEvent = event.asEndElement();

        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("name", fieldName), new JRXMLAttribute("class", className)),
                xmlEventFactory);

        StartElement newStartEventField = xmlEventFactory.createStartElement(new QName(FIELD),
                iterator, oldStartEvent.getNamespaces());
        writer.add(newStartEventField);

        StartElement newStartEventFieldDescription = xmlEventFactory.createStartElement(new QName(FIELD_DESCRIPTION),
                null, newStartEventField.getNamespaces());
        writer.add(newStartEventFieldDescription);
        writer.add(xmlEventFactory.createCData(""));

        EndElement newEndElementFieldDescription = xmlEventFactory.createEndElement(new QName(FIELD_DESCRIPTION), newStartEventFieldDescription.getNamespaces());
        writer.add(newEndElementFieldDescription);

        return xmlEventFactory.createEndElement(new QName(FIELD), newEndElementFieldDescription.getNamespaces());

    }

    private XMLEvent createTextFieldContentHeader(XMLEvent event, XMLEventWriter writer, String fieldName, int x, int width) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("key", "header_" + fieldName),
                        //new JRXMLAttribute("style", "header_style_" + fieldName),
                        new JRXMLAttribute("positionType", "Float"),
                        new JRXMLAttribute("stretchType", "RelativeToTallestObject"),
                        new JRXMLAttribute("x", String.valueOf(x)),
                        new JRXMLAttribute("y", String.valueOf(3)),
                        new JRXMLAttribute("width", String.valueOf(width)),
                        new JRXMLAttribute("height", String.valueOf(15)),
                        new JRXMLAttribute("isPrintWhenDetailOverflows", "true")
                ),
                xmlEventFactory);

        return createTextField(event, writer, iterator, "\"" + fieldName + "\"", false);
    }

    private XMLEvent createTextFieldDetail(XMLEvent event, XMLEventWriter writer, String fieldName, int x, int width) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("key", "detail_" + fieldName),
                        //new JRXMLAttribute("style", "detail_style_" + fieldName),
                        new JRXMLAttribute("positionType", "Float"),
                        new JRXMLAttribute("stretchType", "RelativeToTallestObject"),
                        new JRXMLAttribute("x", String.valueOf(x)),
                        new JRXMLAttribute("y", String.valueOf(0)),
                        new JRXMLAttribute("width", String.valueOf(width)),
                        new JRXMLAttribute("height", String.valueOf(15)),
                        new JRXMLAttribute("isPrintWhenDetailOverflows", "true")
                ),
                xmlEventFactory);

        return createTextField(event, writer, iterator,  "$F{" + fieldName + "}" , false);
    }

    private XMLEvent getAddedEventAfterStartElement(XMLEvent event, XMLEventWriter writer, String elementName,
                                                    String text, boolean isCData) throws XMLStreamException {
        StartElement oldStartEvent = event.asStartElement();
        StartElement newStartEvent = xmlEventFactory.createStartElement(new QName(elementName),
                null, oldStartEvent.getNamespaces());
        writer.add(newStartEvent);
        if(isCData)
            writer.add(xmlEventFactory.createCData(text));
        else writer.add(xmlEventFactory.createCharacters(text));
        return xmlEventFactory.createEndElement(newStartEvent.getName(), newStartEvent.getNamespaces());
    }

    static void closeConnections(FileInputStream fis, FileOutputStream fos, XMLEventReader parser, XMLEventWriter writer) {
        try {
            if (parser != null) {
                parser.close();
            }
        } catch (Exception e) {}
        try {
            if (fis != null) {
                fis.close();
            }
        } catch (Exception e) {}
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception e) {}
        try {
            if (fos != null) {
                fos.close();
            }
        } catch (Exception e) {}
    }

}
