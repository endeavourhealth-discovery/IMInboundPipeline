package org.endeavourhealth.pipeline.inbound.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InstanceDBConfig {

  @Value("${spring.instance.dataSource.username}")
  private String username;
  @Value("${spring.instance.dataSource.password}")
  private String password;
  @Value("${spring.instance.dataSource.url}")
  private String url;

  private static String SPRING_INSTANCE_DATASOURCE_USERNAME;
  private static String SPRING_INSTANCE_DATASOURCE_PASSWORD;
  private static String SPRING_INSTANCE_DATASOURCE_URL;

  @PostConstruct
  public void init() {
    SPRING_INSTANCE_DATASOURCE_USERNAME = username;
    SPRING_INSTANCE_DATASOURCE_PASSWORD = password;
    SPRING_INSTANCE_DATASOURCE_URL = url;
  }

  public static String getSpringInstanceDatasourceUrl() {
    return SPRING_INSTANCE_DATASOURCE_URL;
  }

  public static String getSpringInstanceDatasourcePassword() {
    return SPRING_INSTANCE_DATASOURCE_PASSWORD;
  }

  public static String getSpringInstanceDatasourceUsername() {
    return SPRING_INSTANCE_DATASOURCE_USERNAME;
  }

}
