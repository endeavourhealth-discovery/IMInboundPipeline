package org.endeavourhealth.pipeline.inbound.model;

import java.util.List;

public class FileValidationHeaderItem {
  private String version;
  private List<String> headers;

  public String getVersion() {
    return version;
  }

  public FileValidationHeaderItem setVersion(String version) {
    this.version = version;
    return this;
  }

  public List<String> getHeaders() {
    return headers;
  }

  public FileValidationHeaderItem setHeaders(List<String> headers) {
    this.headers = headers;
    return this;
  }
}
