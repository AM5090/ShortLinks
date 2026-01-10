package Controller;
import Model.UserModel;


public class UserController {

  UserModel userModel = new UserModel();

  public void userID () {

    String id = userModel.userID();

    System.out.println("id >>>  " + id);
  }

}
