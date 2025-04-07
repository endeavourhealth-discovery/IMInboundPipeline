package org.endeavourhealth.pipeline.inbound.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.pipeline.inbound.config.EventDBConfig;
import org.endeavourhealth.pipeline.inbound.config.InstanceDBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public class DBConnectionManager {

  private static final Logger LOG = LoggerFactory.getLogger(DBConnectionManager.class);
  private static PreparedStatement upsertEvent = null;
  private static PreparedStatement upsertInstance = null;
  private static Connection eventConnection = null;
  private static Connection instanceConnection = null;

  static {
    try {
      upsertEvent = prepareEventUpsert();
      upsertInstance = prepareInstanceUpsert();
    } catch (SQLException e) {
      LOG.error("{}", e.toString());
      System.exit(1);
    }
  }

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

  private static PreparedStatement prepareInstanceUpsert() throws SQLException {
    return getInstanceConnection().prepareStatement("INSERT INTO instance (id, json) VALUES (?,?) ON CONFLICT (id) DO UPDATE SET json=?::json");
  }

  private static PreparedStatement getUpsert(String category) {
    if ("EVENT".equals(category)) {
      return upsertEvent;
    } else if ("INSTANCE".equals(category)) {
      return upsertInstance;
    } else {
      throw new IllegalArgumentException("Provided category header '" + category + "' is invalid");
    }
  }

  public static int fileEntity(String category, JsonNode entity) throws SQLException {
    PreparedStatement upsert = getUpsert(category);
    upsert.setObject(1, entity.get("@id").asText(), Types.OTHER);
    upsert.setObject(2, entity, Types.OTHER);
    upsert.setObject(3, entity, Types.OTHER);
    int rows = upsert.executeUpdate();
    LOG.debug("{} filed to database", category);
    return rows;
  }
}
