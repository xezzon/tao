package io.github.xezzon.tao.jpa.dict;

import io.github.xezzon.tao.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@ToString
@Entity
@Table
public class Dict extends BaseEntity<String> {

  @Serial
  private static final long serialVersionUID = 5254551771226841499L;

  @Id
  @Column
  private String id;
  @Column
  private String name;
}
