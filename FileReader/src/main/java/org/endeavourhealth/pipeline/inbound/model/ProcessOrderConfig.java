package org.endeavourhealth.pipeline.inbound.model;

import java.util.List;

public class ProcessOrderConfig {
  private String org;
  private List<ProcessOrderFileItem> orderedList;

  public String getOrg() {
    return org;
  }

  public ProcessOrderConfig setOrg(String org) {
    this.org = org;
    return this;
  }

  public List<ProcessOrderFileItem> getOrderedList() {
    return orderedList;
  }

  public ProcessOrderConfig setOrderedList(List<ProcessOrderFileItem> orderedList) {
    this.orderedList = orderedList;
    return this;
  }
}
