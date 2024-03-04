package greeting.services;

import greeting.api.GreetingService;

/**
 * A service that says hello.
 */
public class HelloService implements GreetingService {
  private static final String MESSAGE = "Hello, world!";

  @Override
  public String createMessage() {
    return MESSAGE;
  }

}
