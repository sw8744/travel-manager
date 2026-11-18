package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class FlightMenu {
    public static void show(List<Flight> items) {
        System.out.println("\n[항공]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Flight item : items) {
            System.out.println(item.getId() + " | " + MenuInput.display(item.getFlightNumber())
                    + " | " + MenuInput.display(item.getDeparture())
                    + " | " + MenuInput.display(item.getArrival())
                    + " | " + MenuInput.display(item.getDepartureTime())
                    + " | " + MenuInput.display(item.getArrivalTime()));
        }
    }

    public static void start(FlightList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 항공 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제  0. 돌아가기");
            System.out.println("8. 날짜별 조회  9. 날짜순 정렬");
            try {
                switch (MenuInput.text(scan, "선택")) {
                    case "1" -> show(list.getFlights());
                    case "2" -> {
                        String flightNumber = MenuInput.required(scan, "편명");
                        String departure = MenuInput.required(scan, "출발지");
                        String arrival = MenuInput.required(scan, "도착지");
                        ZonedDateTime departureTime = MenuInput.time(scan, "출발 시각", null, false);
                        ZonedDateTime arrivalTime = MenuInput.time(scan, "도착 시각", null, false);
                        list.addFlight(flightNumber, departure, arrival, departureTime, arrivalTime);
                    }
                    case "3" -> {
                        show(list.getFlights());
                        if (list.getFlights().isEmpty()) break;
                        Flight item = MenuInput.retry(() -> list.findFlight(MenuInput.id(scan)));
                        String flightNumber = item.getFlightNumber();
                        String departure = item.getDeparture();
                        String arrival = item.getArrival();
                        ZonedDateTime departureTime = item.getDepartureTime();
                        ZonedDateTime arrivalTime = item.getArrivalTime();
                        System.out.println("1. 편명  2. 출발지  3. 도착지  4. 출발 시각  5. 도착 시각");
                        switch (MenuInput.text(scan, "수정 항목")) {
                            case "1" -> flightNumber = MenuInput.editText(scan, "편명", flightNumber);
                            case "2" -> departure = MenuInput.editText(scan, "출발지", departure);
                            case "3" -> arrival = MenuInput.editText(scan, "도착지", arrival);
                            case "4" -> departureTime = MenuInput.time(scan, "출발 시각", departureTime, true);
                            case "5" -> arrivalTime = MenuInput.time(scan, "도착 시각", arrivalTime, true);
                            default -> { System.out.println("잘못된 메뉴 번호입니다."); continue; }
                        }
                        list.updateFlight(item.getId(), flightNumber, departure, arrival, departureTime, arrivalTime);
                    }
                    case "4" -> {
                        show(list.getFlights());
                        if (list.getFlights().isEmpty()) break;
                        Flight item = MenuInput.retry(() -> list.findFlight(MenuInput.id(scan)));
                        list.removeFlight(item.getId());
                    }
                    case "8" -> {
                        LocalDate date = MenuInput.retry(() -> LocalDate.parse(MenuInput.required(scan, "조회 날짜 (YYYY-MM-DD)")));
                        show(list.getFlights(date));
                    }
                    case "9" -> { list.sortByDate(); show(list.getFlights()); }
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 메뉴 번호입니다.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }
}
