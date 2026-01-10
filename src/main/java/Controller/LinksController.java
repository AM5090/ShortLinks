package Controller;
import Model.LinkModel;

public class LinksController {

  LinkModel linkModel = new LinkModel();

  public void addNewLink(String originalLink, int clickCount) {
    linkModel.addNewLink(originalLink, clickCount);
  }
}
