package Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Random;

public class LinkModel {

  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  private static final byte SHORT_LINK_LENGTH = 8;

  UserModel userModel = new UserModel();
  DBModel dbModel = new DBModel();

  ObjectMapper mapper = dbModel.getMapper();
  File jsonFilePath = dbModel.getJsonFile();

  public LinkedHashMap<String, String> addNewLink(String originalLink, int clickCount) {
    String domain = this.selectDomainName(originalLink);
    String shortLink = this.generateShortString(domain);
    String userID = userModel.userID();
    String lifeLinkLife = this.getLinkLife();

    LinkedHashMap<String, String> linkInfoItem = new LinkedHashMap<>();
    linkInfoItem.put("originalLink", originalLink);
    linkInfoItem.put("shortLink", shortLink);
    linkInfoItem.put("clickCounts", String.valueOf(clickCount));
    linkInfoItem.put("availableClickCounts", String.valueOf(clickCount));
    linkInfoItem.put("lifeTimeInHours", lifeLinkLife);
    linkInfoItem.put("available", String.valueOf(true));
    linkInfoItem.put("userID", userID);

    return this.writeLinkInfoInFile(linkInfoItem);
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

  public LinkedHashMap<String, String> writeLinkInfoInFile(LinkedHashMap<String, String> mapForWrite) {
    ObjectNode jsonDataTree = dbModel.getJsonDataTree(mapper, jsonFilePath);
    ArrayNode linksList = dbModel.getLinksList(jsonDataTree);

    mapForWrite.put("id", String.valueOf(linksList.size() + 1));
    ObjectNode newLink = mapper.valueToTree(mapForWrite);
    linksList.add(newLink);

    try {
      mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFilePath, jsonDataTree);
      return mapForWrite;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void changeLinkInfoInFile(int selectedLinkId, String keyName, String newValue) {
    ObjectNode jsonDataTree = dbModel.getJsonDataTree(mapper, jsonFilePath);
    ArrayNode linksList = dbModel.getLinksList(jsonDataTree);

    for (JsonNode linkNode : linksList) {
      JsonNode linkNodeID = linkNode.get("id");
      int originalLinkID = linkNodeID.asInt();
      if (originalLinkID == selectedLinkId) {
        ObjectNode linkObject = (ObjectNode) linkNode;
        linkObject.put(keyName, newValue);
      }
    }

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

  public String getLinkLife() {
    Calendar calendar = Calendar.getInstance();

    calendar.add(Calendar.DAY_OF_YEAR, 1);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(calendar.getTime());
  }

  public boolean linkValidation(String str) {
    if (str.isEmpty()) return false;
    String regex = "^(http|https)://.*\\.[a-z]{2,6}(/.*)?";
    return str.matches(regex);
  }
}
