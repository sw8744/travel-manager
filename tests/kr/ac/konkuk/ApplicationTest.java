package kr.ac.konkuk;

import kr.ac.konkuk.Exceptions.PasswordDoesNotMatchException;
import kr.ac.konkuk.Exceptions.UserAlreadyExistsExeption;
import kr.ac.konkuk.Exceptions.UserNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTest {
    @TempDir Path directory;
    private String previousPath;
    private final Database db = new Database();
    private final TripStore store = new TripStore();
    private final UserManager users = new UserManager();
    private final LocalDate day = LocalDate.of(2026, 12, 1);

    @BeforeEach
    void prepareDatabase() throws Exception {
        previousPath = System.getProperty("travel.db.path");
        Path file = Files.createFile(directory.resolve("data.db"));
        System.setProperty("travel.db.path", file.toString());
        String notes = "electricity_note TEXT, safety_note TEXT, food_note TEXT, climate_note TEXT, transport_note TEXT, travel_alert_note TEXT";
        try (Connection conn = Database.openConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE countries (id INTEGER, name TEXT, " + notes + ")");
            stmt.executeUpdate("CREATE TABLE cities (id INTEGER, name TEXT, country INTEGER, " + notes + ")");
            stmt.executeUpdate("INSERT INTO countries(id, name, electricity_note) VALUES (1, '테스트국가', '국가 테스트 안내')");
            stmt.executeUpdate("INSERT INTO cities(id, name, country, transport_note) VALUES (1, '테스트도시', 1, '도시 테스트 안내')");
        }
        db.initialize();
        store.initialize();
    }

    @AfterEach
    void restorePath() {
        if (previousPath == null) System.clearProperty("travel.db.path");
        else System.setProperty("travel.db.path", previousPath);
    }

    private int register(String id) {
        users.register(new User("테스터", id, "비밀번호!123".hashCode()));
        return db.getUserNumber(id);
    }

    private Trip trip(int userId) {
        return new Trip(1, userId, 1, day, day.plusDays(3));
    }

    @Test
    void registrationAndLoginUsePasswordHash() {
        String password = "  Secret!한글123  ";
        User original = new User("O'Neil", "user'quoted", password.hashCode());
        users.register(original);
        assertEquals(password.hashCode(), db.getUser(original.getId()).getPassword());
        assertEquals(original, users.login(original.getId(), password.hashCode()));
        assertTrue(users.login(new User("이름을 입력하지 않아도 됨", original.getId(), password.hashCode())));
        try {
            users.login(original.getId(), password.trim().hashCode());
            fail("잘못된 비밀번호는 거부해야 합니다.");
        } catch (PasswordDoesNotMatchException e) {
            assertNotNull(e.getMessage());
        }
        try {
            users.login("missing", password.hashCode());
            fail("없는 사용자는 로그인할 수 없어야 합니다.");
        } catch (UserNotFoundException e) {
            assertNotNull(e.getMessage());
        }
        try {
            users.register(original);
            fail("같은 아이디를 중복 등록할 수 없어야 합니다.");
        } catch (UserAlreadyExistsExeption e) {
            assertNotNull(e.getMessage());
        }
        try {
            users.register(new User("", "blank", 1));
            fail("빈 이름은 거부해야 합니다.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }
        int number = db.getUserNumber(original.getId());
        db.initialize();
        store.initialize();
        assertEquals(number, new Database().getUserNumber(original.getId()));
    }

    @Test
    void existingAccountsGetStableNumbersWithoutChangingPasswords() throws Exception {
        try (Connection conn = Database.openConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("INSERT INTO user VALUES ('legacy', '기존 사용자', -12345)");
        }
        db.initialize();
        int number = db.getUserNumber("legacy");
        register("new-account");
        db.initialize();
        assertEquals(number, db.getUserNumber("legacy"));
        assertEquals(-12345, db.getUser("legacy").getPassword());
    }

    @Test
    void savesAllTravelDetailsWithIdsOrderingAndTimeZones() {
        int alice = register("alice");
        int bob = register("bob");
        Trip original = trip(alice);
        original.getTodoList().addTodo(false, null, null, "삭제할 항목");
        original.getTodoList().removeTodo(1);
        original.getTodoList().addTodo(true, day.plusDays(1), null, "늦은 할 일");
        original.getTodoList().addTodo(false, day, day, "이른 할 일");
        original.getTodoList().sortByDate();
        ZonedDateTime departure = ZonedDateTime.parse("2026-12-01T17:00+09:00");
        ZonedDateTime arrival = ZonedDateTime.parse("2026-12-01T10:00-08:00");
        original.getFlightList().addFlight("REMOVE", "ICN", "LAX", null, null);
        original.getFlightList().removeFlight(1);
        original.getFlightList().addFlight("KE1", "ICN", "LAX", departure, arrival);
        original.getHotelList().addHotel("O'Neil 호텔", "테스트국가", "테스트도시", null, day.plusDays(3));
        original.getScheduleList().addSchedule(day, null, "산책");
        store.saveTrips(alice, List.of(original));
        store.saveTrips(bob, List.of(trip(bob)));

        Trip loaded = new TripStore().loadTrips(alice).get(0);
        assertEquals(day, loaded.getStartDate());
        assertEquals(day.plusDays(3), loaded.getEndDate());
        assertEquals(List.of(3, 2), todoIds(loaded.getTodoList()));
        assertTrue(loaded.getTodoList().findTodo(2).isDone());
        assertNull(loaded.getTodoList().findTodo(2).getEndDate());
        assertEquals(departure, loaded.getFlightList().findFlight(2).getDepartureTime());
        assertEquals(arrival, loaded.getFlightList().findFlight(2).getArrivalTime());
        assertEquals("O'Neil 호텔", loaded.getHotelList().findHotel(1).getName());
        assertNull(loaded.getHotelList().findHotel(1).getCheckInDate());
        assertEquals("산책", loaded.getScheduleList().findSchedule(1).getDescription());
        assertNull(loaded.getScheduleList().findSchedule(1).getEndDate());
        assertTrue(store.loadTrips(bob).get(0).getTodoList().getTodos().isEmpty());
        try {
            store.saveTrips(alice, store.loadTrips(bob));
            fail("다른 사용자의 여행은 저장할 수 없어야 합니다.");
        } catch (IllegalArgumentException e) {
            assertNotNull(e.getMessage());
        }

        loaded.getTodoList().addTodo(false, null, null, "추가");
        loaded.getFlightList().addFlight("KE2", "ICN", "LAX", null, null);
        loaded.getHotelList().addHotel("추가 숙박", "국가", "도시", null, null);
        loaded.getScheduleList().addSchedule(null, null, "추가 일정");
        assertEquals("추가", loaded.getTodoList().findTodo(4).getDescription());
        assertEquals("KE2", loaded.getFlightList().findFlight(3).getFlightNumber());
        assertEquals("추가 숙박", loaded.getHotelList().findHotel(2).getName());
        assertEquals("추가 일정", loaded.getScheduleList().findSchedule(2).getDescription());
        loaded.getTodoList().removeTodo(2);
        loaded.getFlightList().removeFlight(2);
        loaded.getHotelList().removeHotel(1);
        loaded.getScheduleList().removeSchedule(1);
        store.saveTrips(alice, List.of(loaded));
        Trip reloaded = store.loadTrips(alice).get(0);
        assertEquals(List.of(3, 4), todoIds(reloaded.getTodoList()));
        assertEquals(1, reloaded.getFlightList().getFlights().size());
        assertNull(reloaded.getFlightList().findFlight(3).getArrivalTime());
        assertEquals(1, reloaded.getHotelList().getHotels().size());
        assertEquals(1, reloaded.getScheduleList().getSchedules().size());
    }

    @Test
    void failedSaveRollsBackWithoutLosingPreviousData() throws Exception {
        int userId = register("alice");
        Trip original = trip(userId);
        original.getTodoList().addTodo(false, null, null, "기존 데이터");
        store.saveTrips(userId, List.of(original));
        try {
            store.saveTrips(userId, List.of(original, original));
            fail("여행 번호가 중복되면 저장을 취소해야 합니다.");
        } catch (IllegalStateException e) {
            assertNotNull(e.getMessage());
        }
        assertEquals("기존 데이터", store.loadTrips(userId).get(0).getTodoList().findTodo(1).getDescription());
        try (Connection conn = Database.openConnection(); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA foreign_key_check")) {
            assertFalse(rs.next());
        }
        store.saveTrips(userId, List.of());
        assertTrue(store.loadTrips(userId).isEmpty());
    }

    @Test
    void mainWorkflowSurvivesRestartAndEndOfInput() {
        String output = runMain("2", "테스터", "alice", "Secret!123",
                "1", "alice", "wrong", "1", "alice", "Secret!123",
                "1", "테스트", "1", "2026-12-01", "2026-12-04",
                "1", "2", "여권 챙기기", "", "", "5", "1", "invalid", "true", "0",
                "2", "2", "KE001", "ICN", "NRT", "2026-12-01 09:00", "+09:00", "2026-12-01 11:00", "+09:00", "0",
                "3", "2", "테스트호텔", "테스트국가", "테스트도시", "2026-12-01", "2026-12-04", "0",
                "4", "2", "산책하기", "2026-12-02", "2026-12-02", "0",
                "5", "0", "2", "0", "0");
        assertTrue(output.contains("로그인에 실패했습니다."));
        assertTrue(output.contains("true 또는 false를 입력하세요."));
        assertTrue(output.contains("국가 테스트 안내"));
        assertTrue(output.contains("도시 테스트 안내"));
        assertTrue(output.contains("프로그램을 종료합니다."));
        assertFalse(output.contains("DB 오류"), output);
        int userId = db.getUserNumber("alice");
        Trip loaded = store.loadTrips(userId).get(0);
        assertTrue(loaded.getTodoList().findTodo(1).isDone());
        assertEquals("KE001", loaded.getFlightList().findFlight(1).getFlightNumber());
        assertEquals("테스트호텔", loaded.getHotelList().findHotel(1).getName());
        assertEquals("산책하기", loaded.getScheduleList().findSchedule(1).getDescription());

        output = runMain("1", "alice", "Secret!123", "3", "1", "1", "2", "EOF 이전 추가", "", "");
        assertTrue(output.contains("여권 챙기기"));
        assertTrue(output.contains("입력이 종료되어 프로그램을 종료합니다."));
        assertEquals("EOF 이전 추가", store.loadTrips(userId).get(0).getTodoList().findTodo(2).getDescription());

        output = runMain("2", "다른 사용자", "bob", "Secret!456", "1", "bob", "Secret!456", "2", "3", "1", "0", "0");
        assertTrue(output.contains("등록된 여행이 없습니다."));
        assertTrue(output.contains("해당 여행이 없습니다."));
        assertFalse(output.contains("여권 챙기기"));
    }

    @Test
    void invalidTripInputReturnsToMenuWithoutSavingBadData() {
        register("alice");
        String output = runMain("bad", "1", "alice", "비밀번호!123",
                "1", "테스트", "not-a-number",
                "1", "테스트", "999",
                "1", "테스트", "1", "bad-date",
                "1", "테스트", "1", "2026-12-03", "2026-12-01",
                "1", "테스트", "1", "2026-12-01", "2026-12-04", "0", "0", "0");
        assertTrue(output.contains("잘못된 메뉴 번호입니다."));
        assertTrue(output.contains("검색 결과에 없는 도시 번호입니다."));
        assertTrue(output.contains("날짜 형식은 YYYY-MM-DD로 입력하세요."));
        assertTrue(output.contains("여행 날짜가 올바르지 않습니다."));
        List<Trip> loaded = store.loadTrips(db.getUserNumber("alice"));
        assertEquals(1, loaded.size());
        assertEquals(1, loaded.get(0).getTripId());
    }

    @Test
    void missingDatabaseDoesNotCreateAnEmptyDatabase() {
        Path missing = directory.resolve("missing.db");
        System.setProperty("travel.db.path", missing.toString());
        assertTrue(runMain("0").contains("DB 파일을 찾을 수 없습니다"));
        assertFalse(Files.exists(missing));
    }

    @Test
    void cautionMenuOpensTodoManagement() throws Exception {
        Trip trip = trip(register("alice"));
        try (Scanner scan = new Scanner("1\n2\n할 일 연결 확인\n\n\n0\n0\n")) {
            TravelCautionMenu.start(trip, new TravelCautionManager(), scan);
        }
        assertEquals("할 일 연결 확인", trip.getTodoList().findTodo(1).getDescription());
    }

    @Test
    void existingMenuChecksStillPass() {
        MenuCheck.main(new String[0]);
    }

    private List<Integer> todoIds(TodoList list) {
        List<Integer> ids = new ArrayList<>();
        for (Todo todo : list.getTodos()) {
            ids.add(todo.getId());
        }
        return ids;
    }

    private String runMain(String... lines) {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream((String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8)));
            System.setOut(output);
            Main.main(new String[]{"--console"});
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    @Test
    void passwordRegexTest() {
        String output = runMain(
                "2", "테스터", "weak1", "hello123",
                "2", "테스터", "weak2", "12345678!",
                "2", "테스터", "weak3", "Ab1!",
                "2", "테스터", "valid1", "Java123!",
                "0"
        );

        assertNull(db.getUser("weak1"));
        assertNull(db.getUser("weak2"));
        assertNull(db.getUser("weak3"));

        assertNotNull(db.getUser("valid1"));
        assertTrue(output.contains("비밀번호는 8~20자여야 합니다."),output);
        assertTrue(output.contains("회원가입이 완료되었습니다."));
    }

}
