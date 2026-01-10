package View;
import java.util.Scanner;

import Controller.LinksController;
import Controller.UserController;

public class UserActions {

  String originalLink;
  int clickCount;

  Scanner linkScanner = new Scanner(System.in);

  UserController userController = new UserController();
  LinksController linksController = new LinksController();


  public void inputLinkInfo() {

    System.out.println("Укажите оригинальную ссылку: ");
    System.out.print("> ");

    if (linkScanner.hasNextLine()) {
      originalLink = linkScanner.nextLine();
    }

    System.out.println("Укажите количество переходов по ссылке.");
    System.out.println("По умолчанию количество переходов равно 10 (Введите 0 - оставить 10 кликов): ");
    System.out.print("> ");

    if (linkScanner.hasNextInt()) {
      clickCount = linkScanner.nextInt();
    }

    if (clickCount == 0) {
      clickCount = 10;
    }

    linksController.addNewLink(originalLink, clickCount);

    linkScanner.close();
  }
}
