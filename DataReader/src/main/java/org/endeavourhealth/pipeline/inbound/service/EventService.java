package org.endeavourhealth.pipeline.inbound.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.endeavourhealth.pipeline.inbound.model.Event;
import org.endeavourhealth.pipeline.inbound.repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
  private final EventRepository eventRepository;

  public Event create(Event event) throws Exception {
    return eventRepository.save(event);
  }

  public void delete(Integer id) throws Exception {
    eventRepository.deleteById(id);
  }
}
