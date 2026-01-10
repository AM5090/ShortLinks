import View.UserActions;

import java.io.FileNotFoundException;

public class Main {
  public static void main(String[] args) {
    UserActions userActions = new UserActions();

    userActions.inputLinkInfo();

    userActions.showMyLinks();

    userActions.closeScanner();
  }
}