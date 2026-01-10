package Model;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import  java.util.UUID;


public class UserModel {

  ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

  File jsonFilePath = Paths.get("DB.json").toFile();
  JsonNode authData = null;

  
  public String userID () {
    try {
      authData = mapper.readTree(jsonFilePath).path("auth");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String auth = (authData.isNull() || authData.isMissingNode()) ? null : authData.asText();

    if (auth == null) {
      return this.addNewUser();
    }

    return auth;
  }

  public String addNewUser() {
    String userId = UUID.randomUUID().toString();
    writeIdInFile(userId);
    return userId;
  }

  public void writeIdInFile(String userId) {
    ObjectNode jsonDataTree;
    try {
      jsonDataTree = (ObjectNode) mapper.readTree(jsonFilePath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    jsonDataTree.put("auth", (String) userId);

    try {
      mapper.writeValue(jsonFilePath, jsonDataTree);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
