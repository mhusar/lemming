package lemming;

import lemming.auth.AuthRequestFilter;
import lemming.context.ContextResource;
import lemming.lemma.LemmaResource;
import lemming.pos.PosResource;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.filter.EncodingFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.ws.rs.ApplicationPath;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationPath("/api")
public class Application extends ResourceConfig {
    public Application() {
        Logger logger = Logger.getLogger(Application.class.getName());

        // configure context resource logging
        ResourceConfig contextResourceConfig = new ResourceConfig(ContextResource.class);
        contextResourceConfig.register(new LoggingFeature(logger, Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY,
                LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        // enable gzip encoder for context resource
        EncodingFilter.enableFor(contextResourceConfig, GZipEncoder.class);

        // configure lemma resource logging
        ResourceConfig lemmaResourceConfig = new ResourceConfig(LemmaResource.class);
        lemmaResourceConfig.register(new LoggingFeature(logger, Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY,
                LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        // enable gzip encoder for lemma resource
        EncodingFilter.enableFor(lemmaResourceConfig, GZipEncoder.class);

        // configure pos resource logging
        ResourceConfig posResourceConfig = new ResourceConfig(PosResource.class);
        posResourceConfig.register(new LoggingFeature(logger, Level.ALL, LoggingFeature.Verbosity.PAYLOAD_ANY,
                LoggingFeature.DEFAULT_MAX_ENTITY_SIZE));
        // enable gzip encoder for pos resource
        EncodingFilter.enableFor(posResourceConfig, GZipEncoder.class);

        packages("lemming.context");
        packages("lemming.lemma");
        packages("lemming.pos");
        // disable buffering
        property(ServerProperties.OUTBOUND_CONTENT_LENGTH_BUFFER, 0);
        register(JacksonFeature.class);
        // enable logging
        register(LoggingFeature.class);
        // enable user roles
        register(AuthRequestFilter.class);
        register(RolesAllowedDynamicFeature.class);
        // enable gzip encoding
        register(EncodingFilter.class);
        register(GZipEncoder.class);
    }
}
