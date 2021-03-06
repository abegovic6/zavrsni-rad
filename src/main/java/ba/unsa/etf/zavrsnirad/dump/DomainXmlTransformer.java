//package ba.unsa.etf.zavrsnirad.dump;
//import ba.unsa.etf.zavrsnirad.utils.XmlCreator;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.FileNotFoundException;
//import java.io.EOFException;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//import java.util.HashSet;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.xml.stream.XMLInputFactory;
//import javax.xml.stream.XMLOutputFactory;
//import javax.xml.stream.XMLEventFactory;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.XMLEventReader;
//import javax.xml.stream.XMLEventWriter;
//import javax.xml.stream.events.XMLEvent;
//import javax.xml.stream.events.StartElement;
//import javax.xml.stream.events.EndElement;
//import javax.xml.stream.events.Attribute;
//import javax.xml.namespace.QName;
//
///**
// */
//public class DomainXmlTransformer {
//
//    private File in;
//    private File out;
//    private final XMLEventFactory xmlEventFactory = XMLEventFactory.newInstance();
//    private final XMLOutputFactory xof = XMLOutputFactory.newInstance();
//
//    private Logger _logger = Logger.getAnonymousLogger(
//            "com.sun.logging.enterprise.system.container.ejb.LogStrings");
//
//    private static final String VIRTUAL_SERVER = "virtual-server"; // should not skip
//    private static final String NETWORK_LISTENERS = "network-listeners";
//    private static final String IIOP_LISTENER = "iiop-listener";
//    private static final String PROTOCOLS = "protocols";
//    private static final String APPLICATIONS = "applications";
//    private static final String APPLICATION_REF = "application-ref";
//    private static final String CLUSTERS = "clusters";
//    private static final String JMS_HOST = "jms-host";
//    private static final String JMX_CONNECTOR = "jmx-connector";
//    private static final String LAZY_INIT_ATTR = "lazy-init";
//    private static final String ADMIN_SERVICE = "admin-service";
//    private static final String DAS_CONFIG = "das-config";
//    private static final String DYNAMIC_RELOAD_ENABLED = "dynamic-reload-enabled";
//    private static final String JAVA_CONFIG = "java-config";
//    private static final String JVM_OPTIONS = "jvm-options";
//    private static final String INITIALIZE_ON_DEMAND = "-Dorg.glassfish.jms.InitializeOnDemand=true";
//    private static final String ENABLED = "enabled";
//    private static final String FALSE = "false";
//    private static final String TRUE = "true";
//
//    private static final Set<String> SKIP_ELEMENTS = new HashSet(Arrays.asList(APPLICATION_REF));
//    private static final Set<String> EMPTY_ELEMENTS = new HashSet(Arrays.asList(NETWORK_LISTENERS, PROTOCOLS, APPLICATIONS, CLUSTERS));
//    private static final Set<String> EMPTY_ELEMENTS_KEEP_PORTS = new HashSet(Arrays.asList(APPLICATIONS, CLUSTERS));
//    private static final Set<String> SKIP_SETTINGS_ELEMENTS = new HashSet(Arrays.asList(IIOP_LISTENER));
//    private static final Set<String> DISABLE_ELEMENTS = new HashSet(Arrays.asList(JMX_CONNECTOR));
//    private static final Set<String> DISABLE_SUB_ELEMENTS = new HashSet(Arrays.asList(LAZY_INIT_ATTR));
//
//    public DomainXmlTransformer(File domainXml) {
//        in = domainXml;
//    }
//
//    public DomainXmlTransformer(File domainXml, Logger logger) {
//        in = domainXml;
//        _logger = logger;
//    }
//
//    public File transform(boolean keepPorts) {
//        FileInputStream fis = null;
//        FileOutputStream fos = null;
//        XMLEventReader parser = null;
//        XMLEventWriter writer = null;
//        XMLInputFactory xif =
//                (XMLInputFactory.class.getClassLoader() == null) ?
//                        XMLInputFactory.newInstance() :
//                        XMLInputFactory.newInstance(XMLInputFactory.class.getName(),
//                                XMLInputFactory.class.getClassLoader());
//
//        Set<String> empty_elements = (keepPorts)? EMPTY_ELEMENTS_KEEP_PORTS : EMPTY_ELEMENTS;
//        try {
//            fis = new FileInputStream(in);
//            out = File.createTempFile("domain", "xml");
//            if (_logger.isLoggable(Level.FINE)) {
//                _logger.fine("[DomainXmlTransformer] Creating temp domain file: " + out);
//            }
//
//            fos = new FileOutputStream(out);
//            parser = xif.createXMLEventReader(fis);
//
//            writer = xof.createXMLEventWriter(fos);
//            boolean fixedDasConfig = false;
//            while (parser.hasNext()) {
//                XMLEvent event = parser.nextEvent();
//                if (event.isStartElement()) {
//                    String name = event.asStartElement().getName().getLocalPart();
//                    if (SKIP_ELEMENTS.contains(name)) {
//                        if (_logger.isLoggable(Level.FINE)) {
//                            _logger.fine("[DomainXmlTransformer] Skipping all of: " + name);
//                        }
//                        getEndEventFor(parser, name);
//                        continue;
//                    }
//
//                    boolean skip_to_end = false;
//                    if (empty_elements.contains(name)) {
//                        if (_logger.isLoggable(Level.FINE)) {
//                            _logger.fine("[DomainXmlTransformer] Skipping details of: " + name);
//                        }
//                        skip_to_end = true;
//                    } else if (SKIP_SETTINGS_ELEMENTS.contains(name)) {
//                        // Make sure lazy init is not enabled by creating a new start element
//                        // based on the original but that never includes the lazy init attribute
//                        event  = getSkippedElementStartEvent(event);
//                        skip_to_end = true;
//                    } else if (DISABLE_ELEMENTS.contains(name)) {
//                        // Disable this element
//                        event  = getReplaceAttributeInStartEvent(event, ENABLED, FALSE);
//                        skip_to_end = true;
//                    } else if (JMS_HOST.equals(name)) {
//                        // Set lazy-init to false
//                        event  = getReplaceAttributeInStartEvent(event, LAZY_INIT_ATTR, FALSE);
//                        skip_to_end = true;
//                    } else if (DAS_CONFIG.equals(name)) {
//                        // Set dynamic-reload-enabled to false
//                        event  = getReplaceAttributeInStartEvent(event, DYNAMIC_RELOAD_ENABLED, FALSE);
//                        fixedDasConfig = true;
//                        skip_to_end = true;
//                    } else if (JAVA_CONFIG.equals(name)) {
//                        // Add jvm-options
//                        writer.add(event);
//                        event = getAddedEvent(event, writer, JVM_OPTIONS, INITIALIZE_ON_DEMAND);
//                    }
//
//                    if (skip_to_end) {
//                        writer.add(event);
//                        event = getEndEventFor(parser, name);
//                    }
//                } else if (event.isEndElement()) {
//                    String name = event.asEndElement().getName().getLocalPart();
//                    if (ADMIN_SERVICE.equals(name)) {
//                        if (!fixedDasConfig) {
//                            writer.add(getAddedEventBeforeEndElement(event, writer, DAS_CONFIG, DYNAMIC_RELOAD_ENABLED, FALSE));
//                        }
//                        fixedDasConfig = false; // for the next config
//                    }
//                }
//                if (_logger.isLoggable(Level.FINEST)) {
//                    _logger.finest("[DomainXmlTransformer] Processing: " + event);
//                }
//                writer.add(event);
//            }
//            writer.flush();
//            writer.close();
//
//        } catch (Exception e) {
//            _logger.log(Level.SEVERE, "ejb.embedded.tmp_file_create_error", e.getMessage());
//            _logger.log(Level.FINE, e.getMessage(), e);
//            return null;
//        } finally {
//            XmlCreator.closeConnections(fis, fos, parser, writer);
//        }
//
//        if (_logger.isLoggable(Level.FINE)) {
//            _logger.fine("[DomainXmlTransformer] Created temp domain file: " + out);
//        }
//        return out;
//    }
//
//    private XMLEvent getEndEventFor(XMLEventReader parser, String name)
//            throws XMLStreamException {
//        while (parser.hasNext()) {
//            XMLEvent event = parser.nextEvent();
//            if (event.isEndElement()
//                    && event.asEndElement().getName().getLocalPart().equals(name)) {
//                if (_logger.isLoggable(Level.FINEST)) {
//                    _logger.finest("[DomainXmlTransformer] END: " + name);
//                }
//                return event;
//            }
//        }
//        return null;
//    }
//
//    /** Create a new start element based on the original but that does not include
//     * the specified attribute.
//     */
//    private StartElement getSkippedElementStartEvent(XMLEvent event) {
//        Set attributes = new HashSet();
//
//        for(java.util.Iterator i = event.asStartElement().getAttributes(); i.hasNext();) {
//            Attribute a = (Attribute) i.next();
//            if( !DISABLE_SUB_ELEMENTS.contains(a.getName().getLocalPart())) {
//                attributes.add(a);
//            }
//        }
//
//        StartElement oldStartEvent = event.asStartElement();
//        return xmlEventFactory.createStartElement(oldStartEvent.getName(),
//                attributes.iterator(), oldStartEvent.getNamespaces());
//    }
//
//    /** Write a new element with the specified name and text
//     * @return the end element
//     */
//    private XMLEvent getAddedEvent(XMLEvent event, XMLEventWriter writer, String elementName,
//                                   String text) throws XMLStreamException {
//        StartElement oldStartEvent = event.asStartElement();
//        StartElement newStartEvent = xmlEventFactory.createStartElement(new QName(elementName),
//                null, oldStartEvent.getNamespaces());
//
//        writer.add(newStartEvent);
//        writer.add(xmlEventFactory.createCharacters(text));
//        return xmlEventFactory.createEndElement(newStartEvent.getName(), newStartEvent.getNamespaces());
//    }
//
//    /** Write a new element with the specified name and attribute before the end element is written out
//     * @return the end element
//     */
//    private XMLEvent getAddedEventBeforeEndElement(XMLEvent event, XMLEventWriter writer, String elementName,
//                                                   String attributeName, String attributeValue) throws XMLStreamException {
//        Attribute newAttribute = xmlEventFactory.createAttribute(attributeName, attributeValue);
//        Set attributes = new HashSet();
//        attributes.add(newAttribute);
//
//        EndElement oldEvent = event.asEndElement();
//        StartElement newStartEvent = xmlEventFactory.createStartElement(new QName(elementName),
//                attributes.iterator(), oldEvent.getNamespaces());
//
//        writer.add(newStartEvent);
//        return xmlEventFactory.createEndElement(newStartEvent.getName(), newStartEvent.getNamespaces());
//    }
//
//    /** Create a new start element based on the original but that replaces attribute value
//     */
//    private StartElement getReplaceAttributeInStartEvent(XMLEvent event, String attr_name, String attr_value) {
//        Set attributes = new HashSet();
//
//        for(java.util.Iterator i = event.asStartElement().getAttributes(); i.hasNext();) {
//            Attribute a = (Attribute) i.next();
//            if( !a.getName().getLocalPart().equals(attr_name) ) {
//                attributes.add(a);
//            }
//        }
//
//        Attribute newAttribute = xmlEventFactory.createAttribute(attr_name, attr_value);
//        attributes.add(newAttribute);
//
//        StartElement oldStartEvent = event.asStartElement();
//        return xmlEventFactory.createStartElement(oldStartEvent.getName(),
//                attributes.iterator(), oldStartEvent.getNamespaces());
//    }
//}
