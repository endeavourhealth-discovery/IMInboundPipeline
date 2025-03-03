package org.endeavourhealth.pipeline.inbound.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProcessOrderConfig {
  private String org;
  private List<ProcessOrderFileItem> orderedList;

  public ProcessOrderConfig() {
  }
}
