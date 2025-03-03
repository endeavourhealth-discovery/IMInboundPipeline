package org.endeavourhealth.pipeline.inbound.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table (name = "instance")
public class Instance {
  @Id
  private UUID id;

  @Column(columnDefinition = "json")
  @ColumnTransformer(write = "?::json")
  private String json;

}
