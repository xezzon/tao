package io.github.xezzon.tao.linked;

import java.util.Iterator;
import java.util.Optional;

/**
 * @author xezzon
 */
public interface LinkedNode<T extends LinkedNode<T>> {

  Optional<T> prev();
  Optional<T> next();

  static <T> T from(Iterable<T> list) {
  }
}
