package Controller;

import Model.DBModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;

public class DBController {
  DBModel dbModel = new DBModel();

  public ObjectMapper getMapper() {
    return dbModel.getMapper();
  }

  public File getJsonFile() {
    return dbModel.getJsonFile();
  }

  public ObjectNode getJsonDataTree(ObjectMapper mapper, File jsonFilePath) {
    return dbModel.getJsonDataTree(mapper, jsonFilePath);
  }

  public ArrayNode getLinksList(ObjectNode jsonDataTree) {
    return dbModel.getLinksList(jsonDataTree);
  }

  public ArrayNode getUserLinksList(ObjectNode jsonDataTree) {
    return dbModel.getUserLinksList(jsonDataTree);
  }
}
