import View.UserActions;

import java.io.FileNotFoundException;

public class Main {
  public static void main(String[] args) {
    UserActions userActions = new UserActions();

    boolean stopApplication = userActions.inputLinkInfo();

    if (!stopApplication) return;

    userActions.showMyLinks();

    userActions.closeScanner();
  }
}