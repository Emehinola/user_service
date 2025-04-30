package com.example.user_service.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OffsetDateTimeToLocalDateTimeConverter implements AttributeConverter<OffsetDateTime, LocalDateTime> {
    @Override
    public LocalDateTime convertToDatabaseColumn(OffsetDateTime attribute) {
        return attribute == null ? null : attribute.toLocalDateTime();
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(LocalDateTime dbData) {
        return dbData == null ? null : dbData.atOffset(ZoneOffset.UTC); // or your preferred offset
    }
}
