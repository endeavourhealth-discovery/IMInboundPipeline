package org.endeavourhealth.pipeline.inbound.database;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.endeavourhealth.pipeline.inbound.model.DBEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DBService {
  private final DBRepository dbRepository;

  public List<DBEntry> getAllByOrganisation(String organisationId) throws Exception {
    return dbRepository.findAll();
  }

  public DBEntry getById(Integer id) throws Exception {
    Optional<DBEntry> dbEntry = dbRepository.findById(id);
    if (dbEntry.isPresent()) {
      return dbEntry.get();
    }
    return null;
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
}
