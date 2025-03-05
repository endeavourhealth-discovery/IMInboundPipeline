package org.endeavourhealth.pipeline.inbound.validator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.endeavourhealth.pipeline.inbound.listener.FileEventListener;
import org.endeavourhealth.pipeline.inbound.model.FileValidationConfig;
import org.endeavourhealth.pipeline.inbound.model.FileValidationHeaderItem;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderConfig;
import org.endeavourhealth.pipeline.inbound.model.ProcessOrderFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOG = LoggerFactory.getLogger(FileValidator.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  public boolean isValidFile(String fileName, List<String> fileHeaders) {
    List<FileValidationConfig> validationConfigPOJO = getFileValidationConfig();
    Optional<FileValidationConfig> found = validationConfigPOJO.stream().filter(file -> fileName.matches(file.getFileName())).findFirst();
    if (found.isPresent()) {
      FileValidationConfig fileValidationConfigItem = found.get();
      List<FileValidationHeaderItem> headers = fileValidationConfigItem.getHeaders();
      Optional<FileValidationHeaderItem> result = headers.stream().filter(headerList -> areListsEqual(fileHeaders, headerList.getHeaders().stream()
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

  private List<FileValidationConfig> getFileValidationConfig() {
    try {
      return objectMapper.readValue(
        fileValidationConfig.getInputStream(),
        new TypeReference<>() {
        }
      );
    } catch (IOException e) {
      LOG.error(e.getMessage());
      return List.of();
    }
  }

  private List<ProcessOrderConfig> getProcessOrderConfig() {
    try {
      return objectMapper.readValue(
        processOrderConfig.getInputStream(),
        new TypeReference<>() {
        }
      );
    } catch (IOException e) {
      LOG.error(e.getMessage());
      return List.of();
    }
  }

  public boolean areAllFilesInBucket(String targetBaseRoutingKey, List<String> filesInBucket) throws IOException {
    List<ProcessOrderConfig> processOrderConfigPOJO = getProcessOrderConfig();
    Optional<ProcessOrderConfig> found = processOrderConfigPOJO.stream().filter(configItem -> configItem.getOrg().equalsIgnoreCase(targetBaseRoutingKey)).findFirst();
    if (found.isEmpty()) return false;

    boolean allFilesInBucket = true;
    for (ProcessOrderFileItem fileItem : found.get().getOrderedList()) {
      Optional<String> result = filesInBucket.stream().filter(file -> file.matches(fileItem.getNamePattern())).findFirst();
      if (result.isEmpty()) {
        LOG.warn("Match NOT found for: {}", fileItem.getNamePattern());
        allFilesInBucket = false;
      }
    }
    return allFilesInBucket;
  }
}
