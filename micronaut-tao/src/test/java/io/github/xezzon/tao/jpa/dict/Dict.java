package io.github.xezzon.tao.jpa.dict;

import io.github.xezzon.tao.jpa.BaseEntity;
import io.github.xezzon.tao.jpa.LogicDeleteEntity;
import io.micronaut.data.annotation.MappedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author xezzon
 */
@Getter
@Setter
@ToString
@Entity
@MappedEntity
@Table
public class Dict extends BaseEntity<String> implements LogicDeleteEntity {

  @Serial
  private static final long serialVersionUID = 5254551771226841499L;

  @Id
  @Column
  private String id;
  @Column
  private String name;
  private Instant deleteTime;
}
