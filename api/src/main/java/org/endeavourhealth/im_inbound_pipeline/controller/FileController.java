package org.endeavourhealth.im_inbound_pipeline.controller;

import org.endeavourhealth.im_inbound_pipeline.converter.CsvToJsonConverter;
import org.endeavourhealth.im_inbound_pipeline.service.QueueSender;
import org.json.JSONArray;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

@RestController
public class FileController {

  private final QueueSender queueSender;
  private final CsvToJsonConverter csvToJsonConverter = new CsvToJsonConverter();

  public FileController(QueueSender queueSender) {
    this.queueSender = queueSender;
  }

  @GetMapping("/upload")
  private String upload(@RequestParam String fileName, @RequestParam String fileOrg, @RequestParam String fileSize) throws Exception {
    queueSender.sendMessage(fileName, fileOrg, fileSize);
    return "Message sent: " + fileName;
  }

  @PostMapping(value = "/uploadFile")
  public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) {
    modelMap.addAttribute("file", file);
    System.out.println(file.getName());
    System.out.println(file.getContentType());
    System.out.println(file.getSize());

    try {
      InputStream inputStream = new BufferedInputStream(file.getInputStream());
      csvToJsonConverter.convertCsvToJsonLines(inputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }


    return "fileUploadView";

  }

}
