package io.github.xezzon.tao.observer;

import io.github.xezzon.tao.exception.ServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author xezzon
 */
class ObserverContextTest {

  @Test
  void observer() {
    new Subscriber1();
    Publisher publisher = new Publisher();
    Assertions.assertDoesNotThrow(publisher::publish);
    new Subscriber2();
    Assertions.assertThrows(ServerException.class, publisher::publish);
  }
}

record Message(String value) implements Observation {

  public static final String DEFAULT = "message";
}

class Publisher {

  public void publish() {
    Observation observation = new Message(Message.DEFAULT);
    ObserverContext.post(observation);
  }
}

class Subscriber1 {

  Subscriber1() {
    ObserverContext.register(Message.class, this::subscribe);
  }

  public void subscribe(Message message) {
    Assertions.assertEquals(Message.DEFAULT, message.value());
  }
}

class Subscriber2 {

  Subscriber2() {
    ObserverContext.register(Message.class, this::subscribe);
  }

  private void subscribe(Message message) {
    throw new ServerException("test throw");
  }
}
