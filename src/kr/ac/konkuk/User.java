
package kr.ac.konkuk;

import java.util.Objects;

public class User {
    private final String name;
    private final String id;
    private final int password;

    public User(String name, String id, int password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }

        User user = (User) obj;

        return id.equals(user.id)
                && name.equals(user.name)
                && password == user.password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, password);
    }
}
