package Controller;
import Model.LinkModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.LinkedHashMap;

public class LinksController {

  LinkModel linkModel = new LinkModel();

  public LinkedHashMap<String, String> addNewLink(String originalLink, int clickCount) {
    return linkModel.addNewLink(originalLink, clickCount);
  }

  public JsonNode searchLinkInDB(String getLink) {
    return linkModel.searchLinkInDB(getLink);
  }

  public boolean linkValidation(String str) {
    return linkModel.linkValidation(str);
  }

  public void changeLinkInfoInFile(int selectedLinkId, String keyName, String newValue) {
    linkModel.changeLinkInfoInFile(selectedLinkId, keyName, newValue);
  }
}
