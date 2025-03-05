package org.endeavourhealth.pipeline.inbound.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "Event")
@Table(name = "event")
public class Event {
  @Id
  private UUID id;

  @Column(columnDefinition = "json")
  @ColumnTransformer(write = "?::json")
  private String json;
}
