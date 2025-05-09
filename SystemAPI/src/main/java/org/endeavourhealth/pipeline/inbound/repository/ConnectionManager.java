package org.endeavourhealth.pipeline.inbound.repository;

import org.endeavourhealth.pipeline.inbound.service.SystemService;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class ConnectionManager {
  private static final Map<String, Connection> connCache = new HashMap<>();

  public static Connection getConnection(String database) throws SQLException {
    synchronized (connCache) {
      Connection conn = connCache.get(database);
      if (conn == null) {
        Properties props = new Properties();
        props.setProperty("user", SystemService.getProperty("database." + database + ".username"));
        props.setProperty("password", SystemService.getProperty("database." + database + ".password"));
        conn = DriverManager.getConnection(SystemService.getProperty("database." + database + ".url"), props);
        connCache.put(database, conn);
      }

      return conn;
    }
  }
}
