package indi.xezzon.tao.observer;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * 观察者调度中心
 * @author xezzon
 */
public class ObserverContext {

  private static final Map<Class<? extends Observation>, Set<Consumer>> OBSERVER_MAP = new ConcurrentHashMap<>();

  /**
   * 将观察者注册到调度中心
   * @param clazz 消息类型
   * @param consumer 观察者
   * @param <T> 消息类型泛型
   */
  public static <T extends Observation> void register(@NotNull Class<T> clazz, @NotNull Consumer<T> consumer) {
    Set<Consumer> observers = OBSERVER_MAP.getOrDefault(clazz,
        new CopyOnWriteArraySet<>());
    observers.add(consumer);
    OBSERVER_MAP.put(clazz, observers);
  }

  /**
   * 消息推送给观察者
   * @param observation 消息
   * @param <T> 消息类型泛型
   */
  public static <T extends Observation> void post(@NotNull T observation) {
    Set<Consumer> observers = OBSERVER_MAP.getOrDefault(observation.getClass(),
        Collections.emptySet());
    observers
        .parallelStream()
        .forEach(observer -> observer.accept(observation));
  }
}
