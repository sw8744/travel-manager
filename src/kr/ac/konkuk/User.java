package kr.ac.konkuk;

import java.sql.*;

public class User {
    public String name;
    public String id;
    private int password;

    public class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }

        public UserNotFoundException() {
            super("User가 검색되지 않음");
        }
    }

    public class PasswordDoesNotMatchException extends RuntimeException {
        public PasswordDoesNotMatchException(String message) {
            super(message);
        }

        public PasswordDoesNotMatchException() {
            super("비밀번호가 다름");
        }
    }

    public User(String id, String password) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:data.db");

            String sql = "SELECT * FROM user WHERE id=" + id;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()) {
                if(rs.getInt("password") == password.hashCode()) {
                    this.name = rs.getString("name");
                    this.id = rs.getString("id");
                    this.password = rs.getInt("password");
                }
                else {
                    throw new PasswordDoesNotMatchException();
                }
            }
            else {
                throw new UserNotFoundException();
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("오류 : " + e.getMessage());
        } catch (UserNotFoundException e) {
            System.out.println("User가 검색되지 않음");
        } catch (PasswordDoesNotMatchException e) {
            System.out.println("비밀번호가 다름");
        }
    }

}
