package org.endeavourhealth.pipeline.inbound.repository;

import org.endeavourhealth.pipeline.inbound.model.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstanceRepository extends JpaRepository<Instance, Integer> {
}
