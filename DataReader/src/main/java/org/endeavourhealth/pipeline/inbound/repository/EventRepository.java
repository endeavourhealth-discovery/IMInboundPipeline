package org.endeavourhealth.pipeline.inbound.repository;

import org.endeavourhealth.pipeline.inbound.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
}
