package kr.ac.konkuk;

import kr.ac.konkuk.Exceptions.UserAlreadyExistsExeption;
import kr.ac.konkuk.Exceptions.UserNotFoundException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class Database {
    public static Connection openConnection() throws SQLException {
        Path path = databasePath();
        if (!Files.isRegularFile(path)) {
            throw new SQLException("DB 파일을 찾을 수 없습니다: " + path);
        }
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("PRAGMA busy_timeout = 5000");
        } catch (SQLException e) {
            conn.close();
            throw e;
        }
        return conn;
    }

    private static Path databasePath() {
        String configured = System.getProperty("travel.db.path");
        if (configured != null && !configured.isBlank()) {
            return Path.of(configured).toAbsolutePath().normalize();
        }
        Path found = findDatabase(Path.of("").toAbsolutePath());
        if (found != null) return found;
        try {
            Path classes = Path.of(Database.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            found = findDatabase(classes);
            if (found != null) return found;
        } catch (Exception ignored) {
            // 실행 위치와 클래스 위치 모두에서 찾지 못하면 기본 경로를 안내한다.
        }
        return Path.of("src/data.db").toAbsolutePath();
    }

    private static Path findDatabase(Path directory) {
        for (Path current = directory; current != null; current = current.getParent()) {
            Path candidate = current.resolve("src/data.db");
            if (Files.isRegularFile(candidate)) return candidate;
        }
        return null;
    }

    public void initialize() {
        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user (id TEXT NOT NULL, name TEXT NOT NULL, password INTEGER NOT NULL)");
                stmt.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS user_login_id ON user(id)");
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user_numbers (user_id INTEGER PRIMARY KEY AUTOINCREMENT, account_id TEXT NOT NULL UNIQUE REFERENCES user(id))");
                stmt.executeUpdate("INSERT OR IGNORE INTO user_numbers(account_id) SELECT id FROM user ORDER BY rowid");
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("DB 초기화 오류: " + e.getMessage(), e);
        }
    }

    public int getUserNumber(String id) {
        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM user_numbers WHERE account_id = ?")) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
            throw new UserNotFoundException();
        } catch (SQLException e) {
            throw new IllegalStateException("사용자 번호 조회 중 DB 오류: " + e.getMessage(), e);
        }
    }

    public User getUser(String id) {
        String sql = "SELECT name, id, password FROM user WHERE id = ?";
        try (Connection conn = openConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getString("name"), rs.getString("id"), rs.getInt("password"));
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("사용자 조회 중 DB 오류: " + e.getMessage(), e);
        }
        return null;
    }

    public void addUser(User user) {
        String sql = "INSERT INTO user (name, id, password) VALUES (?, ?, ?)";
        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 PreparedStatement number = conn.prepareStatement("INSERT INTO user_numbers(account_id) VALUES (?)")) {
                stmt.setString(1, user.name);
                stmt.setString(2, user.id);
                stmt.setInt(3, user.getPassword());
                stmt.executeUpdate();
                number.setString(1, user.id);
                number.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 && getUser(user.id) != null) {
                throw new UserAlreadyExistsExeption();
            }
            throw new IllegalStateException("회원가입 중 DB 오류: " + e.getMessage(), e);
        }
    }
}
