package ba.unsa.etf.zavrsnirad.utils;

import ba.unsa.etf.zavrsnirad.dto.JRXMLAttribute;
import ba.unsa.etf.zavrsnirad.dto.JRXMLElement;
import ba.unsa.etf.zavrsnirad.dto.JRXMLTypes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class JRXMLCreatorFromHTML {

    private final Document document;
    private List<JRXMLElement> elements = new ArrayList<>();

    private final File in;
    private final File out;
    private final XMLInputFactory xif;
    private final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
    private final XMLOutputFactory xof = XMLOutputFactory.newInstance();


    public JRXMLCreatorFromHTML(String html, String sourceFilePath, String destFilePath) throws XMLStreamException, IOException {
        html = html.replace("<br>", "\n");
        document = Jsoup.parse(html);

        xif = XMLInputFactory.newInstance();
        xif.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", Boolean.TRUE);
        in = new File(sourceFilePath);
        out = new File(destFilePath);
        getStyleForTitle();
        getStyleForSubitle();
        getStyleForHeaders();
        getStyleForDetail();
        createJRXML();


    }


    public JRXMLCreatorFromHTML createJRXML() throws IOException, XMLStreamException {
        FileInputStream fis = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);

        XMLEventReader parser = xif.createXMLEventReader(fis);
        XMLEventWriter writer = xof.createXMLEventWriter(fos);

        try {
            while (parser.hasNext()) {
                XMLEvent event = parser.nextEvent();
                if (event.isStartElement()) {
                    String name = event.asStartElement().getName().getLocalPart();

                    if(Objects.equals(name, JRXMLUtils.STYLE)) {
                        while (parser.hasNext()) {
                            event = parser.nextEvent();

                            if(event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, JRXMLUtils.STYLE)) {
                                    for (var element : elements) {
                                        event = createStyles(event, writer, element);
                                        writer.add(event);
                                    }
                                    while (parser.hasNext()) {
                                        event = parser.nextEvent();
                                        if(event.isStartElement()) {
                                            name = event.asStartElement().getName().getLocalPart();
                                            if(Objects.equals(name, JRXMLUtils.PARAMETAR)) {
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }

                    if (Objects.equals(name, JRXMLUtils.TITLE)) {
                        var copy= createTitle(event, writer, parser);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if(event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, JRXMLUtils.TITLE)) {
                                    event = copy;
                                    break;
                                }
                            }
                        }
                    }

                    if (Objects.equals(name, JRXMLUtils.PAGE_HEADER)) {
                        var copy= createSubTitle(event, writer);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if(event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, JRXMLUtils.PAGE_HEADER)) {
                                    event = copy;
                                    break;
                                }
                            }
                        }
                    }

                    if (Objects.equals(name, JRXMLUtils.COLUMN_HEADER)) {
                        var copy= createColumnHeaders(event, writer);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if(event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, JRXMLUtils.COLUMN_HEADER)) {
                                    event = copy;
                                    break;
                                }
                            }
                        }
                    }

                    if (Objects.equals(name, JRXMLUtils.DETAIL)) {
                        var copy= createDetails(event, writer, parser);
                        while (parser.hasNext()) {
                            event = parser.nextEvent();
                            if(event.isEndElement()) {
                                name = event.asEndElement().getName().getLocalPart();
                                if(Objects.equals(name, JRXMLUtils.DETAIL)) {
                                    event = copy;
                                    break;
                                }
                            }
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

    private XMLEvent createDetails(XMLEvent event, XMLEventWriter writer, XMLEventReader parser) throws XMLStreamException {
        StartElement oldStartElement = event.asStartElement();

        StartElement titleStartElement
                = xmlEventFactory.createStartElement(QName.valueOf(JRXMLUtils.DETAIL), null, oldStartElement.getNamespaces());
        writer.add(titleStartElement);

        int height = getHeightForDetails();
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("splitType", "Stretch"),
                        new JRXMLAttribute("isSplitAllowed", "true"),
                        new JRXMLAttribute("height", String.valueOf(height))
                ),
                xmlEventFactory);

        StartElement bandStartElement
                = xmlEventFactory.createStartElement(QName.valueOf(JRXMLUtils.BAND), iterator, titleStartElement.getNamespaces());
        writer.add(bandStartElement);

        for(int i = 0; i < ReportData.getReportColumns().size(); i++) {
            event = createTextFieldDetail(event, writer, ReportData.getReportColumns().get(i).getColumnName(),
                    JRXMLUtils.getX(i, ReportData.getReportColumns().size()),
                    JRXMLUtils.getWidth(ReportData.getReportColumns().size()),
                    getHeightForDetails());
            writer.add(event);
        }

        if(event.isEndElement()) {
            var band = xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.BAND), event.asEndElement().getNamespaces());
            writer.add(band);
            return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.COLUMN_HEADER), band.getNamespaces());
        } else {
            var band = xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.BAND), event.asStartElement().getNamespaces());
            writer.add(band);
            return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.COLUMN_HEADER), band.getNamespaces());
        }
    }

    private XMLEvent createTextFieldDetail(XMLEvent event, XMLEventWriter writer, String fieldName, int x, int width, int height) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("key", "detail_" + fieldName),
                        new JRXMLAttribute("style", "detail_" + fieldName ),
                        new JRXMLAttribute("positionType", "Float"),
                        new JRXMLAttribute("stretchType", "RelativeToTallestObject"),
                        new JRXMLAttribute("x", String.valueOf(x)),
                        new JRXMLAttribute("y", String.valueOf(0)),
                        new JRXMLAttribute("width", String.valueOf(width)),
                        new JRXMLAttribute("height", String.valueOf(height)),
                        new JRXMLAttribute("isPrintWhenDetailOverflows", "true")
                ),
                xmlEventFactory);

        return JRXMLUtils.createTextField(xmlEventFactory, event, writer, iterator,  "$F{" + fieldName + "}" , "detail_" + fieldName);
    }


    private int getHeightForDetails() {
        var columnElements = elements.stream().filter(e -> e.getJrxmlType().equals(JRXMLTypes.JRXML_DETAIL)).toList();
        int max = 0;
        for(var col : columnElements) {
            if(max < col.getHeight()) {
                max = col.getHeight();
            }

        }
        return max + max / 2;
    }

    private XMLEvent createColumnHeaders(XMLEvent event, XMLEventWriter writer) throws XMLStreamException {
        StartElement oldStartElement = event.asStartElement();

        StartElement titleStartElement
                = xmlEventFactory.createStartElement(QName.valueOf(JRXMLUtils.COLUMN_HEADER), null, oldStartElement.getNamespaces());
        writer.add(titleStartElement);

        int height = getHeightForColumnHeader();
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("splitType", "Stretch"),
                        new JRXMLAttribute("isSplitAllowed", "true"),
                        new JRXMLAttribute("height", String.valueOf(height))
                ),
                xmlEventFactory);

        StartElement bandStartElement
                = xmlEventFactory.createStartElement(QName.valueOf(JRXMLUtils.BAND), iterator, titleStartElement.getNamespaces());
        writer.add(bandStartElement);

        iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("forecolor", "#000000"),
                        new JRXMLAttribute("height", "2"),
                        new JRXMLAttribute("width", String.valueOf(515)),
                        new JRXMLAttribute("x", String.valueOf(0)),
                        new JRXMLAttribute("y", String.valueOf(height - 3))
                ),
                xmlEventFactory);
        StartElement lineStart = xmlEventFactory.createStartElement(new QName("line"), null, bandStartElement.getNamespaces());
        writer.add(lineStart);

        StartElement lineStartReport
                = xmlEventFactory.createStartElement(new QName("reportElement"), iterator, lineStart.getNamespaces());
        writer.add(lineStartReport);

        EndElement lineEndReport = xmlEventFactory.createEndElement(new QName("reportElement"), lineStartReport.getNamespaces());
        writer.add(lineEndReport);

        iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("stretchType", "NoStretch"),
                        new JRXMLAttribute("pen", "2Point")
                ),
                xmlEventFactory);
        StartElement graphElement
                = xmlEventFactory.createStartElement(new QName("graphicElement"), iterator, lineEndReport.getNamespaces());
        writer.add(graphElement);

        EndElement lineGraphEl = xmlEventFactory.createEndElement(new QName("graphicElement"), graphElement.getNamespaces());
        writer.add(lineGraphEl);

        EndElement lineEnd = xmlEventFactory.createEndElement(new QName("line"), lineGraphEl.getNamespaces());
        writer.add(lineEnd);

        for(int i = 0; i < ReportData.getReportColumns().size(); i++) {
            var column = ReportData.getReportColumns().get(i);
            var jrxmlElement = elements.stream().filter(e ->
                    e.getJrxmlType().equals(JRXMLTypes.JRXML_COLUMN_HEADER)
                            && e.getStyleName().equals("header_" + column.getColumnName())).findAny().get();
            iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("key", jrxmlElement.getStyleName()),
                            new JRXMLAttribute("style", jrxmlElement.getStyleName()),
                            new JRXMLAttribute("positionType", "Float"),
                            new JRXMLAttribute("stretchType", "RelativeToTallestObject"),
                            new JRXMLAttribute("x", String.valueOf(JRXMLUtils.getX(i, ReportData.getReportColumns().size()))),
                            new JRXMLAttribute("y", String.valueOf(0)),
                            new JRXMLAttribute("width", String.valueOf(JRXMLUtils.getWidth(ReportData.getReportColumns().size()))),
                            new JRXMLAttribute("height", String.valueOf(jrxmlElement.getHeight())),
                            new JRXMLAttribute("isPrintWhenDetailOverflows", "true")
                    ),
                    xmlEventFactory);

            event = JRXMLUtils.createTextField(xmlEventFactory, event, writer, iterator, "\"" + jrxmlElement.getStyleText() + "\"", jrxmlElement.getStyleText());
            writer.add(event);
        }

        if(event.isEndElement()) {
            var band = xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.BAND), event.asEndElement().getNamespaces());
            writer.add(band);
            return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.COLUMN_HEADER), band.getNamespaces());
        } else {
            var band = xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.BAND), event.asStartElement().getNamespaces());
            writer.add(band);
            return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.COLUMN_HEADER), band.getNamespaces());
        }


    }

    private int getHeightForColumnHeader() {
        var columnElements = elements.stream().filter(e -> e.getJrxmlType().equals(JRXMLTypes.JRXML_COLUMN_HEADER)).toList();
        int max = 0;
        for(var col : columnElements) {
            if(max < col.getHeight()) {
                max = col.getHeight();
            }

        }
        return max + max / 2 + 5;
    }


    private XMLEvent createTitle(XMLEvent event, XMLEventWriter writer, XMLEventReader parser) throws XMLStreamException {
        StartElement oldStartElement = event.asStartElement();

        StartElement titleStartElement
                = xmlEventFactory.createStartElement(QName.valueOf(JRXMLUtils.TITLE), null, oldStartElement.getNamespaces());
        writer.add(titleStartElement);

        event = createStaticText(writer, titleStartElement, 79, JRXMLTypes.JRXML_TITLE);
        writer.add(event);

        return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.TITLE), event.asEndElement().getNamespaces());

    }

    private XMLEvent createSubTitle(XMLEvent event, XMLEventWriter writer) throws XMLStreamException {
        StartElement oldStartElement = event.asStartElement();

        StartElement titleStartElement
                = xmlEventFactory.createStartElement(QName.valueOf(JRXMLUtils.PAGE_HEADER), null, oldStartElement.getNamespaces());
        writer.add(titleStartElement);

        int height = calculateHeight(elements.stream().filter(e -> e.getJrxmlType().equals(JRXMLTypes.JRXML_PAGE_HEADER)).toList());
        event = createStaticText(writer, titleStartElement, height, JRXMLTypes.JRXML_PAGE_HEADER);
        writer.add(event);

        return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.PAGE_HEADER), event.asEndElement().getNamespaces());

    }

    private XMLEvent createStaticText(XMLEventWriter writer, StartElement titleStartElement, int height, JRXMLTypes type) throws XMLStreamException {
        var iterator
                = JRXMLAttribute.generateAttributes(List.of(
                        new JRXMLAttribute("splitType", "Stretch"),
                        new JRXMLAttribute("isSplitAllowed", "true"),
                        new JRXMLAttribute("height", String.valueOf(height))
                ),
                xmlEventFactory);
        StartElement bandStartElement
                = xmlEventFactory.createStartElement(QName.valueOf(JRXMLUtils.BAND), iterator, titleStartElement.getNamespaces());
        writer.add(bandStartElement);
        var staticEvent = createAllStaticFieldsForElement(bandStartElement, writer,
                elements.stream().filter(e -> e.getJrxmlType().equals(type)).toList());
        writer.add(staticEvent);
        if(staticEvent.isStartElement()) {
            return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.BAND), staticEvent.asStartElement().getNamespaces());
        }

        else {
             return xmlEventFactory.createEndElement(QName.valueOf(JRXMLUtils.BAND), staticEvent.asEndElement().getNamespaces());
        }
    }

    private int calculateHeight(List<JRXMLElement> filteredElement) {
        int sum = 0;
        for(var element : filteredElement) {
            sum += element.getHeight();
        }
        return sum;
    }

    private int calculateY(List<JRXMLElement> elements, int index) {
        int sum = 0;
        for(int i = 0; i < elements.size(); i++) {
            if(i == index) break;
            sum += elements.get(i).getHeight();
        }
        return sum;
    }


    private XMLEvent createAllStaticFieldsForElement(XMLEvent event, XMLEventWriter writer, List<JRXMLElement> jrxmlElements) throws XMLStreamException {
        for (int i = 0; i < jrxmlElements.size(); i++) {
            var element = jrxmlElements.get(i);
            var iterator
                    = JRXMLAttribute.generateAttributes(List.of(
                            new JRXMLAttribute("style", element.getStyleName()),
                            new JRXMLAttribute("x", String.valueOf(0)),
                            new JRXMLAttribute("y",
                                    String.valueOf(calculateY(jrxmlElements, i))),
                            new JRXMLAttribute("width", String.valueOf(JRXMLUtils.PAGE_SIZE)),
                            new JRXMLAttribute("height",
                                    String.valueOf(jrxmlElements.get(i).getHeight()))
                    ),
                    xmlEventFactory);
            event = JRXMLUtils.createStaticText(xmlEventFactory, event, writer, iterator, element.getStyleText(), element.getStyleText());
            if(i != jrxmlElements.size() - 1)
                writer.add(event);
        }
        return event;
    }

    private XMLEvent createStyles(XMLEvent event, XMLEventWriter writer, JRXMLElement jrxmlElement) throws XMLStreamException {
        EndElement oldEndElement = event.asEndElement();

        StartElement newStartStyleHeaderElement
                = xmlEventFactory.createStartElement(new QName(JRXMLUtils.STYLE), jrxmlElement.getStyleAttributes(), oldEndElement.getNamespaces());
        writer.add(newStartStyleHeaderElement);

        return xmlEventFactory.createEndElement(new QName(JRXMLUtils.STYLE), newStartStyleHeaderElement.getNamespaces());
    }

    public List<JRXMLAttribute> getIteratorForStyle(String styles, String styleName, List<JRXMLAttribute> additional) {
        if(styles == null) return new ArrayList<>();
        var listOfStyles = styles.split(";");
        List<JRXMLAttribute> jrxmlAttributeList = new ArrayList<>();

        for(var stylePair : listOfStyles) {
            var styleKey = stylePair.split(":")[0];
            var styleValue  = stylePair.split(":")[1];

            var att = new JRXMLAttribute(styleKey.trim(), styleValue.trim());
            jrxmlAttributeList.add(att);
        }
        jrxmlAttributeList = JRXMLStyleAttributesCreator.generateStyles(jrxmlAttributeList);
        jrxmlAttributeList.add(new JRXMLAttribute("name", styleName));
        jrxmlAttributeList.addAll(additional);
        return jrxmlAttributeList;
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


    private void getStyleForTitle() {
        if(ReportData.getReportTitle() != null) {
            String style = document.select("#" + ReportData.getReportTitleStyleName()).attr("style");
            var mainSpan = document.select("#" + ReportData.getReportTitleStyleName()).select("span");
            style += mainSpan.attr("style");
            var spans = mainSpan.select("span");
            for (int i = 0; i < spans.size(); i++) {
                JRXMLElement jrxmlElement = new JRXMLElement();
                jrxmlElement.setJrxmlType(JRXMLTypes.JRXML_TITLE);
                jrxmlElement.setStyleName(ReportData.getReportTitleStyleName() + "_" + i);
                jrxmlElement.setStyleText(spans.get(i).ownText());
                List<JRXMLAttribute> additional = List.of(
                        new JRXMLAttribute("backcolor", JRXMLUtils.TITLE_COLOR),
                        new JRXMLAttribute("mode", "Opaque"));

                jrxmlElement.setStyleAttributes(getIteratorForStyle(style + spans.get(i).attr("style"),
                                ReportData.getReportTitleStyleName() + "_" + i, additional), xmlEventFactory);
                elements.add(jrxmlElement);
            }
        }
    }

    private void getStyleForSubitle() {
        if(ReportData.getReportSubtitle() != null) {
            String style = document.select("#" + ReportData.getReportSubTitleStyleName()).attr("style");
            var mainSpan = document.select("#" + ReportData.getReportSubTitleStyleName()).select("span");
            style += mainSpan.attr("style");
            var spans = mainSpan.select("span");
            for (int i = 0; i < spans.size(); i++) {
                JRXMLElement jrxmlElement = new JRXMLElement();
                jrxmlElement.setJrxmlType(JRXMLTypes.JRXML_PAGE_HEADER);
                jrxmlElement.setStyleName(ReportData.getReportSubTitleStyleName() + "_" + i);
                jrxmlElement.setStyleText(spans.get(i).ownText());
                List<JRXMLAttribute> additional = List.of(
                        new JRXMLAttribute("backcolor", JRXMLUtils.SUBTITLE_COLOR),
                        new JRXMLAttribute("mode", "Opaque"));

                jrxmlElement.setStyleAttributes(getIteratorForStyle(style + spans.get(i).attr("style"),
                        ReportData.getReportSubTitleStyleName() + "_" + i, additional), xmlEventFactory);
                elements.add(jrxmlElement);
            }
        }
    }

    private void getStyleForHeaders() {
        for(var columnHeader : ReportData.getReportColumns()) {
            String styleName = "#" + "header_" + columnHeader.getColumnName();
            String style = document.select(styleName).attr("style");
            var mainSpan = document.select(styleName).select("span");
            style += mainSpan.attr("style");
            var spans = mainSpan.select("span");
            JRXMLElement jrxmlElement = new JRXMLElement();
            jrxmlElement.setJrxmlType(JRXMLTypes.JRXML_COLUMN_HEADER);
            jrxmlElement.setStyleName("header_" + columnHeader.getColumnName());
            List<JRXMLAttribute> additional = List.of(new JRXMLAttribute("backcolor", "#ffffff"));

            jrxmlElement.setStyleAttributes(getIteratorForStyle(style + spans.attr("style"),
                    jrxmlElement.getStyleName(), additional), xmlEventFactory);
            jrxmlElement.setStyleText("");
            for (int i = 0; i < spans.size(); i++) {
                if(spans.get(i).ownText() != null)
                    jrxmlElement.setStyleText(jrxmlElement.getStyleText() + spans.get(i).ownText());
            }
            elements.add(jrxmlElement);
        }
    }

    private void getStyleForDetail() {
        for(var columnHeader : ReportData.getReportColumns()) {
            System.out.println(columnHeader.getColumnName());
            String styleName = "#" + "detail_" + columnHeader.getColumnName();
            String style = document.select(styleName).attr("style");
            var mainSpan = document.select(styleName).select("span");
            style += mainSpan.attr("style");
            var spans = mainSpan.select("span");
            JRXMLElement jrxmlElement = new JRXMLElement();
            jrxmlElement.setJrxmlType(JRXMLTypes.JRXML_DETAIL);
            jrxmlElement.setStyleName("detail_" + columnHeader.getColumnName());
            List<JRXMLAttribute> additional = List.of(new JRXMLAttribute("backcolor", "#ffffff"));
            jrxmlElement.setStyleAttributes(getIteratorForStyle(style + spans.attr("style"),
                    jrxmlElement.getStyleName(), additional), xmlEventFactory);
            elements.add(jrxmlElement);
        }
    }

}
