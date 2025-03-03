package org.endeavourhealth.pipeline.inbound.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessOrderFileItem {

  private String namePattern;
  private Category category;

  public ProcessOrderFileItem() {
  }
}



