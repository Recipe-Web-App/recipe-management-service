package com.recipe_manager.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.recipe_manager.exception.RevisionSerializationException;
import com.recipe_manager.model.dto.revision.AbstractRevision;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

/**
 * JPA AttributeConverter for converting between AbstractRevision objects and JSON strings for
 * storage in JSONB database columns.
 */
@Converter
@Slf4j
public class RevisionDataConverter implements AttributeConverter<AbstractRevision, String> {

  private static final ObjectMapper objectMapper =
      new ObjectMapper().registerModule(new JavaTimeModule());

  @Override
  public String convertToDatabaseColumn(AbstractRevision attribute) {
    if (attribute == null) {
      return null;
    }

    try {
      String json = objectMapper.writeValueAsString(attribute);
      log.debug("Converted revision to JSON: {}", json);
      return json;
    } catch (JsonProcessingException e) {
      log.error("Error converting revision to JSON", e);
      throw new RevisionSerializationException("Failed to convert revision to JSON", e);
    }
  }

  @Override
  public AbstractRevision convertToEntityAttribute(String dbData) {
    if (dbData == null || dbData.trim().isEmpty()) {
      return null;
    }

    try {
      AbstractRevision revision = objectMapper.readValue(dbData, AbstractRevision.class);
      log.debug("Converted JSON to revision: {}", revision);
      return revision;
    } catch (JsonProcessingException e) {
      log.error("Error converting JSON to revision: {}", dbData, e);
      throw new RevisionSerializationException("Failed to convert JSON to revision", e);
    }
  }
}
