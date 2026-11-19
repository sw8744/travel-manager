package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class ScheduleMenu {
    public static void show(List<Schedule> items) {
        System.out.println("\n[일정]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Schedule item : items) {
            System.out.println(item.getId() + " | " + item.getDescription()
                    + " | " + MenuInput.display(item.getStartDate()) + " ~ " + MenuInput.display(item.getEndDate()));
        }
    }

    public static void start(ScheduleList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 일정 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제");
            System.out.println("5. 날짜별 조회  6. 날짜순 정렬  0. 돌아가기");
            try {
                switch (MenuInput.text(scan, "선택")) {
                    case "1" -> show(list.getSchedules());
                    case "2" -> {
                        String description = MenuInput.required(scan, "내용");
                        LocalDate start = MenuInput.date(scan, "시작일");
                        LocalDate end = MenuInput.date(scan, "종료일");
                        list.addSchedule(start, end, description);
                    }
                    case "3" -> {
                        show(list.getSchedules());
                        if (list.getSchedules().isEmpty()) break;
                        Schedule item = MenuInput.retry(() -> list.findSchedule(MenuInput.id(scan)));
                        switch (MenuInput.text(scan, "수정 항목 (1: 내용, 2: 기간)")) {
                            case "1" -> list.setDescription(item.getId(), MenuInput.editText(scan, "내용", item.getDescription()));
                            case "2" -> list.setDate(item.getId(),
                                    MenuInput.editDate(scan, "시작일", item.getStartDate()),
                                    MenuInput.editDate(scan, "종료일", item.getEndDate()));
                            default -> System.out.println("잘못된 메뉴 번호입니다.");
                        }
                    }
                    case "4" -> {
                        show(list.getSchedules());
                        if (list.getSchedules().isEmpty()) break;
                        Schedule item = MenuInput.retry(() -> list.findSchedule(MenuInput.id(scan)));
                        list.removeSchedule(item.getId());
                    }
                    case "5" -> {
                        LocalDate date = MenuInput.retry(() -> LocalDate.parse(MenuInput.required(scan, "조회 날짜 (YYYY-MM-DD)")));
                        show(list.getSchedules(date));
                    }
                    case "6" -> { list.sortByDate(); show(list.getSchedules()); }
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 메뉴 번호입니다.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }
}
