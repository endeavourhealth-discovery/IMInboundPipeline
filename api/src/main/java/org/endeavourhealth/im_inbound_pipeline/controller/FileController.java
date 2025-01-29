package org.endeavourhealth.im_inbound_pipeline.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

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
