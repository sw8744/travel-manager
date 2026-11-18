package kr.ac.konkuk;

import java.io.*;
import java.time.*;
import java.util.*;

// Run after compiling src and this file with Java 17 or later.
public class MenuCheck {
    private static int checks;

    private static void check(boolean condition) {
        checks++;
        if (!condition) throw new AssertionError("Check " + checks);
    }

    private static void rejected(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException e) {
            checks++;
            return;
        }
        throw new AssertionError("Expected rejection");
    }

    private static Scanner input(String... lines) {
        return new Scanner(String.join("\n", lines) + "\n");
    }

    public static void main(String[] args) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            LocalDate day = LocalDate.of(2026, 12, 1);
            check(MenuInput.display("NRT").equals("NRT"));
            check(MenuInput.display("TEST HOTEL").equals("TEST HOTEL"));
            check(MenuInput.id(input("text", "1")) == 1);
            check(MenuInput.date(input("2026-02-30", "2026-12-01"), "date").equals(day));
            check(MenuInput.editDate(input(""), "date", day).equals(day));
            check(MenuInput.editDate(input("없음"), "date", day) == null);
            check(MenuInput.required(input("", "value"), "required").equals("value"));
            ZonedDateTime departure = ZonedDateTime.of(2026, 12, 1, 17, 0, 0, 0, ZoneId.of("Asia/Seoul"));
            ZonedDateTime arrival = ZonedDateTime.of(2026, 12, 1, 10, 0, 0, 0, ZoneId.of("America/Los_Angeles"));
            Flight flight = new Flight(1, "KE1", "ICN", "LAX", departure, arrival);
            check(flight.getArrivalTime().toInstant().isAfter(departure.toInstant()));
            rejected(() -> flight.update("KE2", "ICN", "LAX", arrival, departure));
            check(flight.getFlightNumber().equals("KE1"));
            rejected(() -> new Flight(2, "", "ICN", "LAX", null, null));
            ZonedDateTime parsed = MenuInput.time(input("bad", "2026-12-01 17:00", "Bad/Zone",
                    "2026-12-01 17:00", "Asia/Seoul"), "time", null, false);
            check(parsed.equals(departure));
            ZonedDateTime dst = MenuInput.time(input("2026-03-08 02:30", "America/Los_Angeles",
                    "2026-03-08 03:30", "America/Los_Angeles"), "time", null, false);
            check(dst.getHour() == 3);
            ZonedDateTime overlap = MenuInput.time(input("2026-11-01 01:30", "America/Los_Angeles", "-08:00"), "time", null, false);
            check(overlap.getOffset().equals(ZoneOffset.of("-08:00")));
            check(MenuInput.time(input(""), "time", departure, true).equals(departure));
            check(MenuInput.time(input("없음"), "time", departure, true) == null);

            Trip trip = new Trip(1, 1, 1, day, day.plusDays(5));
            TodoList todos = trip.getTodoList();
            TodoMenu.start(todos, input("3", "4", "bad", "2", "", "passport", "bad", "2026-12-01", "2026-12-03",
                    "3", "x", "999", "1", "1", "", "3", "1", "2", "없음", "", "8", "bad", "2026-12-02", "9", "0"));
            check(todos.getTodos().size() == 1);
            check(todos.findTodo(1).getDescription().equals("passport"));
            check(todos.findTodo(1).getStartDate() == null);
            check(todos.findTodo(1).getEndDate().equals(day.plusDays(2)));
            check(todos.getTodos(day.plusDays(1)).size() == 1);
            todos.addTodo(false, null, null, "undated");
            todos.addTodo(false, day, day, "dated");
            todos.sortByDate();
            check(todos.getTodos().get(0).getDescription().equals("dated"));
            check(todos.getTodos(day.plusDays(10)).isEmpty());
            rejected(() -> todos.findTodo(1).setDate(day.plusDays(1), day));
            check(todos.findTodo(1).getStartDate() == null);
            TodoMenu.start(todos, input("2", "invalid period", "2026-12-03", "2026-12-01", "0"));
            check(todos.getTodos().size() == 3);

            HotelList hotels = trip.getHotelList();
            HotelMenu.start(hotels, input("3", "4", "2", "", "TEST HOTEL", "Japan", "Tokyo", "2026-12-01", "2026-12-03",
                    "3", "1", "1", "", "3", "1", "4", "없음", "8", "2026-12-02", "9", "0"));
            check(hotels.findHotel(1).getName().equals("TEST HOTEL"));
            check(hotels.findHotel(1).getCheckInDate() == null);
            check(hotels.getHotels(day.plusDays(1)).size() == 1);
            rejected(() -> hotels.updateHotel(1, "Changed", "Japan", "Tokyo", day.plusDays(3), day));
            check(hotels.findHotel(1).getName().equals("TEST HOTEL"));
            HotelMenu.start(hotels, input("3", "1", "3", "Osaka", "0"));
            check(hotels.findHotel(1).getCity().equals("Osaka"));
            hotels.addHotel("Earlier", "Japan", "Tokyo", day, day);
            hotels.sortByDate();
            check(hotels.getHotels().get(0).getName().equals("Earlier"));

            FlightList flights = trip.getFlightList();
            FlightMenu.start(flights, input("3", "4", "2", "KE1", "ICN", "NRT", "2026-12-01 09:00", "Asia/Seoul",
                    "2026-12-01 11:00", "Asia/Tokyo", "3", "1", "1", "", "3", "1", "4", "없음", "8", "2026-12-01", "9", "0"));
            check(flights.findFlight(1).getFlightNumber().equals("KE1"));
            check(flights.findFlight(1).getDepartureTime() == null);
            check(flights.getFlights(day).size() == 1);
            flights.addFlight("KE2", "ICN", "LAX", departure, arrival);
            flights.sortByDate();
            check(flights.getFlights().get(0).getFlightNumber().equals("KE2"));

            ScheduleList schedules = trip.getScheduleList();
            ScheduleMenu.start(schedules, input("3", "4", "2", "walk", "2026-12-01", "2026-12-03",
                    "3", "1", "1", "", "3", "1", "2", "", "없음", "8", "2026-12-02", "9", "0"));
            check(schedules.findSchedule(1).getDescription().equals("walk"));
            check(schedules.findSchedule(1).getStartDate().equals(day));
            check(schedules.findSchedule(1).getEndDate() == null);
            check(schedules.getSchedules(day.plusDays(1)).size() == 1);
            schedules.addSchedule(null, null, "undated");
            schedules.sortByDate();
            check(schedules.getSchedules().get(0).getDescription().equals("walk"));
            Trip other = new Trip(2, 1, 1, day, day);
            check(other.getFlightList().getFlights().isEmpty() && other.getHotelList().getHotels().isEmpty()
                    && other.getTodoList().getTodos().isEmpty() && other.getScheduleList().getSchedules().isEmpty());
            TripDetailMenu.start(trip, null, input("invalid", "0"));
            check(output.toString().contains("잘못된 메뉴 번호입니다."));
            check(output.toString().contains("해당 ID"));
            check(output.toString().contains("TEST HOTEL"));
        } finally {
            System.setOut(original);
        }
        System.out.println("PASS: " + checks + " checks");
    }
}
