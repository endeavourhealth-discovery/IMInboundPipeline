package org.endeavourhealth.im_inbound_pipeline.controller;

import org.endeavourhealth.im_inbound_pipeline.converter.CsvToJsonConverter;
import org.endeavourhealth.im_inbound_pipeline.service.QueueSender;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;

@RestController
public class EventController {

  private final QueueSender queueSender;
  private final CsvToJsonConverter csvToJsonConverter = new CsvToJsonConverter();

  public EventController(QueueSender queueSender) {
    this.queueSender = queueSender;
  }

  @PostMapping(value = "/uploadFile")
  public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap) {
    // TODO: when message is received, poll to get the file
    modelMap.addAttribute("file", file);
    System.out.println(file.getName());
    System.out.println(file.getContentType());
    System.out.println(file.getSize());

    try {
      InputStream inputStream = new BufferedInputStream(file.getInputStream());
      List<String> lines = csvToJsonConverter.convertCsvToJsonLines(inputStream);
      for (String line: lines) {
        queueSender.sendMessage(line, "emis." + file.getSize());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }


    return "fileUploadView";

  }

}
