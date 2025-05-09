package org.endeavourhealth.pipeline.inbound.model;

import java.util.List;

public class FileValidationConfig {
  private String fileName;
  private List<FileValidationHeaderItem> headers;

  public String getFileName() {
    return fileName;
  }

  public FileValidationConfig setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public List<FileValidationHeaderItem> getHeaders() {
    return headers;
  }

  public FileValidationConfig setHeaders(List<FileValidationHeaderItem> headers) {
    this.headers = headers;
    return this;
  }
}