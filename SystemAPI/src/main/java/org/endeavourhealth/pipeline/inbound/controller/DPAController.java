package org.endeavourhealth.pipeline.inbound.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.endeavourhealth.pipeline.inbound.service.DPAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestMapping("api/dpa")
@Tag(name="DPA Controller")
@RequestScope
public class DPAController {
  private static final Logger LOG = LoggerFactory.getLogger(DPAController.class);

  @GetMapping(value = "public/hasActiveDPA")
  public boolean submit(@RequestParam String odsCode) {
    LOG.debug("hasActiveDPA [{}]", odsCode);
    return new DPAService().hasActiveDPA(odsCode);
  }

}
