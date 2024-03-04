package greeting.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HelloServiceTest {
  @Test
  void given_noArgs_when_createMessage_then_helloWorld() {
    HelloService sut = new HelloService();

    assertEquals("Hello, world!", sut.createMessage());
  }
}
