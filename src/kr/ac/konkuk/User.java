package kr.ac.konkuk;

import java.sql.*;

public class User {
    public String name;
    public String id;
    private int password;

    public User(String name, String id, int password) {
        this.name = name;
        this.id = id;
        this.password = password;
    }

    public int getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof User) {
            User u = (User) obj;
            if(u.id.equals(this.id) && u.name.equals(this.name) && u.password == this.password) {
                return true;
            }
        }
        return false;
    }
}
