package org.endeavourhealth.pipeline.inbound.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.FileValidationConfigItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public class FileValidator {

  @Value("classpath:data/fileValidationConfig.json")
  private Resource validationConfig;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public boolean validateFile(String fileName) throws IOException {
    List<FileValidationConfigItem> validationConfigPOJO = getValidationConfig();
    return validationConfigPOJO.stream().anyMatch(file -> file.getFileName().equals(fileName));
  }

  private List<FileValidationConfigItem> getValidationConfig() throws IOException {
    try {
      return objectMapper.readValue(
        validationConfig.getInputStream(),
        new TypeReference<List<FileValidationConfigItem>>() {
        }
      );
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }
}
