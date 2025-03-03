package org.endeavourhealth.pipeline.inbound.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FileValidationHeaderItem {
  private String version;
  private List<String> headers;

  public FileValidationHeaderItem() {
  }
}
