package org.endeavourhealth.pipeline.inbound.service;

import org.endeavourhealth.pipeline.inbound.repository.DPARepository;

import java.sql.SQLException;

public class DPAService {
  private final DPARepository dpaRepository = new DPARepository();

  public boolean hasActiveDPA(String odsCode) {
    try {
      return dpaRepository.hasActiveDPA(odsCode);
    } catch (SQLException e) {
      throw new RuntimeException("Unable to check for DPA for ODS code [" + odsCode + "]", e);
    }
  }
}
