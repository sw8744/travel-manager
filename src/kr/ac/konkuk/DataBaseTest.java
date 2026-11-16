
package kr.ac.konkuk;

import java.sql.*;

public class DataBaseTest {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:data.db");

            String sql = "SELECT id, name FROM countries";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                System.out.println(id + " : " + name);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException e) {
            System.out.println("오류 : " + e.getMessage());
        }
    }
}
