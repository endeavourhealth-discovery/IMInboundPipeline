package org.endeavourhealth.pipeline.inbound.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.endeavourhealth.pipeline.inbound.model.Instance;
import org.endeavourhealth.pipeline.inbound.repository.InstanceRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstanceService {

  private final InstanceRepository instanceRepository;

  public Instance create(Instance instance) throws Exception {
    return instanceRepository.save(instance);
  }

  public void delete(Integer id) throws Exception {
    instanceRepository.deleteById(id);
  }
}
