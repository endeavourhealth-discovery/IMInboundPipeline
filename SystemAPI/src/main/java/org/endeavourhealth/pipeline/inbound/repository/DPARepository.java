package org.endeavourhealth.pipeline.inbound.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DPARepository {
  private final Connection conn;

  public DPARepository() {
    try {
      conn = ConnectionManager.getConnection("dsm");
    } catch (SQLException e) {
      throw new RuntimeException("Unable to connect to DSM database", e);
    }
  }

  public boolean hasActiveDPA(String organisationUuid) throws SQLException {
    try (PreparedStatement sql = conn.prepareStatement("""
            select dpa.uuid 
            from data_processing_agreement dpa 
              inner join master_mapping mm on mm.parent_uuid = dpa.uuid and mm.parent_map_type_id = ? 
              inner join organisation o on o.uuid = mm.child_uuid
                  where o.uuid = ? 
              and mm.child_map_type_id = ?
              and (dpa.start_date is null or (dpa.start_date is not null and dpa.start_date <= current_date))
              and (dpa.end_date is null or dpa.end_date >= current_date) 
              and dpa.dsa_status_id = 0 
""")) {
      sql.setShort(1, (short) 5);
      sql.setString(2, organisationUuid);
      sql.setShort(3, (short) 8);
      sql.executeQuery();
      return sql.getResultSet().next();
    }
  }
}
