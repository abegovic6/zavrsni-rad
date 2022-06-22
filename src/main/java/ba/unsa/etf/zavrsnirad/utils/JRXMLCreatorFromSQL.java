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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class JRXMLCreatorFromSQL {


    private final File in;
    private final File out;
    private final XMLInputFactory xif;
    private final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
    private final XMLOutputFactory xof = XMLOutputFactory.newInstance();

    public JRXMLCreatorFromSQL(String sourceFilePath, String destFilePath) throws IOException, XMLStreamException {
        xif =
                (XMLInputFactory.class.getClassLoader() == null) ?
                        XMLInputFactory.newInstance() :
                        XMLInputFactory.newInstance(XMLInputFactory.class.getName(),
                                XMLInputFactory.class.getClassLoader());
        xif.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
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

        return outCopy;
    }

    public void createJRXML() throws FileNotFoundException, XMLStreamException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        XMLEventReader parser = xif.createXMLEventReader(fis);
        XMLEventWriter writer = xof.createXMLEventWriter(fos);

        try {
            while (parser.hasNext()) {
                XMLEvent event = parser.nextEvent();
                if (event.isEndElement()) {
                    String name = event.asEndElement().getName().getLocalPart();
                    if(Objects.equals(name, JRXMLUtils.PARAMETAR)) {
                        writer.add(event);

                        event = addQueryElement(event, writer, ReportData.getReportQuery()).asEndElement();
                        writer.add(event);
                        for (int i = 0; i < ReportData.getReportColumns().size(); i++) {
                            event = createField(event, writer,
                                    ReportData.getReportColumns().get(i).getColumnName(),
                                    ReportData.getReportColumns().get(i).getColumnType());
                            if(i != ReportData.getReportColumns().size() - 1 ||
                                    ReportData.getReportTitle() != null ||
                                    ReportData.getReportSubtitle() != null)
                                writer.add(event);
                        }

                        if(ReportData.getReportTitle() != null) {
                            while (parser.hasNext()) {
                                event = parser.nextEvent();
                                if(event.isEndElement()) {
                                    name = event.asEndElement().getName().getLocalPart();
                                    if(Objects.equals(name, JRXMLUtils.BAND)) {
                                        var copyElement = event;
                                        event = addTitle(event, writer, ReportData.getReportTitle());
                                        writer.add(event);
                                        event = copyElement;
                                        writer.add(event);
                                        break;
                                    }
                                }
                                writer.add(event);
                            }
                        } else {
                            while (parser.hasNext()) {
                                event = parser.nextEvent();
                                if (event.isEndElement()) {
                                    name = event.asEndElement().getName().getLocalPart();
                                    if(Objects.equals(name, JRXMLUtils.TITLE)) {
                                        break;
                                    }
                                }
                            }
                        }

                        if(ReportData.getReportSubtitle() != null) {
                            while (parser.hasNext()) {
                                event = parser.nextEvent();
                                if(event.isEndElement()) {
                                    name = event.asEndElement().getName().getLocalPart();
                                    if(Objects.equals(name, JRXMLUtils.BAND)) {
                                        var copyElement = event;
                                        event = addSubTitle(event, writer, ReportData.getReportSubtitle());
                                        writer.add(event);
                                        event = copyElement;
                                        break;
                                    }
                                }
                                writer.add(event);
                            }
                        } else {
                            while (parser.hasNext()) {
                                event = parser.nextEvent();
                                if (event.isEndElement()) {
                                    name = event.asEndElement().getName().getLocalPart();
                                    if(Objects.equals(name, JRXMLUtils.PAGE_HEADER)) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if(event.isStartElement()) {
                    String name = event.asStartElement().getName().getLocalPart();
                    if(Objects.equals(name, JRXMLUtils.PARAMETAR)) {
                        createStyleElements(ReportData.getReportTitle(), ReportData.getReportSubtitle(), ReportData.getReportColumns(), event, writer);
                    }
                    if (Objects.equals(name, JRXMLUtils.COLUMN_HEADER)) {
                        writer.add(event);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if (event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, JRXMLUtils.BAND)) {
                                    var copyElement = event;
                                    for(int i = 0; i < ReportData.getReportColumns().size(); i++) {
                                        event = createTextFieldContentHeader(event, writer, ReportData.getReportColumns().get(i).getColumnName(),
                                                JRXMLUtils.getX(i, ReportData.getReportColumns().size()), JRXMLUtils.getWidth(ReportData.getReportColumns().size()));
                                        writer.add(event);
                                    }
                                    event = copyElement;

                                } else if(Objects.equals(name, JRXMLUtils.COLUMN_HEADER)) {
                                    break;
                                }

                            }
                            writer.add(event);
                        }
                    }
                    if (Objects.equals(name, JRXMLUtils.DETAIL)) {
                        writer.add(event);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if (event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, JRXMLUtils.BAND)) {
                                    var copyElement = event;
                                    for(int i = 0; i < ReportData.getReportColumns().size(); i++) {
                                        event = createTextFieldDetail(event, writer, ReportData.getReportColumns().get(i).getColumnName(),
                                                JRXMLUtils.getX(i, ReportData.getReportColumns().size()), JRXMLUtils.getWidth(ReportData.getReportColumns().size()));
                                        writer.add(event);
                                    }
                                    event = copyElement;

                                } else if(Objects.equals(name, JRXMLUtils.DETAIL)) {
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
        } finally {
            closeConnections(fis, fos, parser, writer);
        }

    }

    private XMLEvent addTitle(XMLEvent event, XMLEventWriter writer, String title) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("style", ReportData.getReportTitleStyleName()),
                        new JRXMLAttribute("x", String.valueOf(0)),
                        new JRXMLAttribute("y", String.valueOf(0)),
                        new JRXMLAttribute("backcolor", JRXMLUtils.TITLE_COLOR),
                        new JRXMLAttribute("mode", "Opaque"),
                        new JRXMLAttribute("width", String.valueOf(JRXMLUtils.PAGE_SIZE)),
                        new JRXMLAttribute("height", String.valueOf(79))
                ),
                xmlEventFactory);
        return JRXMLUtils.createStaticText(xmlEventFactory, event, writer, iterator, title, ReportData.getReportTitleStyleName());

    }

    private XMLEvent addSubTitle(XMLEvent event, XMLEventWriter writer, String title) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("style", ReportData.getReportSubTitleStyleName()),
                        new JRXMLAttribute("x", String.valueOf(0)),
                        new JRXMLAttribute("y", String.valueOf(0)),
                        new JRXMLAttribute("backcolor", JRXMLUtils.TITLE_COLOR),
                        new JRXMLAttribute("width", String.valueOf(JRXMLUtils.PAGE_SIZE)),
                        new JRXMLAttribute("height", String.valueOf(40))
                ),
                xmlEventFactory);
        return JRXMLUtils.createStaticText(xmlEventFactory, event, writer, iterator, title, ReportData.getReportSubTitleStyleName());

    }

    private XMLEvent addQueryElement(XMLEvent event, XMLEventWriter writer,
                                     String text) throws XMLStreamException {
        EndElement oldEndElement = event.asEndElement();
        StartElement newStartEvent = xmlEventFactory.createStartElement(new QName(JRXMLUtils.QUERY_STRING),
                null, oldEndElement.getNamespaces());
        writer.add(newStartEvent);
        writer.add(xmlEventFactory.createCData(text));
        return xmlEventFactory.createEndElement(new QName(JRXMLUtils.QUERY_STRING), newStartEvent.getNamespaces());
    }



    private XMLEvent createStyleElements(String title, String subtitle, List<JRXMLColumn> columns, XMLEvent event, XMLEventWriter writer) throws XMLStreamException {
        if(title != null) {
            var iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name", ReportData.getReportTitleStyleName()),
                            new JRXMLAttribute("hTextAlign", "Center"),
                            new JRXMLAttribute("backcolor", JRXMLUtils.TITLE_COLOR),
                            new JRXMLAttribute("fontSize", "36"),
                            new JRXMLAttribute("isBold", "true")
                           ),
                    xmlEventFactory);
            event = createStyleElement(event, writer, iterator);
            writer.add(event);
        }

        if(subtitle != null) {
            var iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name", ReportData.getReportSubTitleStyleName()),
                            new JRXMLAttribute("hTextAlign", "Right"),
                            new JRXMLAttribute("backcolor", JRXMLUtils.SUBTITLE_COLOR),
                            new JRXMLAttribute("fontSize", "18"),
                            new JRXMLAttribute("isItalic", "true"),
                            new JRXMLAttribute("isBold", "true")
                    ),
                    xmlEventFactory);
            event = createStyleElement(event, writer, iterator);
            writer.add(event);
        }

        for(var column : columns) {
            var iteratorHeader
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name", "header_" + column.getColumnName()),
                            new JRXMLAttribute("hTextAlign", "Center"),
                            new JRXMLAttribute("fontSize", "12"),
                            new JRXMLAttribute("isBold", "true")
                    ),
                    xmlEventFactory);
            event = createStyleElement(event, writer, iteratorHeader);
            writer.add(event);
            var iteratorDetail
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("name","detail_" + column.getColumnName()),
                            new JRXMLAttribute("hTextAlign", "Center"),
                            new JRXMLAttribute("fontSize", "12")
                    ),
                    xmlEventFactory);
            event = createStyleElement(event, writer, iteratorDetail);
            writer.add(event);
        }



        return event;
    }

    private XMLEvent createStyleElement(XMLEvent event, XMLEventWriter writer, Iterator<? extends Attribute> iterator) throws XMLStreamException {
        if(event.isEndElement()) {
            EndElement oldEndElement = event.asEndElement();

            StartElement newStartStyleHeaderElement
                    = xmlEventFactory.createStartElement(new QName(JRXMLUtils.STYLE), iterator, oldEndElement.getNamespaces());
            writer.add(newStartStyleHeaderElement);

            return xmlEventFactory.createEndElement(new QName(JRXMLUtils.STYLE), newStartStyleHeaderElement.getNamespaces());
        } else {
            StartElement oldStartEvent = event.asStartElement();

            StartElement newStartStyleHeaderElement
                    = xmlEventFactory.createStartElement(new QName(JRXMLUtils.STYLE), iterator, oldStartEvent.getNamespaces());
            writer.add(newStartStyleHeaderElement);

            return xmlEventFactory.createEndElement(new QName(JRXMLUtils.STYLE), newStartStyleHeaderElement.getNamespaces());
        }

    }

    private XMLEvent createField(XMLEvent event, XMLEventWriter writer, String fieldName, String className) throws XMLStreamException {
        EndElement oldStartEvent = event.asEndElement();

        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("name", fieldName), new JRXMLAttribute("class", className)),
                xmlEventFactory);

        StartElement newStartEventField = xmlEventFactory.createStartElement(new QName(JRXMLUtils.FIELD),
                iterator, oldStartEvent.getNamespaces());
        writer.add(newStartEventField);

        StartElement newStartEventFieldDescription = xmlEventFactory.createStartElement(new QName(JRXMLUtils.FIELD_DESCRIPTION),
                null, newStartEventField.getNamespaces());
        writer.add(newStartEventFieldDescription);
        writer.add(xmlEventFactory.createCData(""));

        EndElement newEndElementFieldDescription = xmlEventFactory.createEndElement(new QName(JRXMLUtils.FIELD_DESCRIPTION), newStartEventFieldDescription.getNamespaces());
        writer.add(newEndElementFieldDescription);

        return xmlEventFactory.createEndElement(new QName(JRXMLUtils.FIELD), newEndElementFieldDescription.getNamespaces());

    }

    private XMLEvent createTextFieldContentHeader(XMLEvent event, XMLEventWriter writer, String fieldName, int x, int width) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("key", "header_" + fieldName),
                        new JRXMLAttribute("style", "header_" + fieldName),
                        new JRXMLAttribute("positionType", "Float"),
                        new JRXMLAttribute("stretchType", "RelativeToTallestObject"),
                        new JRXMLAttribute("x", String.valueOf(x)),
                        new JRXMLAttribute("y", String.valueOf(3)),
                        new JRXMLAttribute("width", String.valueOf(width)),
                        new JRXMLAttribute("height", String.valueOf(18)),
                        new JRXMLAttribute("isPrintWhenDetailOverflows", "true")
                ),
                xmlEventFactory);

        return JRXMLUtils.createTextField(xmlEventFactory, event, writer, iterator, "\"" + fieldName + "\"", "header_" + fieldName);
    }

    private XMLEvent createTextFieldDetail(XMLEvent event, XMLEventWriter writer, String fieldName, int x, int width) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("key", "detail_" + fieldName),
                        new JRXMLAttribute("style", "detail_" + fieldName ),
                        new JRXMLAttribute("positionType", "Float"),
                        new JRXMLAttribute("stretchType", "RelativeToTallestObject"),
                        new JRXMLAttribute("x", String.valueOf(x)),
                        new JRXMLAttribute("y", String.valueOf(0)),
                        new JRXMLAttribute("width", String.valueOf(width)),
                        new JRXMLAttribute("height", String.valueOf(18)),
                        new JRXMLAttribute("isPrintWhenDetailOverflows", "true")
                ),
                xmlEventFactory);

        return JRXMLUtils.createTextField(xmlEventFactory, event, writer, iterator,  "$F{" + fieldName + "}" , "detail_" + fieldName);
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
