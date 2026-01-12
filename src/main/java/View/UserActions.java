package View;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
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
  String selectedLink;
  JsonNode selectedLinkNode;

  Scanner linkScanner = new Scanner(System.in);

  UserController userController = new UserController();
  LinksController linksController = new LinksController();
  DBController dbController = new DBController();

  ObjectMapper mapper = dbController.getMapper();
  File jsonFilePath = dbController.getJsonFile();

  public boolean inputLinkInfo() {
    ColorPrinter.println(ColorPrinter.Color.GREEN, "Укажите оригинальную ссылку:");
    ColorPrinter.println(ColorPrinter.Color.WHITE, "(Введите L для отображения доступных ссылок)");
    ColorPrinter.print(ColorPrinter.Color.GREEN, "> ");

    if (linkScanner.hasNextLine()) {
      originalLink = linkScanner.nextLine();
    }

    if (originalLink.equals("L")) {
      return true;
    }

    boolean linkIsValid = linksController.linkValidation(originalLink);
    if (!linkIsValid) {
      ColorPrinter.println(ColorPrinter.Color.RED, "Некорректный формат ссылки:");
      ColorPrinter.println(ColorPrinter.Color.RED, originalLink);
      System.out.println();
      return false;
    }

    JsonNode linkMatches = linksController.searchLinkInDB(originalLink, true);
    boolean duplicateLink = linksController.duplicateLinksFromOtherOwners(originalLink);

    if (linkMatches != null) {
      System.out.println();
      ColorPrinter.println(ColorPrinter.Color.RED, "Такая ссылка уже записана:");
      this.linkInformation(linkMatches);
      return true;
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

    LinkedHashMap<String, String> newLinkInfo = linksController.addNewLink(originalLink, clickCount, duplicateLink);
    System.out.println();
    ColorPrinter.println(ColorPrinter.Color.GREEN,"Информация по новой записи:");
    this.linkInformation(newLinkInfo);
    return true;
  }

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
      if (linkNode.get("available").asBoolean()) {
        ColorPrinter.print(ColorPrinter.Color.GREEN, "Да" + " ");
      } else {
        ColorPrinter.print(ColorPrinter.Color.RED, "Нет" + " ");
      }

      ColorPrinter.print(ColorPrinter.Color.WHITE, "Доступна до: ");
      ColorPrinter.print(ColorPrinter.Color.YELLOW, (linkNode.get("lifeTimeInHours").asText()) + " ");
      ColorPrinter.print(ColorPrinter.Color.WHITE, "Переходов осталось: ");
      ColorPrinter.println(ColorPrinter.Color.YELLOW, linkNode.get("availableClickCounts").asText());
    }
    System.out.println();

    this.openLink(linksList);
  }

  public void openLink(ArrayNode linksList) {
    ColorPrinter.println(ColorPrinter.Color.GREEN, "Укажите id ссылки для взаимодействия:" );
    ColorPrinter.println(ColorPrinter.Color.WHITE, "(Введите 0 для завершения работы)");
    ColorPrinter.print(ColorPrinter.Color.GREEN, "> ");

    if (linkScanner.hasNextInt()) {
      selectedLinkId = linkScanner.nextInt();
    }

    if (selectedLinkId == 0) {
      return;
    }

    for (JsonNode linkNode : linksList) {
      JsonNode linkNodeID = linkNode.get("id");
      int originalLinkID = linkNodeID.asInt();
      if (originalLinkID == selectedLinkId) {
        selectedLink = linkNode.get("originalLink").asText();
        selectedLinkNode = linkNode;
      }
    }

    if (selectedLinkNode == null) {
      System.out.println();
      ColorPrinter.print(ColorPrinter.Color.RED, "Ссылка с ID ");
      ColorPrinter.print(ColorPrinter.Color.YELLOW, String.valueOf(selectedLinkId));
      ColorPrinter.print(ColorPrinter.Color.RED, " не доступна!");
      return;
    }

    String selectedShortLink = selectedLinkNode.get("shortLink").asText();
    int actualClickCount = selectedLinkNode.get("availableClickCounts").asInt();

    if (actualClickCount == 0) {
      System.out.println();
      ColorPrinter.print(ColorPrinter.Color.RED, "Ссылка ");
      ColorPrinter.print(ColorPrinter.Color.YELLOW, selectedShortLink);
      ColorPrinter.print(ColorPrinter.Color.RED, " с ID ");
      ColorPrinter.print(ColorPrinter.Color.YELLOW, String.valueOf(selectedLinkId));
      ColorPrinter.print(ColorPrinter.Color.RED, " больше не доступна!");

      return;
    }

    int newClickCounts = actualClickCount - 1;

    if (newClickCounts <= 0) {
      linksController.changeLinkInfoInFile(selectedLinkId, "available", "false");
    }

    if (newClickCounts >= 0) {
      linksController.changeLinkInfoInFile(selectedLinkId, "availableClickCounts", String.valueOf(newClickCounts));
    }

    try {
      Desktop.getDesktop().browse(new URI(selectedLink));
      System.out.println();
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
    System.out.println();
    System.out.println();
    ColorPrinter.println(ColorPrinter.Color.GREEN, "Приложение завершило свою работу!" );
    linkScanner.close();
  }
}
