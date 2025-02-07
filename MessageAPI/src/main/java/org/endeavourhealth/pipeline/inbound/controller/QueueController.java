package org.endeavourhealth.pipeline.inbound.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

@RestController
@RequestMapping("api/queue")
@Tag(name="Queue Controller")
@RequestScope
public class QueueController {
  private static final Logger LOG = LoggerFactory.getLogger(QueueController.class);

  @GetMapping(value = "/status")
  public String getStatus(@RequestParam(name = "id") String id) {
    LOG.debug("getStatus");
    //TODO get item status logic
    return "OK";
  }

  @GetMapping(value = "/pause")
  public void pause(@RequestParam(name = "id") String id) {
    LOG.debug("pause");
    //TODO pause item in queue logic
  }

  @DeleteMapping(value = "/delete")
  public void delete(@RequestParam(name = "id") String id) {
    LOG.debug("delete");
    //TODO delete item in queue logic
  }
}
