package org.endeavourhealth.im_inbound_pipeline.controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

  public FileController() {
  }

  @PostMapping(value = "/uploadFile")
  public String submit(@RequestParam("file") MultipartFile file, ModelMap modelMap, @RequestParam String fileOrg) {
    modelMap.addAttribute("file", file);
    System.out.println(file.getName());
    System.out.println(file.getContentType());
    System.out.println(file.getSize());
//  Config about organisation and directory destination
//    TODO: Upload file to directory
    return "file uploaded to Directory";
  }

}
