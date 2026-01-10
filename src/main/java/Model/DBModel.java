package Model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class DBModel {


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
}
