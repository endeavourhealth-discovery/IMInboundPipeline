package org.endeavourhealth.im_inbound_pipeline.controller;

import org.endeavourhealth.im_inbound_pipeline.service.QueueSender;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
public class EventController {

//  private static final String DIRECTORY_URL = from config
//  private static final String ORGANISATION = from config

  private final QueueSender queueSender;

  public EventController(QueueSender queueSender) {
    this.queueSender = queueSender;
  }

  @PostMapping(value = "/uploadFile")
  public String submit(@RequestParam("file") MultipartFile file) {
    // TODO: when message is received, poll to get the file from DIRECTORY_URL - temporarily accept a file
    System.out.println(file.getName());
    System.out.println(file.getContentType());
    System.out.println(file.getSize());

//      TODO: validate

    try {
      InputStream inputStream = new BufferedInputStream(file.getInputStream());

      try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
        String line;
        String[] headers = null;
        while ((line = br.readLine()) != null) {
          String[] values = line.split(",");
          if (headers == null) {
            headers = values;
          } else {
            JSONObject jsonObject = new JSONObject();
            for (int i = 0; i < headers.length; i++) {
              jsonObject.put(headers[i], values[i]);
            }
            System.out.println(line + " -> " + jsonObject.toString());
//            fileOrg in config - ORGANISATION
            String fileOrg = "emis";
            queueSender.sendMessage(line, fileOrg + "." + file.getSize());
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      return "fileUploadView";
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
