package abc.services;

import greeting.api.GreetingService;

public class FormalGreetingService implements GreetingService {
  private static final String MESSAGE = "How do you do?";
  public String createMessage() {
    return MESSAGE;
  }

}
