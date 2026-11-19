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

    private static Scanner input(String... lines) {
        return new Scanner(String.join("\n", lines) + "\n");
    }

    public static void main(String[] args) {
        PrintStream original = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        try {
            LocalDate day = LocalDate.of(2026, 12, 1);
            ZonedDateTime departure = ZonedDateTime.of(2026, 12, 1, 17, 0, 0, 0, ZoneOffset.of("+09:00"));
            ZonedDateTime arrival = ZonedDateTime.of(2026, 12, 1, 10, 0, 0, 0, ZoneOffset.of("-08:00"));
            Flight flight = new Flight(1, "KE1", "ICN", "LAX", departure, arrival);
            check(flight.getArrivalTime().toInstant().isAfter(departure.toInstant()));
            try {
                flight.update("KE2", "ICN", "LAX", arrival, departure);
                throw new AssertionError("Expected rejection");
            } catch (IllegalArgumentException e) {
                checks++;
            }
            check(flight.getFlightNumber().equals("KE1"));
            try {
                new Flight(2, "", "ICN", "LAX", null, null);
                throw new AssertionError("Expected rejection");
            } catch (IllegalArgumentException e) {
                checks++;
            }
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
            try {
                todos.findTodo(1).setDate(day.plusDays(1), day);
                throw new AssertionError("Expected rejection");
            } catch (IllegalArgumentException e) {
                checks++;
            }
            check(todos.findTodo(1).getStartDate() == null);
            TodoMenu.start(todos, input("2", "invalid period", "2026-12-03", "2026-12-01", "0"));
            check(todos.getTodos().size() == 3);

            HotelList hotels = trip.getHotelList();
            HotelMenu.start(hotels, input("3", "4", "2", "", "TEST HOTEL", "Japan", "Tokyo", "2026-12-01", "2026-12-03",
                    "3", "1", "1", "", "3", "1", "4", "없음", "5", "2026-12-02", "6", "0"));
            check(hotels.findHotel(1).getName().equals("TEST HOTEL"));
            check(hotels.findHotel(1).getCheckInDate() == null);
            check(hotels.getHotels(day.plusDays(1)).size() == 1);
            try {
                hotels.updateHotel(1, "Changed", "Japan", "Tokyo", day.plusDays(3), day);
                throw new AssertionError("Expected rejection");
            } catch (IllegalArgumentException e) {
                checks++;
            }
            check(hotels.findHotel(1).getName().equals("TEST HOTEL"));
            HotelMenu.start(hotels, input("3", "1", "3", "Osaka", "0"));
            check(hotels.findHotel(1).getCity().equals("Osaka"));
            hotels.addHotel("Earlier", "Japan", "Tokyo", day, day);
            hotels.sortByDate();
            check(hotels.getHotels().get(0).getName().equals("Earlier"));

            FlightList flights = trip.getFlightList();
            FlightMenu.start(flights, input("3", "4", "2", "KE1", "ICN", "NRT", "bad time", "2026-12-01 09:00", "bad offset", "2026-12-01 09:00", "+09:00",
                    "2026-12-01 11:00", "+09:00", "3", "1", "1", "", "3", "1", "4", "없음", "5", "2026-12-01", "6", "0"));
            check(flights.findFlight(1).getFlightNumber().equals("KE1"));
            check(flights.findFlight(1).getDepartureTime() == null);
            check(flights.getFlights(day).size() == 1);
            flights.addFlight("KE2", "ICN", "LAX", departure, arrival);
            flights.sortByDate();
            check(flights.getFlights().get(0).getFlightNumber().equals("KE2"));

            ScheduleList schedules = trip.getScheduleList();
            ScheduleMenu.start(schedules, input("3", "4", "2", "walk", "2026-12-01", "2026-12-03",
                    "3", "1", "1", "", "3", "1", "2", "", "없음", "5", "2026-12-02", "6", "0"));
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
            TodoMenu.start(todos, input("5", "1", "true", "6", "7", "1", "0"));
            check(todos.findTodo(1).isDone());
            TripDetailMenu.start(trip, null, input("1", "0", "2", "0", "3", "0", "4", "0", "invalid", "0"));
            check(output.toString().contains("잘못된 메뉴 번호입니다."));
            check(output.toString().contains("해당 ID"));
            check(output.toString().contains("TEST HOTEL"));
            check(output.toString().contains("NRT"));
            FlightMenu.start(flights, input("3", "1", "5", "", "0"));
            check(flights.findFlight(1).getArrivalTime().getHour() == 11);
            FlightMenu.start(flights, input("2", "FIXED", "ICN", "LAX", "2026-03-08 02:30", "-08:00", "", "0"));
            Flight fixed = flights.getFlights().get(flights.getFlights().size() - 1);
            check(fixed.getDepartureTime().getHour() == 2);
            check(fixed.getDepartureTime().getOffset().equals(ZoneOffset.of("-08:00")));
        } finally {
            System.setOut(original);
        }
        System.out.println("PASS: " + checks + " checks");
    }
}
