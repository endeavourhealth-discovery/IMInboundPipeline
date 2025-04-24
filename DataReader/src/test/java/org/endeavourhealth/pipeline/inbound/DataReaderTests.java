package org.endeavourhealth.pipeline.inbound;

import org.endeavourhealth.pipeline.inbound.helpers.DBConnectionManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataReaderTests {
  DBConnectionManager dbConnectionManager;

  @Test
  void testInvalidPrepareUpsertDatatypeFails() {
    String invalidDatatype = "{invalidDatatype}";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
      DBConnectionManager.prepareInstanceUpsert(invalidDatatype));
    assertEquals("Invalid table name: " + invalidDatatype, exception.getMessage());
  }

  @Test
  void testInvalidPrepareInstanceUpsertTableDatatypeFails() {
    String invalidDatatype = "{invalidDatatype}";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
      DBConnectionManager.prepareInstanceCreateTable(invalidDatatype));
    assertEquals("Invalid table name: " + invalidDatatype, exception.getMessage());
  }

  @Test
  void testInvalidUpsertCategoryFails() {
    String invalidCategory = "invalid";
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
      DBConnectionManager.getUpsert(invalidCategory, ""));
    assertEquals("Provided category header '" + invalidCategory + "' is invalid", exception.getMessage());
  }

}
