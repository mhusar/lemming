package lemming.context;

import lemming.WebApplication;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
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
        throw (exception);
    }

    /**
     * Receive notification of a non-recoverable error.
     *
     * @param exception error information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        throw (exception);
    }

    /**
     * Receive notification of a warning.
     *
     * @param exception warning information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void warning(SAXParseException exception) throws SAXException {
        throw (exception);
    }

    /**
     * Creates a context from obtained from information of the start element interface.
     *
     * @param element start element of a tag
     * @return A context object.
     */
    private Context getContext(StartElement element) {
        Context context = new Context();

        for (Iterator<?> attributes = element.getAttributes(); attributes.hasNext();) {
            Attribute attribute = (Attribute) attributes.next();

            switch (attribute.getName().getLocalPart()) {
                case "following":
                    context.setFollowing(attribute.getValue());
                    break;
                case "location":
                    context.setLocation(attribute.getValue());
                    break;
                case "preceding":
                    context.setPreceding(attribute.getValue());
                    break;
                case "type":
                    String attributeValue = attribute.getValue();

                    if (attributeValue.equals("rubric_item")) {
                        context.setType(ContextType.Type.RUBRIC);
                    } else if (attributeValue.equals("seg_item")) {
                        context.setType(ContextType.Type.SEGMENT);
                    }

                    break;
            }
        }

        return context;
    }

    /**
     * Reads context XML from an input stream.
     *
     * @param inputStream input stream
     * @return A list of contexts or null.
     */
    public List<Context> readXml(InputStream inputStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        List<Context> contexts = new ArrayList<Context>();
        XMLEventReader reader = factory.createXMLEventReader(inputStream);
        String currentElement = "";
        Context context = null;

        while (reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            switch (event.getEventType()) {
                case XMLStreamConstants.START_ELEMENT:
                    StartElement startElement = event.asStartElement();

                    if (startElement.getName().getLocalPart().equals("item")) {
                        currentElement = "item";
                        context = getContext(startElement);
                    } else if (startElement.getName().getLocalPart().equals("punctuation")) {
                        currentElement = "punctuation";
                    }

                    break;
                case XMLStreamConstants.END_ELEMENT:
                    EndElement endElement = event.asEndElement();
                    currentElement = "";

                    if (endElement.getName().getLocalPart().equals("item")) {
                        contexts.add(context);
                    }

                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    reader.close();
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();

                    if (currentElement.equals("item")) {
                        context.setKeyword(characters.getData());
                    } else if (currentElement.equals("punctuation")) {
                        context.setPunctuation(characters.getData());
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
}
