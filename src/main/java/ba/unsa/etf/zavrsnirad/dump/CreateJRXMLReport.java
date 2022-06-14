package ba.unsa.etf.zavrsnirad.dump;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


public class CreateJRXMLReport {


    public static String createReport(String htmlContent) {
        Document document = Jsoup.parse( htmlContent);
        Elements elements = document.select("td");
//        for (var element : elements) {
//            element.cssSelector();
//        }
        elements.size();
        return String.valueOf(elements.get(4).attr("style"));
    }



}
