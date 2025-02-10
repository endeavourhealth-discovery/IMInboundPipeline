package org.endeavourhealth.pipeline.inbound.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ValidationConfigModel {
  private String fileName;
  private List<String> headers;

  public ValidationConfigModel() {
  }
}