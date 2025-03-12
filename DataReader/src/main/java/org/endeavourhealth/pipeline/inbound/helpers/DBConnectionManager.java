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

  private DBConnectionManager() {
    throw new IllegalStateException("Utility class");
  }

  public static Connection getConnection() throws SQLException {
    Properties props = new Properties();
    props.setProperty("user", DBConfig.getSpringDatasourceUsername());
    props.setProperty("password", DBConfig.getSpringDatasourcePassword());
    return DriverManager.getConnection(DBConfig.getSpringDatasourceUrl(), props);
  }

  public static void fileEvent(Connection connection, Event event) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO healthdb.event (id, json) VALUES (?,(?::json)) ON CONFLICT (id) DO UPDATE SET json=?::json")) {
      statement.setObject(1, event.getId());
      statement.setString(2, event.getJson());
      statement.setString(3, event.getJson());
      statement.executeUpdate();
      LOG.debug("Event filed to database");
    }
  }

  public static void fileInstance(Connection connection, Instance instance) throws SQLException {
    try (PreparedStatement statement = connection.prepareStatement("INSERT INTO healthdb.instance (id, json) VALUES (?,(?::json)) ON CONFLICT (id) DO UPDATE SET json=?::json")) {
      statement.setObject(1, instance.getId());
      statement.setString(2, instance.getJson());
      statement.setString(3, instance.getJson());
      statement.executeUpdate();
      LOG.debug("Instance filed database");
    }
  }
}
