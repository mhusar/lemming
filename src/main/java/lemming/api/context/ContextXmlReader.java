package lemming.api.context;

import lemming.api.WebApplication;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.stream.*;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to validate and read context XML data.
 */
public class ContextXmlReader implements ErrorHandler {
    /**
     * A logger named corresponding to this class.
     */
    private static final Logger logger = Logger.getLogger(ContextXmlReader.class.getName());

    /**
     * Target that produces an Ajax response
     */
    private AjaxRequestTarget target;

    /**
     * A data access object for contexts.
     */
    private transient ContextDao contextDao = new ContextDao();

    /**
     * Creates a context XML reader.
     *
     * @param target target that produces an Ajax response
     */
    public ContextXmlReader(AjaxRequestTarget target) {
        this.target = target;
    }

    /**
     * Receive notification of a recoverable error.
     *
     * @param e error information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void error(SAXParseException e) throws SAXException {
        e.printStackTrace();
    }

    /**
     * Receive notification of a non-recoverable error.
     *
     * @param e error information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        e.printStackTrace();
    }

    /**
     * Receive notification of a warning.
     *
     * @param e warning information encapsulated in a SAX parse exception
     * @throws SAXException
     */
    @Override
    public void warning(SAXParseException e) throws SAXException {
        e.printStackTrace();
    }

    /**
     * Receive notification of a XML read error.
     *
     * @param e error information encapsulated in a XML stream exception
     */
    public void onReadError(Exception e) {
        logger.log(Level.SEVERE, "Reading of context XML file failed.", e);
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
     */
    public List<Context> readXml(InputStream inputStream) {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        List<Context> contexts = new ArrayList<Context>();

        try {
            XMLEventReader reader = factory.createXMLEventReader(inputStream);
            Context context = null;

            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();

                switch (event.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = event.asStartElement();

                        if (startElement.getName().getLocalPart().equals("item")) {
                            context = getContext(startElement);
                            context.setKeyword(reader.getElementText());
                            contexts.add(context);
                        }

                        break;
                    case XMLStreamConstants.END_DOCUMENT:
                        reader.close();
                        break;
                }
            }
        } catch (XMLStreamException e) {
            onReadError(e);
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
    public void validateXml(InputStream inputStream) throws RuntimeException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            InputStream schemaStream = getSchema();
            Schema schema = schemaFactory.newSchema(new StreamSource(schemaStream));
            Validator validator = schema.newValidator();

            validator.setErrorHandler(this);
            validator.validate(new StreamSource(inputStream));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Validation of context XML file failed.", e);
            throw new IllegalStateException(e);
        }
    }
}
