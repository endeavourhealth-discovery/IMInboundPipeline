package org.endeavourhealth.pipeline.inbound.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.model.FileValidationConfigItem;
import org.endeavourhealth.pipeline.inbound.model.HeaderItem;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderConfigItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FileValidator {

  @Value("classpath:fileValidationConfig.json")
  private Resource fileValidationConfig;

  @Value("classpath:processOrderConfig.json")
  private Resource processOrderConfig;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public boolean isValidFile(String fileName, List<String> fileHeaders) throws IOException {
    List<FileValidationConfigItem> validationConfigPOJO = getFileValidationConfig();
    Optional<FileValidationConfigItem> found = validationConfigPOJO.stream().filter(file -> fileName.matches(file.getFileName())).findFirst();
    if (found.isPresent()) {
      FileValidationConfigItem fileValidationConfigItem = found.get();
      List<HeaderItem> headers = fileValidationConfigItem.getHeaders();
      Optional<HeaderItem> result = headers.stream().filter(headerList -> areListsEqual(fileHeaders, headerList.getHeaders().stream()
        .map(header -> "\"" + header + "\"")
        .collect(Collectors.toList()))).findFirst();
      return result.isPresent();
    }
    return false;
  }

  public boolean areListsEqual(List<String> list1, List<String> list2) {
    if (list1.size() != list2.size()) return false;

    List<String> sortedList1 = new ArrayList<>(list1);
    List<String> sortedList2 = new ArrayList<>(list2);

    Collections.sort(sortedList1);
    Collections.sort(sortedList2);

    return sortedList1.equals(sortedList2);
  }

  private List<FileValidationConfigItem> getFileValidationConfig() throws IOException {
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

  private List<ProcessOrderConfigItem> getProcessOrderConfig() throws IOException {
    try {
      return objectMapper.readValue(
        processOrderConfig.getInputStream(),
        new TypeReference<List<ProcessOrderConfigItem>>() {
        }
      );
    } catch (IOException e) {
      e.printStackTrace();
      return List.of();
    }
  }

  public boolean areAllFilesInBucket(String targetBaseRoutingKey, List<String> filesInBucket) throws IOException {
    List<ProcessOrderConfigItem> processOrderConfigPOJO = getProcessOrderConfig();
    Optional<ProcessOrderConfigItem> found = processOrderConfigPOJO.stream().filter(configItem -> configItem.getOrg().equalsIgnoreCase(targetBaseRoutingKey)).findFirst();
    if (found.isEmpty()) return false;

    boolean allFilesInBucket = true;
    for (String pattern : found.get().getOrderedList()) {
      Optional<String> result = filesInBucket.stream().filter(file -> file.matches(pattern)).findFirst();
      if (result.isEmpty()) {
        System.out.println(pattern + ": match NOT found");
        allFilesInBucket = false;
      } else System.out.println(pattern + ": match found");
    }
    return allFilesInBucket;
  }
}
