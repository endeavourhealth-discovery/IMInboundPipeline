package org.endeavourhealth.pipeline.inbound.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.endeavourhealth.pipeline.inbound.database.helpers.ConnectionManager;
import org.endeavourhealth.pipeline.inbound.model.DBEntry;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DBService {
  private final InboundDataRepository dbRepository;

  public List<DBEntry> getAllByOrganisation(String organisationId) throws Exception {
    String sql = """
      SELECT * FROM table WHERE organisation_id = quote_literal($1)
      """;
    List<String> bindings = new ArrayList<>();
    bindings.add(organisationId);
    return customSelectStatement(sql, bindings);
  }

  public DBEntry getById(Integer id) throws Exception {
    Optional<DBEntry> dbEntry = dbRepository.findById(id);
    return dbEntry.orElse(null);
  }

  public DBEntry create(DBEntry dbEntry) throws Exception {
    return dbRepository.save(dbEntry);
  }

  public DBEntry update(DBEntry dbEntry) throws Exception {
    Optional<DBEntry> existingDbEntry = dbRepository.findById(dbEntry.getId());
    existingDbEntry.ifPresent(entry -> dbEntry.setCreatedAt(entry.getCreatedAt()));
    dbEntry.setUpdatedAt(LocalDateTime.now());
    return dbRepository.save(dbEntry);
  }

  public void delete(Integer id) throws Exception {
    dbRepository.deleteById(id);
  }

  private List<DBEntry> customSelectStatement(String sql, List<String> bindings) throws Exception {
    try (PreparedStatement statement = ConnectionManager.getConnection().prepareStatement(sql)) {
      for (int i = 0; i < bindings.size(); i++) {
        statement.setString(i + 1, bindings.get(i));
      }
      try (ResultSet rs = statement.executeQuery(sql)) {
        List<DBEntry> dbEntries = new ArrayList<>();
        while (rs.next()) {
          DBEntry dbEntry = new DBEntry();
          dbEntry.setId(rs.getInt("id"));
          dbEntry.setCreatedAt(LocalDateTime.parse(rs.getString("created_at")));
          dbEntry.setUpdatedAt(LocalDateTime.parse(rs.getString("updated_at")));
          dbEntry.setOrganisation(rs.getString("organisation"));
          dbEntry.setData(rs.getString("data"));
          dbEntries.add(dbEntry);
        }
        return dbEntries;
      }
    }
  }
}
