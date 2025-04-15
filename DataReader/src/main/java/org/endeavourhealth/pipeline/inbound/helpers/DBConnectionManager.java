package org.endeavourhealth.pipeline.inbound.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.pipeline.inbound.config.EventDBConfig;
import org.endeavourhealth.pipeline.inbound.config.InstanceDBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Properties;

public class DBConnectionManager {

  private static final Logger LOG = LoggerFactory.getLogger(DBConnectionManager.class);
  private static Connection eventConnection = null;
  private static Connection instanceConnection = null;
  private static HashMap<String, PreparedStatement> upsertCache = new HashMap<>();

  private DBConnectionManager() {
    throw new IllegalStateException("Utility class");
  }

  private static Connection getEventConnection() throws SQLException {
    if (eventConnection == null) {
      Properties props = new Properties();
      props.setProperty("user", EventDBConfig.getSpringEventDatasourceUsername());
      props.setProperty("password", EventDBConfig.getSpringEventDatasourcePassword());
      eventConnection = DriverManager.getConnection(EventDBConfig.getSpringEventDatasourceUrl(), props);
    }
    return eventConnection;
  }

  private static Connection getInstanceConnection() throws SQLException {
    if (instanceConnection == null) {
      Properties props = new Properties();
      props.setProperty("user", InstanceDBConfig.getSpringInstanceDatasourceUsername());
      props.setProperty("password", InstanceDBConfig.getSpringInstanceDatasourcePassword());
      instanceConnection = DriverManager.getConnection(InstanceDBConfig.getSpringInstanceDatasourceUrl(), props);
    }
    return instanceConnection;
  }

  private static PreparedStatement prepareEventUpsert() throws SQLException {
    return getEventConnection().prepareStatement("INSERT INTO event (id, json) VALUES (?,?) ON CONFLICT (id) DO UPDATE SET json=?::json");
  }

  private static PreparedStatement prepareInstanceUpsert(String datatype) throws SQLException {
    if (!datatype.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))  // check for SQL injection
      throw new IllegalArgumentException("Invalid table name: " + datatype);

    return getInstanceConnection().prepareStatement("INSERT INTO datatype (id, json) VALUES (?,?) ON CONFLICT (id) DO UPDATE SET json=?::json".replace("datatype", datatype));
  }

  private static PreparedStatement prepareInstanceCreateTable(String datatype) throws SQLException {
    if (!datatype.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))  // check for SQL injection
      throw new IllegalArgumentException("Invalid table name: " + datatype);

    return getInstanceConnection().prepareStatement("""
      CREATE OR REPLACE FUNCTION json_date(text)
        RETURNS timestamptz AS
      $$SELECT to_timestamp($1, 'YYYY-MM-DDTHH24:MI:SS')$$
        LANGUAGE sql IMMUTABLE;
      
      
      CREATE TABLE datatype (
            id UUID PRIMARY KEY,
            json JSON NOT NULL,
            type text generated always as (json ->> '@type') stored
            );
      
            CREATE INDEX idx_datatype_pat_dob ON datatype ((json_date(json ->> 'dateOfBirth'))) WHERE type = 'Patient';
            CREATE INDEX idx_datatype_pat_nhs ON datatype ((json ->> 'nhsNumber')) WHERE type = 'Patient';
      """.replaceAll("datatype", datatype));
  }

  private static PreparedStatement getUpsert(String category, String datatype) throws SQLException {
    if ("EVENT".equals(category)) {
      return prepareEventUpsert();
    } else if ("INSTANCE".equals(category)) {
      PreparedStatement returnUpsert = upsertCache.get(datatype);
      if (returnUpsert == null) {
        returnUpsert = prepareInstanceUpsert(datatype);
        upsertCache.put(datatype, returnUpsert);
      }
      return returnUpsert;
    } else {
      throw new IllegalArgumentException("Provided category header '" + category + "' is invalid");
    }
  }

  private static boolean createNewInstanceRelation(String datatype) throws SQLException {
    PreparedStatement relationCreate = prepareInstanceCreateTable(datatype);
    try {
      relationCreate.execute();
      return true;
    } catch (Exception e) {
      LOG.debug(e.getMessage());
      return false;
    }
  }

  public static int fileEntity(String category, JsonNode entity, String datatype) throws SQLException {
    PreparedStatement upsert = getUpsert(category, datatype);
    upsert.setObject(1, entity.get("@id").asText(), Types.OTHER);
    upsert.setObject(2, entity, Types.OTHER);
    upsert.setObject(3, entity, Types.OTHER);
    try {
      int rows = upsert.executeUpdate();
      LOG.debug("{} filed to database", category);
      return rows;
    } catch (Exception e) {
      if (category.equals("INSTANCE") && e.getMessage().contains("ERROR: relation") && e.getMessage().contains("does not exist")) {
        LOG.debug("Relation not found. Creating it and trying again.");
        boolean created = createNewInstanceRelation(datatype);
        if (created) {
          LOG.debug("Relation created successfully.");
          int rows = upsert.executeUpdate();
          LOG.debug("{} refiled to database", category);
          return rows;
        }
      }
      LOG.debug(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  public static void clearCache() {
    upsertCache = new HashMap<>();
  }
}
