package org.endeavourhealth.pipeline.inbound.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnTransformer;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity(name = "Instance")
@Table(name = "instance")
public class Instance {
  @Id
  private UUID id;

  @Column(columnDefinition = "json")
  @ColumnTransformer(write = "?::json")
  private String json;

}
