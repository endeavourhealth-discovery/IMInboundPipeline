package org.endeavourhealth.pipeline.inbound.database.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Properties;

public class ConnectionManager {

  private static final String POSTGRES_URL = Optional.ofNullable(System.getenv("POSTGRES_URL")).orElse("localhost");
  private static final String POSTGRES_USER = Optional.ofNullable(System.getenv("POSTGRES_USER")).orElse("postgres");
  private static final String POSTGRES_PASSWORD = Optional.ofNullable(System.getenv("POSTGRES_PASSWORD")).orElse("123456");
  private static Connection connection = null;

  private ConnectionManager() {
    throw new IllegalStateException("Utility class");
  }

  public static Connection getConnection() throws SQLException {
    if (connection != null) {
      return connection;
    }
    final String url = "jdbc:postgresql://%s:5432/discovery"
      .formatted(
        POSTGRES_URL
      );
    Properties props = new Properties();
    props.setProperty("user", POSTGRES_USER);
    props.setProperty("password", POSTGRES_PASSWORD);
    try (Connection conn = DriverManager.getConnection(url,props)) {
      connection = conn;
      return connection;
    }
  }
}
