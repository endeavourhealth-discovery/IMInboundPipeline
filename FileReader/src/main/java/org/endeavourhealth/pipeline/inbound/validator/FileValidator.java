package org.endeavourhealth.pipeline.inbound.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.FileValidationConfigItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class FileValidator {

  @Value("classpath:fileValidationConfig.json")
  private Resource fileValidationConfig;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public boolean isValidFile(String fileName, List<String> fileHeaders) throws IOException {
    List<FileValidationConfigItem> validationConfigPOJO = getValidationConfig();
    Optional<FileValidationConfigItem> found = validationConfigPOJO.stream().filter(file -> file.getFileName().equals(fileName)).findFirst();
    if (found.isPresent()) {
      FileValidationConfigItem fileValidationConfigItem = found.get();
      List<String> validationHeaders = fileValidationConfigItem.getHeaders();
      return areListsEqual(fileHeaders, validationHeaders);
    }
    System.out.println("No validation config found for file: " + fileName);
    return false;
  }

  public boolean areListsEqual(List<String> list1, List<String> list2) {
    System.out.println("list1: " + list1);
    System.out.println("list2: " + list2);
    if (list1.size() != list2.size()) return false;

    List<String> sortedList1 = new ArrayList<>(list1);
    List<String> sortedList2 = new ArrayList<>(list2);

    Collections.sort(sortedList1);
    Collections.sort(sortedList2);

    return sortedList1.equals(sortedList2);
  }

  private List<FileValidationConfigItem> getValidationConfig() throws IOException {
    try {
      return objectMapper.readValue(
        fileValidationConfig.getInputStream(),
        new TypeReference<List<FileValidationConfigItem>>() {
        }
      );
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }
}
