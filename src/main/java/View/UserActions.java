package View;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Scanner;

import Controller.DBController;
import Controller.LinksController;
import Controller.UserController;
import Shared.ColorPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserActions {

  String originalLink;
  int clickCount;
  int selectedLinkId;
  String selectedLink; // ссылка для перехода


  Scanner linkScanner = new Scanner(System.in);

  UserController userController = new UserController();
  LinksController linksController = new LinksController();
  DBController dbController = new DBController();

  ObjectMapper mapper = dbController.getMapper();
  File jsonFilePath = dbController.getJsonFile();

  public void showMyLinks() {
    String userID = userController.userID();
    ObjectNode jsonDataTree = dbController.getJsonDataTree(mapper, jsonFilePath);

    ColorPrinter.println(ColorPrinter.Color.GREEN,"Ваш ID: " + userID);
    ColorPrinter.println(ColorPrinter.Color.GREEN,"Доступные ссылки для управления:");
    ArrayNode linksList = dbController.getUserLinksList(jsonDataTree);

    for (JsonNode linkNode : linksList) {
      ColorPrinter.print(ColorPrinter.Color.RED, linkNode.get("id").asText() + ": ");
      ColorPrinter.print(ColorPrinter.Color.WHITE, "Ссылка: " );
      ColorPrinter.print(ColorPrinter.Color.YELLOW, linkNode.get("shortLink").asText() + " ");
      ColorPrinter.print(ColorPrinter.Color.WHITE, "Доступна: ");
      ColorPrinter.print(ColorPrinter.Color.YELLOW, (linkNode.get("available").asBoolean() ? "Да" : "Нет") + " ");
      ColorPrinter.print(ColorPrinter.Color.WHITE, "Переходов осталось: ");
      ColorPrinter.println(ColorPrinter.Color.YELLOW, linkNode.get("availableClickCounts").asText());
    }
    System.out.println();

    this.openLink(linksList);
  }

  public void inputLinkInfo() {
    ColorPrinter.println(ColorPrinter.Color.GREEN, "Укажите оригинальную ссылку:");
    ColorPrinter.print(ColorPrinter.Color.GREEN, "> ");

    if (linkScanner.hasNextLine()) {
      originalLink = linkScanner.nextLine();
    }

    JsonNode linkMatches = linksController.searchLinkInDB(originalLink);

    if (linkMatches != null) {
      System.out.println();
      ColorPrinter.println(ColorPrinter.Color.RED, "Такая ссылка уже записана:");
      this.linkInformation(linkMatches);
      return;
    }

    System.out.println();
    ColorPrinter.println(ColorPrinter.Color.GREEN, "Укажите количество переходов по ссылке.");
    ColorPrinter.println(ColorPrinter.Color.GREEN, "По умолчанию количество переходов равно 10 (Введите 0 - оставить 10 кликов):");
    ColorPrinter.print(ColorPrinter.Color.GREEN,"> ");

    if (linkScanner.hasNextInt()) {
      clickCount = linkScanner.nextInt();
    }

    if (clickCount == 0) {
      clickCount = 10;
    }

    LinkedHashMap<String, String> newLinkInfo = linksController.addNewLink(originalLink, clickCount);
    System.out.println();
    ColorPrinter.println(ColorPrinter.Color.GREEN,"Информация по новой записи:");
    this.linkInformation(newLinkInfo);
  }

  public void openLink(ArrayNode linksList) {
    ColorPrinter.println(ColorPrinter.Color.GREEN, "Укажите id ссылки для взаимодействия" );
    ColorPrinter.print(ColorPrinter.Color.GREEN, "> ");

    if (linkScanner.hasNextInt()) {
      selectedLinkId = linkScanner.nextInt();
    }

    for (JsonNode linkNode : linksList) {
      JsonNode linkNodeID = linkNode.get("id");
      int originalLink = linkNodeID.asInt();
      if (originalLink == selectedLinkId) {
        selectedLink = linkNode.get("originalLink").asText();
      }
    }

    try {
      Desktop.getDesktop().browse(new URI(selectedLink));
      ColorPrinter.print(ColorPrinter.Color.GREEN, "Ссылка открыта в браузере!");
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public void linkInformation(JsonNode linkMatch) {
    if (linkMatch != null) {
      linkMatch.properties().forEach(entry -> {
        ColorPrinter.print(ColorPrinter.Color.WHITE, entry.getKey() + ": ");
        ColorPrinter.println(ColorPrinter.Color.YELLOW, entry.getValue().asText());
      });
      System.out.println();
    }
  }

  public void linkInformation(LinkedHashMap<String, String> linkMatch) {
    if (linkMatch != null) {
      linkMatch.forEach((key, value) -> {
        ColorPrinter.print(ColorPrinter.Color.WHITE, key + ": ");
        ColorPrinter.println(ColorPrinter.Color.YELLOW, value);
      });
      System.out.println();
    }
  }

  public void closeScanner() {
    linkScanner.close();
  }
}
