package org.endeavourhealth.pipeline.inbound.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "discovery")
public class DBEntry {
  @Id
  private Integer id;
  private String organisation;
  private String data;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
