package org.endeavourhealth.pipeline.inbound.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/file")
@Tag(name="File Controller")
@RequestScope
public class FileController {
  private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

  @PostMapping(value = "/uploadFile")
  public String submit(@RequestParam("file") MultipartFile file, @RequestParam String fileOrg) {
//    Protected API - certificate? IP whitelist?
//    fileOrg - param? dynamic identification?
    System.out.println(file.getName());
    System.out.println(file.getContentType());
    System.out.println(file.getSize());
//  Config about organisation and directory destination - map of filename to directory destination (where does config live?)
//    TODO: Upload file to directory
    return "file uploaded to Directory";
  }

}
