package org.endeavourhealth.pipeline.inbound.model;

public class ProcessOrderFileItem {

  private String namePattern;
  private Category category;

  public String getNamePattern() {
    return namePattern;
  }

  public ProcessOrderFileItem setNamePattern(String namePattern) {
    this.namePattern = namePattern;
    return this;
  }

  public Category getCategory() {
    return category;
  }

  public ProcessOrderFileItem setCategory(Category category) {
    this.category = category;
    return this;
  }
}



