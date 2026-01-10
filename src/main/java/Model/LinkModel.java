package Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Random;

public class LinkModel {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final byte SHORT_LINK_LENGTH = 8;
  private static final byte LIFE_TIME_IN_HOURS = 24;

  UserModel userModel = new UserModel();
  DBModel dbModel = new DBModel();

  ObjectMapper mapper = dbModel.getMapper();
  File jsonFilePath = dbModel.getJsonFile();

  public void addNewLink(String originalLink, int clickCount) {
    String domain = this.selectDomainName(originalLink);
    String shortLink = this.generateShortString(domain);
    String userID = userModel.userID();

    JsonNode linkMatch = searchLinkInDB(originalLink);

    if (linkMatch != null) {
      System.out.println("Такая ссылка уже записана:");
      linkMatch.properties().forEach(entry -> {
        System.out.println(entry.getKey() + ": " + entry.getValue().asText());
      });
      return;
    }

    LinkedHashMap<String, String> linkInfoItem = new LinkedHashMap<>();
    linkInfoItem.put("originalLink", originalLink);
    linkInfoItem.put("shortLink", shortLink);
    linkInfoItem.put("clickCount", String.valueOf(clickCount));
    linkInfoItem.put("lifeTimeInHours", String.valueOf(LIFE_TIME_IN_HOURS));
    linkInfoItem.put("available", String.valueOf(true));
    linkInfoItem.put("userID", userID);

    this.writeLinkInfoInFile(linkInfoItem);
  }

  private String generateShortString(String domain) {
    Random random = new Random();
    StringBuilder sb = new StringBuilder(SHORT_LINK_LENGTH);

    for (int i = 0; i < SHORT_LINK_LENGTH; i++) {
      int index = random.nextInt(CHARACTERS.length());
      sb.append(CHARACTERS.charAt(index));
    }

    return "click" + domain + "/" + sb;
  }

  public String selectDomainName(String url) {
    try {
      URI uri = new URI(url);
      String host = uri.getHost();

      if (host != null && !host.isEmpty()) {
        String[] parts = host.split("\\.");
        if (parts.length > 0) {
          return "." + parts[parts.length - 1];
        }
      }
      return "";
    } catch (URISyntaxException e) {
      throw new RuntimeException("Некорректный URL: " + url, e);
    }
  }

  public void writeLinkInfoInFile(LinkedHashMap<String, String> mapForWrite) {
    ObjectNode jsonDataTree = dbModel.getJsonDataTree(mapper, jsonFilePath);
    ArrayNode linksList = dbModel.getLinksList(jsonDataTree);

    mapForWrite.put("id", String.valueOf(linksList.size() + 1));
    ObjectNode newLink = mapper.valueToTree(mapForWrite);
    linksList.add(newLink);

    try {
      mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFilePath, jsonDataTree);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public JsonNode searchLinkInDB(String getLink) {
    ObjectNode jsonDataTree = dbModel.getJsonDataTree(mapper, jsonFilePath);
    ArrayNode linksList = dbModel.getLinksList(jsonDataTree);

    for (JsonNode linkNode : linksList) {
      JsonNode originalLinkNode = linkNode.get("originalLink");
      String originalLink = originalLinkNode.asText();
      if (originalLink.equals(getLink)) {
        return linkNode;
      }
    }

    return null;
  }
}
