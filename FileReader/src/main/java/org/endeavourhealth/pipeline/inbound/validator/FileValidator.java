package org.endeavourhealth.pipeline.inbound.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.ValidationConfigModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileValidator {

  @Value("classpath:data/validationConfig.json")
  private Resource validationConfig;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public boolean validateFile(String fileName) throws IOException {
    List<ValidationConfigModel> validationConfigPOJO = getValidationConfig();
    return validationConfigPOJO.stream().anyMatch(file -> file.getFileName().equals(fileName));
  }

  private List<ValidationConfigModel> getValidationConfig() throws IOException {
    try {
      return objectMapper.readValue(
        validationConfig.getInputStream(),
        new TypeReference<List<ValidationConfigModel>>() {
        }
      );
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }
}
