package kr.ac.konkuk;

import kr.ac.konkuk.Exceptions.UserAlreadyExistsExeption;

import java.sql.*;

public class Database {
    private ResultSet connect(String sql) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:data.db");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            return rs;
        } catch (SQLException e) {
            System.out.println("오류 : " + e.getMessage());
        }
        return null;
    }

    public User getUser(String id) {
        ResultSet rs = this.connect("SELECT * FROM users WHERE id=" + id);
        if(rs != null) {
            try {
                return new User(rs.getString("name"), rs.getString("id"), rs.getInt("password"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public void addUser(User user) {
        User searchUser = getUser(user.id);
        if(searchUser == null) {
            throw new UserAlreadyExistsExeption();
        }
        else {
            this.connect("INSERT INTO user (name, id, password) VALUES (" + user.name + ", " + user.id + ", " + user.getPassword());
        }
    }

}
