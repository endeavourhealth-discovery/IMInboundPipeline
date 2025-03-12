package org.endeavourhealth.pipeline.inbound.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DBConfig {

  @Value("${spring.dataSource.username}")
  private String username;
  @Value("${spring.dataSource.password}")
  private String password;
  @Value("${spring.dataSource.url}")
  private String url;

  private static String SPRING_DATASOURCE_USERNAME;
  private static String SPRING_DATASOURCE_PASSWORD;
  private static String SPRING_DATASOURCE_URL;

  @PostConstruct
  public void init() {
    SPRING_DATASOURCE_USERNAME = username;
    SPRING_DATASOURCE_PASSWORD = password;
    SPRING_DATASOURCE_URL = url;
  }

  public static String getSpringDatasourceUrl() {
    return SPRING_DATASOURCE_URL;
  }

  public static String getSpringDatasourcePassword() {
    return SPRING_DATASOURCE_PASSWORD;
  }

  public static String getSpringDatasourceUsername() {
    return SPRING_DATASOURCE_USERNAME;
  }

}
