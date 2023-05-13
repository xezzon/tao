package io.github.xezzon.tao.desensitize;

/**
 * 脱敏
 */
@FunctionalInterface
public interface Desensitizer {

  String desensitize(String origin);
}
