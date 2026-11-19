package kr.ac.konkuk;

import kr.ac.konkuk.Exceptions.PasswordDoesNotMatchException;
import kr.ac.konkuk.Exceptions.UserNotFoundException;

public class UserManager {
    static Database db = new Database();

    public boolean login(User userInfo) {
        User searchUser = db.getUser(userInfo.id);
        return searchUser != null && userInfo.getPassword() == searchUser.getPassword();
    }

    public User login(String id, int password) {
        User user = db.getUser(id);
        if (user == null) throw new UserNotFoundException();
        if (user.getPassword() != password) throw new PasswordDoesNotMatchException();
        return user;
    }

    public void register(User user) {
        if (user.name == null || user.name.isBlank() || user.id == null || user.id.isBlank()) {
            throw new IllegalArgumentException("이름과 아이디를 모두 입력하세요.");
        }
        db.addUser(user);
    }
}
