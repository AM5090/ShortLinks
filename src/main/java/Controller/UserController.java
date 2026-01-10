package Controller;
import Model.UserModel;


public class UserController {

  UserModel userModel = new UserModel();

  public String userID () {
    return userModel.userID();
  }

}
