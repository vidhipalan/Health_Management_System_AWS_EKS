package edu.stevens.cs548.clinic.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter(autoApply = true)
public class UuidConverter implements AttributeConverter<UUID, String> {

    @Override
    public String convertToDatabaseColumn(final UUID entityValue) {
        if (entityValue ==  null) {
            return null;
        } else {
            return entityValue.toString();
        }
    }

    @Override
    public UUID convertToEntityAttribute(final String databaseValue) {
        if (databaseValue ==  null) {
            return null;
        } else {
            return UUID.fromString(databaseValue);
        }
    }
}
