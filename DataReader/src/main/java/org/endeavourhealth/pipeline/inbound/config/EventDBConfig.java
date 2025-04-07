package org.endeavourhealth.pipeline.inbound.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventDBConfig {

  @Value("${spring.event.dataSource.username}")
  private String username;
  @Value("${spring.event.dataSource.password}")
  private String password;
  @Value("${spring.event.dataSource.url}")
  private String url;

  private static String SPRING_EVENT_DATASOURCE_USERNAME;
  private static String SPRING_EVENT_DATASOURCE_PASSWORD;
  private static String SPRING_EVENT_DATASOURCE_URL;

  @PostConstruct
  public void init() {
    SPRING_EVENT_DATASOURCE_USERNAME = username;
    SPRING_EVENT_DATASOURCE_PASSWORD = password;
    SPRING_EVENT_DATASOURCE_URL = url;
  }

  public static String getSpringEventDatasourceUrl() {
    return SPRING_EVENT_DATASOURCE_URL;
  }

  public static String getSpringEventDatasourcePassword() {
    return SPRING_EVENT_DATASOURCE_PASSWORD;
  }

  public static String getSpringEventDatasourceUsername() {
    return SPRING_EVENT_DATASOURCE_USERNAME;
  }

}
