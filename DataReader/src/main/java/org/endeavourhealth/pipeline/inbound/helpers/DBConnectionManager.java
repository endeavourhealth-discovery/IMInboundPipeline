package org.endeavourhealth.pipeline.inbound.helpers;

import org.endeavourhealth.pipeline.inbound.config.DBConfig;
import org.endeavourhealth.pipeline.inbound.model.Event;
import org.endeavourhealth.pipeline.inbound.model.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

public class DBConnectionManager {

  //private static final String POSTGRES_URL = Optional.ofNullable(System.getenv("SPRING_DATASOURCE_URL")).orElseThrow(() -> new IllegalArgumentException("Env var 'POSTGRES_URL' is not defined"));
  //private static final String POSTGRES_USER = Optional.ofNullable(System.getenv("SPRING_DATASOURCE_USER")).orElseThrow(() -> new IllegalArgumentException("Env var 'POSTGRES_USER' is not defined"));
  //private static final String POSTGRES_PASSWORD = Optional.ofNullable(System.getenv("SPRING_DATASOURCE_PASSWORD")).orElseThrow(() -> new IllegalArgumentException("Env var 'POSTGRES_PASSWORD' is not defined"));


  private static final Logger LOG = LoggerFactory.getLogger(DBConnectionManager.class);
  private static PreparedStatement upsertEvent = null;
  private static PreparedStatement upsertInstance = null;

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

  public static Connection getConnection() throws SQLException {
    Properties props = new Properties();
    props.setProperty("user", DBConfig.getSpringDatasourceUsername());
    props.setProperty("password", DBConfig.getSpringDatasourcePassword());
    return DriverManager.getConnection(DBConfig.getSpringDatasourceUrl(), props);
  }

  private static PreparedStatement prepareUpsertEvent() throws SQLException {
    return getConnection().prepareStatement("INSERT INTO healthdb.event (id, json) VALUES (?,(?::json)) ON CONFLICT (id) DO UPDATE SET json=?::json");
  }

  private static PreparedStatement prepareUpsertInstance() throws SQLException {
    return getConnection().prepareStatement("INSERT INTO healthdb.instance (id, json) VALUES (?,(?::json)) ON CONFLICT (id) DO UPDATE SET json=?::json");
  }

  public static void fileEvent(Event event) throws SQLException {
    upsertEvent.setObject(1, event.getId());
    upsertEvent.setString(2, event.getJson());
    upsertEvent.setString(3, event.getJson());
    upsertEvent.executeUpdate();
    LOG.debug("Event filed to database");
  }

  public static void fileInstance(Instance instance) throws SQLException {
    upsertInstance.setObject(1, instance.getId());
    upsertInstance.setString(2, instance.getJson());
    upsertInstance.setString(3, instance.getJson());
    upsertInstance.executeUpdate();
    LOG.debug("Instance filed database");
  }
}
