package org.endeavourhealth.im_inbound_pipeline.controller;

import org.endeavourhealth.im_inbound_pipeline.service.QueueSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController {

  private final QueueSender queueSender;

  public FileController(QueueSender queueSender) {
    this.queueSender = queueSender;
  }

  @GetMapping("/upload")
  private String  upload(@RequestParam String fileName, @RequestParam String fileOrg) throws Exception {
    queueSender.sendMessage(fileName);
    return "Message sent: " + fileName;
  }

}
