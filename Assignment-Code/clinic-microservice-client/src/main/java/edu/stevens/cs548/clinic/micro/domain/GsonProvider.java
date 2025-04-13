package edu.stevens.cs548.clinic.micro.domain;

import com.google.gson.Gson;
import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * A custom JSON provider for the Web service, using Gson instead of Moxy, the reference implementation
 * of JSON-B. The app server scans the classes and sees the @Provider annotation.
 *
 * https://eclipsesource.com/blogs/2012/11/02/integrating-gson-into-a-jax-rs-based-application/
 */

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonProvider implements MessageBodyWriter<Object>,
        MessageBodyReader<Object> {

    private static final Logger logger = Logger.getLogger(GsonProvider.class.getCanonicalName());

    private static final String UTF_8 = "UTF-8";

    private static final Gson gson = GsonFactory.createGson();

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(Class<Object> type, Type genericType,
                           Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException {
		logger.info(String.format("Reading object of type %s (generic type %s", type.getName(), genericType.getTypeName()));
        InputStreamReader streamReader = new InputStreamReader(entityStream, UTF_8);
        try {
            return gson.fromJson(streamReader, genericType);
        } catch (com.google.gson.JsonSyntaxException e) {
            logger.log(Level.SEVERE, "Syntax error reading JSON!", e);
        } finally {
            streamReader.close();
        }
        return null;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(Object object, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(Object object, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException,
            WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
        try {
            gson.toJson(object, genericType, writer);
        } finally {
            writer.close();
        }
    }
}
