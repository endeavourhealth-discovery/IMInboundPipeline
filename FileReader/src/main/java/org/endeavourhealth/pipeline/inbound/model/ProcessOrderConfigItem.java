package org.endeavourhealth.pipeline.inbound.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProcessOrderConfigItem {
  private String org;
  private List<String> orderedList;

  public ProcessOrderConfigItem() {
  }
}
