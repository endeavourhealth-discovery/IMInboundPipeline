package org.endeavourhealth.pipeline.inbound.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class SystemService implements InitializingBean {

  @Autowired
  private Environment env;

  private static SystemService instance;

  protected String getInstanceProperty(String propertyName) {
    return env.getProperty(propertyName);
  }

  @Override
  public void afterPropertiesSet() {
    instance = this;
  }

  public static String getProperty(String propertyName) {
    return instance.getInstanceProperty(propertyName);
  }
}