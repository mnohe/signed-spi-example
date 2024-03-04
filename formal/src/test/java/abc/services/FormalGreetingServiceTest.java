package abc.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FormalGreetingServiceTest {
  @Test
  void given_noArgs_when_createMessage_then_formalGreeting() {
    FormalGreetingService sut = new FormalGreetingService();

    assertEquals("How do you do?", sut.createMessage());
  }
}
