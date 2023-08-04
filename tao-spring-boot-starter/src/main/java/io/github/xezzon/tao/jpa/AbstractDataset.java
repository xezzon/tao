package io.github.xezzon.tao.jpa;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author xezzon
 */
public abstract class AbstractDataset<T> implements CommandLineRunner {

  private int executeTimes = 1;
  protected final transient Collection<T> dataset;
  private final transient JpaRepository<T, ?> repository;

  protected AbstractDataset(
      @NotNull Collection<T> dataset,
      @NotNull JpaRepository<T, ?> repository
  ) {
    this.dataset = dataset;
    this.repository = repository;
  }

  @Override
  public void run(String... args) {
    if (executeTimes-- <= 0) {
      return;
    }
    repository.saveAll(dataset);
  }
}
