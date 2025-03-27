package org.endeavourhealth.pipeline.inbound.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import org.endeavourhealth.pipeline.inbound.config.DBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class DBConnectionManager {

  private static final Logger LOG = LoggerFactory.getLogger(DBConnectionManager.class);
  private static PreparedStatement upsertEvent = null;
  private static PreparedStatement upsertInstance = null;
  private static Connection connection = null;

  static {
    try {
      upsertEvent = prepareUpsertEvent();
      upsertInstance = prepareUpsertInstance();
    } catch (SQLException e) {
      LOG.error("{}", e.toString());
      System.exit(1);
    }
  }

  private DBConnectionManager() {
    throw new IllegalStateException("Utility class");
  }

  private static Connection getConnection() throws SQLException {
    if (connection == null) {
      Properties props = new Properties();
      props.setProperty("user", DBConfig.getSpringDatasourceUsername());
      props.setProperty("password", DBConfig.getSpringDatasourcePassword());
      return DriverManager.getConnection(DBConfig.getSpringDatasourceUrl(), props);
    }
    return connection;
  }

  private static PreparedStatement prepareUpsertEvent() throws SQLException {
    return getConnection().prepareStatement("INSERT INTO healthdb.event (id, json) VALUES (?,(?::json)) ON CONFLICT (id) DO UPDATE SET json=?::json");
  }

  private static PreparedStatement prepareUpsertInstance() throws SQLException {
    return getConnection().prepareStatement("INSERT INTO healthdb.instance (id, json) VALUES (?,(?::json)) ON CONFLICT (id) DO UPDATE SET json=?::json");
  }

  private static PreparedStatement getUpsert(String category) throws SQLException {
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
