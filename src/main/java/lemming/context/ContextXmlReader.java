package lemming.context;

import lemming.WebApplication;
import lemming.context.inbound.InboundContext;
import org.apache.wicket.model.StringResourceModel;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class to validate and read context XML data.
 */
public class ContextXmlReader implements ErrorHandler {
    /**
     * Receive notification of a recoverable error.
     *
     * @param exception error information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void error(SAXParseException exception) throws SAXException {
        if (exception != null) {
            throw (exception);
        }
    }

    /**
     * Receive notification of a non-recoverable error.
     *
     * @param exception error information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        if (exception != null) {
            throw (exception);
        }
    }

    /**
     * Receive notification of a warning.
     *
     * @param exception warning information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        if (exception != null) {
            throw (exception);
        }
    }

    /**
     * Creates a context from obtained from information of the start element interface.
     *
     * @param element start element of a tag
     * @return A context object.
     */
    private InboundContext createContext(StartElement element) {
        InboundContext context = new InboundContext();

        for (Iterator<?> attributes = element.getAttributes(); attributes.hasNext(); ) {
            Attribute attribute = (Attribute) attributes.next();
            String value = (attribute.getValue() != null) ? attribute.getValue() : "";

            switch (attribute.getName().getLocalPart()) {
                case "following":
                    context.setFollowing(value);
                    break;
                case "location":
                    context.setLocation(value);
                    break;
                case "n":
                    context.setNumber(Integer.valueOf(value));
                    break;
                case "preceding":
                    context.setPreceding(value);
                    break;
                case "type":
                    if (value.equals("rubric_item")) {
                        context.setType(ContextType.Type.RUBRIC);
                    } else if (value.equals("seg_item")) {
                        context.setType(ContextType.Type.SEGMENT);
                    } else if (value.equals("verse_item")) {
                        context.setType(ContextType.Type.VERSE);
                    }

                    break;
                case "sp":
                    if (value.equals("direct")) {
                        context.setSpeech(SpeechType.Type.DIRECT);
                    } else if (value.equals("indirect")) {
                        context.setSpeech(SpeechType.Type.INDIRECT);
                    } else if (value.equals("")) {
                        context.setSpeech(SpeechType.Type.NONE);
                    }

                    break;
            }
        }

        return context;
    }

    /**
     * Validates the order of punctuation tags in a context item.
     *
     * @param context                current context object
     * @param location               location of the event
     * @param lastPunctuationType    punctuation type last seen
     * @param currentPunctuationType current punctuation type
     */
    private void validatePunctuation(InboundContext context, Location location, String lastPunctuationType,
                                     String currentPunctuationType) throws XmlStreamException {
        String message = null;

        if (lastPunctuationType.equals(currentPunctuationType)) {
            message = new StringResourceModel("ContextXmlReader.duplicate-punctuation-type").getString();
        } else if (lastPunctuationType.equals("end") && currentPunctuationType.equals("init")) {
            message = new StringResourceModel("ContextXmlReader.punctuation-type-incorrect-order").getString();
        } else if (context.getKeyword() != null && currentPunctuationType.equals("init")) {
            message = new StringResourceModel("ContextXmlReader.init-punctuation-after-keyword").getString();
        } else if (context.getKeyword() == null && currentPunctuationType.equals("end")) {
            message = new StringResourceModel("ContextXmlReader.end-punctuation-before-keyword").getString();
        }

        if (message != null) {
            throw new XmlStreamException(message, location);
        }
    }

    /**
     * Reads context XML from an input stream.
     *
     * @param inputStream input stream
     * @return A list of contexts or null.
     */
    public List<InboundContext> readXml(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        List<InboundContext> contexts = new ArrayList<>();
        XMLEventReader reader = factory.createXMLEventReader(inputStream);
        String currentElementName = "";
        String punctuationType = "";
        InboundContext context = null;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals("item")) {
                        currentElementName = "item";
                        context = createContext(startElement);
                    } else if (startElement.getName().getLocalPart().equals("punctuation")) {
                        currentElementName = "punctuation";
                        String currentPunctuationType = startElement.getAttributeByName(new QName("type")).getValue();
                        // validates the order of punctuation tags in a context item
                        validatePunctuation(context, event.getLocation(), punctuationType, currentPunctuationType);
                        punctuationType = currentPunctuationType;
                    } else if (startElement.getName().getLocalPart().equals("string")) {
                        currentElementName = "string";
                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    currentElementName = "";

                    if (endElement.getName().getLocalPart().equals("item")) {
                        contexts.add(context);
                        punctuationType = "";
                    }

                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    reader.close();
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    assert context != null;

                    if (currentElementName.equals("punctuation")) {
                        if (punctuationType.equals("init")) {
                            context.setInitPunctuation(characters.getData());
                        } else if (punctuationType.equals("end")) {
                            context.setEndPunctuation(characters.getData());
                        }
                    } else if (currentElementName.equals("string")) {
                        context.setKeyword(characters.getData());
                    }

                    break;
            }
        }

        return contexts;
    }

    /**
     * Returns an input stream of the context schema.
     *
     * @return An input stream.
     */
    private InputStream getSchema() {
        ServletContext context = WebApplication.get().getServletContext();
        return context.getResourceAsStream("/WEB-INF/schema/context.xsd");
    }

    /**
     * Validates content XML data delivered a input stream
     *
     * @param inputStream input stream
     */
    public void validateXml(InputStream inputStream) throws IOException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        InputStream schemaStream = getSchema();
        Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
        Validator validator = schema.newValidator();

        validator.setErrorHandler(this);
        validator.validate(new StreamSource(inputStream));
    }

    /**
     * A XMLStreamException which doesn’t mess up the message text.
     * <p>
     * original code:
     * super("ParseError at [row,col]:["+location.getLineNumber()+","+
     * location.getColumnNumber()+"]\n"+
     * "Message: "+msg);
     */
    public class XmlStreamException extends XMLStreamException {
        /**
         * Creates a XmlStreamException.
         *
         * @param message exception message
         */
        public XmlStreamException(String message) {
            super(message);
        }

        /**
         * Creates a XmlStreamException.
         *
         * @param message  exception message
         * @param location location of the error
         */
        public XmlStreamException(String message, Location location) {
            super(message);
            super.location = location;
        }
    }
}
