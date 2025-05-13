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

  public static PreparedStatement prepareInstanceUpsert(String datatype) throws SQLException {
    if (!datatype.matches("^[a-zA-Z_]\\w*$"))  // check for SQL injection
      throw new IllegalArgumentException("Invalid table name: " + datatype);

    return getInstanceConnection().prepareStatement("INSERT INTO %s (id, json) VALUES (?,?) ON CONFLICT (id) DO UPDATE SET json=?::json".formatted(datatype));
  }

  public static PreparedStatement prepareInstanceCreateTable(String datatype) throws SQLException {
    if (!datatype.matches("^[a-zA-Z_]\\w*$"))  // check for SQL injection
      throw new IllegalArgumentException("Invalid table name: " + datatype);

    return getInstanceConnection().prepareStatement("""      
      CREATE TABLE %s (
            id UUID PRIMARY KEY,
            json JSON NOT NULL,
            type text generated always as (json ->> 'type') stored
            );
            
            CREATE INDEX idx_%s_pat_dob ON %s ((json_date(json ->> 'dateOfBirth'))) WHERE type = 'Patient';
            CREATE INDEX idx_%s_pat_nhs ON %s ((json ->> 'nhsNumber')) WHERE type = 'Patient';
      """.formatted(datatype, datatype, datatype, datatype, datatype));
  }

  public static PreparedStatement getUpsert(String category, String datatype) throws SQLException {
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

  private static boolean createNewInstanceRelation(String datatype) {
    try (PreparedStatement relationCreate = prepareInstanceCreateTable(datatype)) {
      relationCreate.execute();
      return true;
    } catch (Exception e) {
      LOG.debug(e.getMessage());
      return false;
    }
  }

  public static int fileEntity(String category, JsonNode entity, String datatype) throws SQLException {
    PreparedStatement upsert = getUpsert(category, datatype);
    upsert.setObject(1, entity.get("iri").asText(), Types.OTHER);
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
