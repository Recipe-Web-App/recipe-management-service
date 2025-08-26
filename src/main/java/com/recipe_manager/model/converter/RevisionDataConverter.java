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
 * JPA Attribute Converter for AbstractRevision objects. Handles conversion between AbstractRevision
 * objects and their JSON string representation for storage in JSONB database columns.
 */
@Converter
@Slf4j
public final class RevisionDataConverter implements AttributeConverter<AbstractRevision, String> {

  /** ObjectMapper instance configured with JavaTimeModule for JSON processing. */
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().registerModule(new JavaTimeModule());

  /**
   * Converts the value stored in the entity attribute into the data representation to be stored in
   * the database.
   *
   * @param attribute the entity attribute value to be converted
   * @return the converted data to be stored in the database column
   */
  @Override
  public String convertToDatabaseColumn(final AbstractRevision attribute) {
    if (attribute == null) {
      return null;
    }

    try {
      String json = OBJECT_MAPPER.writeValueAsString(attribute);
      log.debug("Converted revision to JSON: {}", json);
      return json;
    } catch (JsonProcessingException e) {
      log.error("Error converting revision to JSON", e);
      throw new RevisionSerializationException("Failed to convert revision to JSON", e);
    }
  }

  /**
   * Converts the data stored in the database column into the value to be stored in the entity
   * attribute.
   *
   * @param dbData the data from the database column to be converted
   * @return the converted value to be stored in the entity attribute
   */
  @Override
  public AbstractRevision convertToEntityAttribute(final String dbData) {
    if (dbData == null || dbData.trim().isEmpty()) {
      return null;
    }

    try {
      AbstractRevision revision = OBJECT_MAPPER.readValue(dbData, AbstractRevision.class);
      log.debug("Converted JSON to revision: {}", revision);
      return revision;
    } catch (JsonProcessingException e) {
      log.error("Error converting JSON to revision: {}", dbData, e);
      throw new RevisionSerializationException("Failed to convert JSON to revision", e);
    }
  }
}
