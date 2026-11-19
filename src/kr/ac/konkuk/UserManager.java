package kr.ac.konkuk;

import kr.ac.konkuk.Exceptions.UserAlreadyExistsExeption;

public class UserManager {
    static Database db = new Database();

    public boolean login(User userInfo) {
        User searchUser = db.getUser(userInfo.id);
        return userInfo.equals(searchUser);
    }

    public void register(User user) {
        try {
            db.addUser(user);
        } catch (UserAlreadyExistsExeption e) {
            throw e;
        }
    }
}
