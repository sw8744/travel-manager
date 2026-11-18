package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class HotelMenu {
    public static void show(List<Hotel> items) {
        System.out.println("\n[숙박]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Hotel item : items) {
            System.out.println(item.getId() + " | " + MenuInput.display(item.getName())
                    + " | " + MenuInput.display(item.getCountry())
                    + " | " + MenuInput.display(item.getCity())
                    + " | " + MenuInput.display(item.getCheckInDate())
                    + " | " + MenuInput.display(item.getCheckOutDate()));
        }
    }

    public static void start(HotelList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 숙박 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제  0. 돌아가기");
            System.out.println("8. 날짜별 조회  9. 날짜순 정렬");
            try {
                switch (MenuInput.text(scan, "선택")) {
                    case "1" -> show(list.getHotels());
                    case "2" -> {
                        String name = MenuInput.required(scan, "숙소 이름");
                        String country = MenuInput.required(scan, "국가");
                        String city = MenuInput.required(scan, "도시");
                        LocalDate checkInDate = MenuInput.date(scan, "체크인");
                        LocalDate checkOutDate = MenuInput.date(scan, "체크아웃");
                        list.addHotel(name, country, city, checkInDate, checkOutDate);
                    }
                    case "3" -> {
                        show(list.getHotels());
                        if (list.getHotels().isEmpty()) break;
                        Hotel item = MenuInput.retry(() -> list.findHotel(MenuInput.id(scan)));
                        String name = item.getName();
                        String country = item.getCountry();
                        String city = item.getCity();
                        LocalDate checkInDate = item.getCheckInDate();
                        LocalDate checkOutDate = item.getCheckOutDate();
                        System.out.println("1. 숙소 이름  2. 국가  3. 도시  4. 체크인  5. 체크아웃");
                        switch (MenuInput.text(scan, "수정 항목")) {
                            case "1" -> name = MenuInput.editText(scan, "숙소 이름", name);
                            case "2" -> country = MenuInput.editText(scan, "국가", country);
                            case "3" -> city = MenuInput.editText(scan, "도시", city);
                            case "4" -> checkInDate = MenuInput.editDate(scan, "체크인", checkInDate);
                            case "5" -> checkOutDate = MenuInput.editDate(scan, "체크아웃", checkOutDate);
                            default -> { System.out.println("잘못된 메뉴 번호입니다."); continue; }
                        }
                        list.updateHotel(item.getId(), name, country, city, checkInDate, checkOutDate);
                    }
                    case "4" -> {
                        show(list.getHotels());
                        if (list.getHotels().isEmpty()) break;
                        Hotel item = MenuInput.retry(() -> list.findHotel(MenuInput.id(scan)));
                        list.removeHotel(item.getId());
                    }
                    case "8" -> {
                        LocalDate date = MenuInput.retry(() -> LocalDate.parse(MenuInput.required(scan, "조회 날짜 (YYYY-MM-DD)")));
                        show(list.getHotels(date));
                    }
                    case "9" -> { list.sortByDate(); show(list.getHotels()); }
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 메뉴 번호입니다.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }
}
