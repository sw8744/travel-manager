package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class FlightMenu {
    public static void show(List<Flight> items) {
        System.out.println("\n[항공]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Flight item : items) {
            String departureTimeText = "미정";
            if (item.getDepartureTime() != null) {
                departureTimeText = item.getDepartureTime().format(java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm XXX"));
            }
            String arrivalTimeText = "미정";
            if (item.getArrivalTime() != null) {
                arrivalTimeText = item.getArrivalTime().format(java.time.format.DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm XXX"));
            }
            System.out.println(item.getId() + " | " + item.getFlightNumber()
                    + " | " + item.getDeparture()
                    + " | " + item.getArrival()
                    + " | " + departureTimeText
                    + " | " + arrivalTimeText);
        }
    }

    public static void start(FlightList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 항공 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제");
            System.out.println("5. 날짜별 조회  6. 날짜순 정렬  0. 돌아가기");
            try {
                System.out.print("선택 : ");
                switch (scan.nextLine().trim()) {
                    case "1" -> show(list.getFlights());
                    case "2" -> addItem(list, scan);
                    case "3" -> editMenu(list, scan);
                    case "4" -> removeItem(list, scan);
                    case "5" -> showByDate(list, scan);
                    case "6" -> showSorted(list);
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 메뉴 번호입니다.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }

    private static void addItem(FlightList list, Scanner scan) {
        String flightNumber;
        while (true) {
            System.out.print("편명 : ");
            flightNumber = scan.nextLine().trim();
            if (!flightNumber.isBlank()) {
                break;
            }
            System.out.println("필수 항목입니다.");
        }
        String departure;
        while (true) {
            System.out.print("출발지 : ");
            departure = scan.nextLine().trim();
            if (!departure.isBlank()) {
                break;
            }
            System.out.println("필수 항목입니다.");
        }
        String arrival;
        while (true) {
            System.out.print("도착지 : ");
            arrival = scan.nextLine().trim();
            if (!arrival.isBlank()) {
                break;
            }
            System.out.println("필수 항목입니다.");
        }
        ZonedDateTime departureTime = time(scan, "출발 시각", null);
        ZonedDateTime arrivalTime = time(scan, "도착 시각", null);
        list.addFlight(flightNumber, departure, arrival, departureTime, arrivalTime);
    }

    private static void removeItem(FlightList list, Scanner scan) {
        show(list.getFlights());
        if (list.getFlights().isEmpty()) return;
        Flight item = selectItem(list, scan);
        list.removeFlight(item.getId());
    }

    private static void showByDate(FlightList list, Scanner scan) {
        LocalDate date;
        while (true) {
            System.out.print("조회 날짜 (YYYY-MM-DD) : ");
            try {
                date = LocalDate.parse(scan.nextLine().trim());
                break;
            } catch (DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
        show(list.getFlights(date));
    }

    private static String editFlightNumber(Scanner scan, String old) {
        System.out.print("편명 (빈칸: 유지) : ");
        String value = scan.nextLine().trim();
        if (!value.isEmpty()) {
            return value;
        }
        return old;
    }

    private static String editDeparture(Scanner scan, String old) {
        System.out.print("출발지 (빈칸: 유지) : ");
        String value = scan.nextLine().trim();
        if (!value.isEmpty()) {
            return value;
        }
        return old;
    }

    private static String editArrival(Scanner scan, String old) {
        System.out.print("도착지 (빈칸: 유지) : ");
        String value = scan.nextLine().trim();
        if (!value.isEmpty()) {
            return value;
        }
        return old;
    }

    private static void showSorted(FlightList list) {
        list.sortByDate();
        show(list.getFlights());
    }

    private static void editMenu(FlightList list, Scanner scan) {
        show(list.getFlights());
        if (list.getFlights().isEmpty()) return;
        Flight item = selectItem(list, scan);
        String flightNumber = item.getFlightNumber();
        String departure = item.getDeparture();
        String arrival = item.getArrival();
        ZonedDateTime departureTime = item.getDepartureTime();
        ZonedDateTime arrivalTime = item.getArrivalTime();
        System.out.println("1. 편명  2. 출발지  3. 도착지  4. 출발 시각  5. 도착 시각");
        System.out.print("수정 항목 : ");
        switch (scan.nextLine().trim()) {
            case "1" -> flightNumber = editFlightNumber(scan, flightNumber);
            case "2" -> departure = editDeparture(scan, departure);
            case "3" -> arrival = editArrival(scan, arrival);
            case "4" -> departureTime = time(scan, "출발 시각", departureTime);
            case "5" -> arrivalTime = time(scan, "도착 시각", arrivalTime);
            default -> { System.out.println("잘못된 메뉴 번호입니다."); return; }
        }
        list.updateFlight(item.getId(), flightNumber, departure, arrival, departureTime, arrivalTime);
    }

    private static Flight selectItem(FlightList list, Scanner scan) {
        while (true) {
            try {
                System.out.print("ID : ");
                int id = Integer.parseInt(scan.nextLine().trim());
                return list.findFlight(id);
            } catch (IllegalArgumentException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }

    private static ZonedDateTime time(Scanner scan, String label, ZonedDateTime old) {
        while (true) {
            try {
                System.out.print(label + " (YYYY-MM-DD HH:mm, 빈칸: 유지/미정, 없음: 제거) : ");
                String value = scan.nextLine().trim();
                if (value.isEmpty()) {
                    return old;
                }
                if (value.equals("없음")) {
                    return null;
                }
                LocalDateTime local = LocalDateTime.parse(value.replace(' ', 'T'));
                System.out.print("UTC 시차 (예: +09:00, -08:00) : ");
                ZoneOffset offset = ZoneOffset.of(scan.nextLine().trim());
                return ZonedDateTime.of(local, offset);
            } catch (DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }

}
