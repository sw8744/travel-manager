package kr.ac.konkuk;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TripStore {
    public void initialize() {
        try (Connection conn = Database.openConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS trips ("
                        + "user_id INTEGER NOT NULL REFERENCES user_numbers(user_id), id INTEGER NOT NULL, "
                        + "city_id INTEGER NOT NULL, start_date TEXT NOT NULL, end_date TEXT NOT NULL, "
                        + "PRIMARY KEY(user_id, id))");
                createItemTable(stmt, "todos", "done INTEGER NOT NULL, start_date TEXT, end_date TEXT, description TEXT");
                createItemTable(stmt, "flights", "flight_number TEXT NOT NULL, departure TEXT NOT NULL, arrival TEXT NOT NULL, departure_time TEXT, arrival_time TEXT");
                createItemTable(stmt, "hotels", "name TEXT NOT NULL, country TEXT NOT NULL, city TEXT NOT NULL, check_in TEXT, check_out TEXT");
                createItemTable(stmt, "schedules", "start_date TEXT, end_date TEXT, description TEXT");
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("여행 저장소 초기화 오류: " + e.getMessage(), e);
        }
    }

    private void createItemTable(Statement stmt, String table, String columns) throws SQLException {
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " ("
                + "user_id INTEGER NOT NULL, trip_id INTEGER NOT NULL, id INTEGER NOT NULL, position INTEGER NOT NULL, "
                + columns + ", PRIMARY KEY(user_id, trip_id, id), "
                + "FOREIGN KEY(user_id, trip_id) REFERENCES trips(user_id, id) ON DELETE CASCADE)");
    }

    public List<Trip> loadTrips(int userId) {
        Map<Integer, Trip> trips = new LinkedHashMap<>();
        try (Connection conn = Database.openConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM trips WHERE user_id = ? ORDER BY id")) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Trip trip = new Trip(rs.getInt("id"), userId, rs.getInt("city_id"),
                                date(rs.getString("start_date")), date(rs.getString("end_date")));
                        trips.put(trip.getTripId(), trip);
                    }
                }
            }
            loadItems(conn, userId, trips);
            conn.commit();
        } catch (SQLException | IllegalArgumentException | java.time.DateTimeException e) {
            throw new IllegalStateException("여행 불러오기 오류: " + e.getMessage(), e);
        }
        return new ArrayList<>(trips.values());
    }

    private void loadItems(Connection conn, int userId, Map<Integer, Trip> trips) throws SQLException {
        try (PreparedStatement stmt = itemsQuery(conn, "todos", userId);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trips.get(rs.getInt("trip_id")).getTodoList().restore(new Todo(rs.getInt("id"), rs.getBoolean("done"),
                        date(rs.getString("start_date")), date(rs.getString("end_date")), rs.getString("description")));
            }
        }
        try (PreparedStatement stmt = itemsQuery(conn, "flights", userId);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trips.get(rs.getInt("trip_id")).getFlightList().restore(new Flight(rs.getInt("id"), rs.getString("flight_number"),
                        rs.getString("departure"), rs.getString("arrival"),
                        time(rs.getString("departure_time")), time(rs.getString("arrival_time"))));
            }
        }
        try (PreparedStatement stmt = itemsQuery(conn, "hotels", userId);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trips.get(rs.getInt("trip_id")).getHotelList().restore(new Hotel(rs.getInt("id"), rs.getString("name"),
                        rs.getString("country"), rs.getString("city"), date(rs.getString("check_in")), date(rs.getString("check_out"))));
            }
        }
        try (PreparedStatement stmt = itemsQuery(conn, "schedules", userId);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                trips.get(rs.getInt("trip_id")).getScheduleList().restore(new Schedule(rs.getInt("id"),
                        date(rs.getString("start_date")), date(rs.getString("end_date")), rs.getString("description")));
            }
        }
    }

    private PreparedStatement itemsQuery(Connection conn, String table, int userId) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + table + " WHERE user_id = ? ORDER BY trip_id, position");
        stmt.setInt(1, userId);
        return stmt;
    }

    public void saveTrips(int userId, List<Trip> trips) {
        for (Trip trip : trips) {
            if (trip.getUserId() != userId) {
                throw new IllegalArgumentException("다른 사용자의 여행은 저장할 수 없습니다.");
            }
        }
        try (Connection conn = Database.openConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM trips WHERE user_id = ?")) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }
                writeTrips(conn, userId, trips);
                conn.commit();
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("여행 저장 오류: " + e.getMessage(), e);
        }
    }

    private void writeTrips(Connection conn, int userId, List<Trip> trips) throws SQLException {
        try (PreparedStatement tripStmt = conn.prepareStatement("INSERT INTO trips VALUES (?, ?, ?, ?, ?)");
             PreparedStatement todoStmt = conn.prepareStatement("INSERT INTO todos VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
             PreparedStatement flightStmt = conn.prepareStatement("INSERT INTO flights VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
             PreparedStatement hotelStmt = conn.prepareStatement("INSERT INTO hotels VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
             PreparedStatement scheduleStmt = conn.prepareStatement("INSERT INTO schedules VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            for (Trip trip : trips) {
                int tripId = trip.getTripId();
                tripStmt.setInt(1, userId);
                tripStmt.setInt(2, tripId);
                tripStmt.setInt(3, trip.getCityId());
                tripStmt.setString(4, trip.getStartDate().toString());
                tripStmt.setString(5, trip.getEndDate().toString());
                tripStmt.executeUpdate();

                int position = 0;
                for (Todo item : trip.getTodoList().getTodos()) {
                    keys(todoStmt, userId, tripId, item.getId(), position++);
                    todoStmt.setBoolean(5, item.isDone());
                    todoStmt.setString(6, text(item.getStartDate()));
                    todoStmt.setString(7, text(item.getEndDate()));
                    todoStmt.setString(8, item.getDescription());
                    todoStmt.addBatch();
                }
                position = 0;
                for (Flight item : trip.getFlightList().getFlights()) {
                    keys(flightStmt, userId, tripId, item.getId(), position++);
                    flightStmt.setString(5, item.getFlightNumber());
                    flightStmt.setString(6, item.getDeparture());
                    flightStmt.setString(7, item.getArrival());
                    flightStmt.setString(8, text(item.getDepartureTime()));
                    flightStmt.setString(9, text(item.getArrivalTime()));
                    flightStmt.addBatch();
                }
                position = 0;
                for (Hotel item : trip.getHotelList().getHotels()) {
                    keys(hotelStmt, userId, tripId, item.getId(), position++);
                    hotelStmt.setString(5, item.getName());
                    hotelStmt.setString(6, item.getCountry());
                    hotelStmt.setString(7, item.getCity());
                    hotelStmt.setString(8, text(item.getCheckInDate()));
                    hotelStmt.setString(9, text(item.getCheckOutDate()));
                    hotelStmt.addBatch();
                }
                position = 0;
                for (Schedule item : trip.getScheduleList().getSchedules()) {
                    keys(scheduleStmt, userId, tripId, item.getId(), position++);
                    scheduleStmt.setString(5, text(item.getStartDate()));
                    scheduleStmt.setString(6, text(item.getEndDate()));
                    scheduleStmt.setString(7, item.getDescription());
                    scheduleStmt.addBatch();
                }
            }
            todoStmt.executeBatch();
            flightStmt.executeBatch();
            hotelStmt.executeBatch();
            scheduleStmt.executeBatch();
        }
    }

    private void keys(PreparedStatement stmt, int userId, int tripId, int id, int position) throws SQLException {
        stmt.setInt(1, userId);
        stmt.setInt(2, tripId);
        stmt.setInt(3, id);
        stmt.setInt(4, position);
    }

    private static String text(Object value) {
        return value == null ? null : value.toString();
    }

    private static LocalDate date(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    private static ZonedDateTime time(String value) {
        return value == null ? null : ZonedDateTime.parse(value);
    }
}
