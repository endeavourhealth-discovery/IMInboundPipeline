package org.endeavourhealth.pipeline.inbound.database;

import org.endeavourhealth.pipeline.inbound.model.DBEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DBRepository extends JpaRepository<DBEntry, Integer> {
}
