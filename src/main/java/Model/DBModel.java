package Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class DBModel {

  UserModel user = new UserModel();

  public ObjectMapper getMapper() {
    return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  }

  public File getJsonFile() {
    return Paths.get("DB.json").toFile();
  }

  public ObjectNode getJsonDataTree(ObjectMapper mapper, File jsonFilePath) {
    try {
      return  (ObjectNode) mapper.readTree(jsonFilePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ArrayNode getLinksList(ObjectNode jsonDataTree) {
    return (ArrayNode) jsonDataTree.get("links");
  }

  public ArrayNode getUserLinksList(ObjectNode jsonDataTree) {
    ArrayNode userLinks = this.getMapper().createArrayNode();
    String userID = user.getUserId();
    ArrayNode allLinks = (ArrayNode) jsonDataTree.get("links");

    for (JsonNode nodeLink : allLinks) {
      String userIdInNode = nodeLink.get("userID").asText();
      if (userIdInNode.equals(userID)) {
        userLinks.add(nodeLink);
      }
    }
    return userLinks;
  }


}
