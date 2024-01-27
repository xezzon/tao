package io.github.xezzon.tao.jpa.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "t_user")
public class User {

  @Id
  @Column
  private String id;
  @Column(nullable = false)
  private String name;
  @Column
  private Integer age;
  @Column(precision = 13, scale = 9)
  private BigDecimal credit;
  @Column(updatable = false)
  private GenderEnum gender;
  @Column(updatable = false)
  private Instant createTime;
  @Column
  private LocalDateTime deleteDateTime;
  @Column
  private Boolean deleted;
  @Column
  private LocalDate deleteDate;
  @Column
  private LocalTime deleteTime;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
