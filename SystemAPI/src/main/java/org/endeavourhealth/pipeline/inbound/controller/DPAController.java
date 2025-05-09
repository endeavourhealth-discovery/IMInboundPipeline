package org.endeavourhealth.pipeline.inbound.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.endeavourhealth.pipeline.inbound.service.DPAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestMapping("api/dpa")
@Tag(name="DPA Controller")
@RequestScope
public class DPAController {
  private static final Logger LOG = LoggerFactory.getLogger(DPAController.class);

  @GetMapping(value = "public/hasActiveDPA")
  public boolean submit(@RequestParam(name = "organisationUuid") String organisationUuid) {
    LOG.debug("hasActiveDPA [{}]", organisationUuid);
    return new DPAService().hasActiveDPA(organisationUuid);
  }

}
