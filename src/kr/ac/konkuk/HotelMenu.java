package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class HotelMenu {
    public static void show(List<Hotel> items) {
        System.out.println("\n[숙박]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Hotel item : items) {
            String checkInDateText = "미정";
            if (item.getCheckInDate() != null) {
                checkInDateText = item.getCheckInDate().toString();
            }
            String checkOutDateText = "미정";
            if (item.getCheckOutDate() != null) {
                checkOutDateText = item.getCheckOutDate().toString();
            }
            System.out.println(item.getId() + " | " + item.getName()
                    + " | " + item.getCountry()
                    + " | " + item.getCity()
                    + " | " + checkInDateText
                    + " | " + checkOutDateText);
        }
    }

    public static void start(HotelList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 숙박 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제");
            System.out.println("5. 날짜별 조회  6. 날짜순 정렬  0. 돌아가기");
            try {
                System.out.print("선택 : ");
                switch (scan.nextLine().trim()) {
                    case "1" -> show(list.getHotels());
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

    private static void addItem(HotelList list, Scanner scan) {
        String name;
        while (true) {
            System.out.print("숙소 이름 : ");
            name = scan.nextLine().trim();
            if (!name.isBlank()) {
                break;
            }
            System.out.println("필수 항목입니다.");
        }
        String country;
        while (true) {
            System.out.print("국가 : ");
            country = scan.nextLine().trim();
            if (!country.isBlank()) {
                break;
            }
            System.out.println("필수 항목입니다.");
        }
        String city;
        while (true) {
            System.out.print("도시 : ");
            city = scan.nextLine().trim();
            if (!city.isBlank()) {
                break;
            }
            System.out.println("필수 항목입니다.");
        }
        LocalDate checkInDate = editDate(scan, "체크인", null);
        LocalDate checkOutDate = editDate(scan, "체크아웃", null);
        list.addHotel(name, country, city, checkInDate, checkOutDate);
    }

    private static void removeItem(HotelList list, Scanner scan) {
        show(list.getHotels());
        if (list.getHotels().isEmpty()) return;
        Hotel item = selectItem(list, scan);
        list.removeHotel(item.getId());
    }

    private static void showByDate(HotelList list, Scanner scan) {
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
        show(list.getHotels(date));
    }

    private static String editName(Scanner scan, String old) {
        System.out.print("숙소 이름 (빈칸: 유지) : ");
        String value = scan.nextLine().trim();
        if (!value.isEmpty()) {
            return value;
        }
        return old;
    }

    private static String editCountry(Scanner scan, String old) {
        System.out.print("국가 (빈칸: 유지) : ");
        String value = scan.nextLine().trim();
        if (!value.isEmpty()) {
            return value;
        }
        return old;
    }

    private static String editCity(Scanner scan, String old) {
        System.out.print("도시 (빈칸: 유지) : ");
        String value = scan.nextLine().trim();
        if (!value.isEmpty()) {
            return value;
        }
        return old;
    }

    private static void showSorted(HotelList list) {
        list.sortByDate();
        show(list.getHotels());
    }

    private static void editMenu(HotelList list, Scanner scan) {
        show(list.getHotels());
        if (list.getHotels().isEmpty()) return;
        Hotel item = selectItem(list, scan);
        String name = item.getName();
        String country = item.getCountry();
        String city = item.getCity();
        LocalDate checkInDate = item.getCheckInDate();
        LocalDate checkOutDate = item.getCheckOutDate();
        System.out.println("1. 숙소 이름  2. 국가  3. 도시  4. 체크인  5. 체크아웃");
        System.out.print("수정 항목 : ");
        switch (scan.nextLine().trim()) {
            case "1" -> name = editName(scan, name);
            case "2" -> country = editCountry(scan, country);
            case "3" -> city = editCity(scan, city);
            case "4" -> checkInDate = editDate(scan, "체크인", checkInDate);
            case "5" -> checkOutDate = editDate(scan, "체크아웃", checkOutDate);
            default -> { System.out.println("잘못된 메뉴 번호입니다."); return; }
        }
        list.updateHotel(item.getId(), name, country, city, checkInDate, checkOutDate);
    }

    private static Hotel selectItem(HotelList list, Scanner scan) {
        while (true) {
            try {
                System.out.print("ID : ");
                int id = Integer.parseInt(scan.nextLine().trim());
                return list.findHotel(id);
            } catch (IllegalArgumentException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }

    private static LocalDate editDate(Scanner scan, String label, LocalDate old) {
        while (true) {
            try {
                System.out.print(label + " (YYYY-MM-DD, 빈칸: 유지/미정, 없음: 제거) : ");
                String value = scan.nextLine().trim();
                if (value.isEmpty()) {
                    return old;
                }
                if (value.equals("없음")) {
                    return null;
                }
                return LocalDate.parse(value);
            } catch (DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }

}
