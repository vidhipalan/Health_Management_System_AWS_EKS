package edu.stevens.cs548.clinic.rest;

import com.google.gson.Gson;
import edu.stevens.cs548.clinic.service.dto.util.GsonFactory;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GsonContextResolver implements ContextResolver<Gson> {
    @Override
    public Gson getContext(Class<?> aClass) {
        return GsonFactory.createGson();
    }
}
