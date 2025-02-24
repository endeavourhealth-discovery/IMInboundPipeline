package org.endeavourhealth.pipeline.inbound.repository;

import org.endeavourhealth.pipeline.inbound.model.DBEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InboundDataRepository extends JpaRepository<DBEntry, Integer> {
}
