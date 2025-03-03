package org.endeavourhealth.pipeline.inbound.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FileValidationConfig {
  private String fileName;
  private List<FileValidationHeaderItem> headers;

  public FileValidationConfig() {
  }
}