package Controller;
import Model.LinkModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;

public class LinksController {

  LinkModel linkModel = new LinkModel();

  public LinkedHashMap<String, String> addNewLink(String originalLink, int clickCount, boolean duplicate) {
    return linkModel.addNewLink(originalLink, clickCount, duplicate);
  }

  public JsonNode searchLinkInDB(String getLink, boolean findInUserLinks) {
    return linkModel.searchLinkInDB(getLink, findInUserLinks);
  }

  public boolean linkValidation(String link) {
    return linkModel.linkValidation(link);
  }

  public void changeLinkInfoInFile(int selectedLinkId, String keyName, String newValue) {
    linkModel.changeLinkInfoInFile(selectedLinkId, keyName, newValue);
  }

  public boolean duplicateLinksFromOtherOwners(String originalLink) {
    return linkModel.duplicateLinksFromOtherOwners(originalLink);
  }

  public ArrayNode checkingLinkTimeAvailable(ArrayNode linksList) {
    return linkModel.checkingLinkTimeAvailable(linksList);
  }
}
