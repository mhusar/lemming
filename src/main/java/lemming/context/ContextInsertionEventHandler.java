package lemming.context;

import com.google.common.xml.XmlEscapers;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;

/**
 * Context reference stream insertion event handler.
 */
public class ContextInsertionEventHandler implements ReferenceInsertionEventHandler {
    /**
     * A call-back which is executed during Velocity merge before a reference value is inserted into the output stream.
     *
     * @param reference reference from template about to be inserted
     * @param value value about to be inserted (after its toString() method is called)
     * @return Object on which toString() should be called for output.
     */
    @Override
    public Object referenceInsert(String reference, Object value) {
        if (value == null) {
            return null;
        }

        if (reference.equals("keyword") || reference.equals("punctuation")) {
            return XmlEscapers.xmlContentEscaper().escape(value.toString());
        } else {
            return XmlEscapers.xmlAttributeEscaper().escape(value.toString()).replace("&apos;", "'");
        }
    }
}
