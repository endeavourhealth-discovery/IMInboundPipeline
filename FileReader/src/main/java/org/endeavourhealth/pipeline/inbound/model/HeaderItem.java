package org.endeavourhealth.pipeline.inbound.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class HeaderItem {
  private String version;
  private List<String> headers;

  public HeaderItem() {
  }
}
