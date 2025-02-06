package org.endeavourhealth.im_inbound_pipeline.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FileValidation {
  private String fileName;
  private List<String> headers;

  public FileValidation() {
  }
}
