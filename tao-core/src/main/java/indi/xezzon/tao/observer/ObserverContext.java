package indi.xezzon.tao.observer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 观察者调度中心
 * @author xezzon
 */
public class ObserverContext {

  private static final Map<Class<? extends Observation>, List<Consumer>> OBSERVER_MAP = new ConcurrentHashMap<>();

  /**
   * 将观察者注册到调度中心
   * @param clazz 消息类型
   * @param consumer 观察者
   * @param <T> 消息类型泛型
   */
  public static <T extends Observation> void register(Class<T> clazz, Consumer<T> consumer) {
    List<Consumer> observers = OBSERVER_MAP.getOrDefault(clazz,
        new LinkedList<>());
    observers.add(consumer);
    OBSERVER_MAP.put(clazz, observers);
  }

  /**
   * 消息推送给观察者
   * @param observation 消息
   * @param <T> 消息类型泛型
   */
  public static <T extends Observation> void post(T observation) {
    List<Consumer> observers = OBSERVER_MAP.getOrDefault(observation.getClass(),
        Collections.emptyList());
    observers.forEach(observer -> observer.accept(observation));
  }
}
