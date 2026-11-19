package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class ScheduleMenu {
    public static void show(List<Schedule> items) {
        System.out.println("\n[일정]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Schedule item : items) {
            String startDateText = "미정";
            if (item.getStartDate() != null) {
                startDateText = item.getStartDate().toString();
            }
            String endDateText = "미정";
            if (item.getEndDate() != null) {
                endDateText = item.getEndDate().toString();
            }
            System.out.println(item.getId() + " | " + item.getDescription()
                    + " | " + startDateText + " ~ " + endDateText);
        }
    }

    public static void start(ScheduleList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 일정 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제");
            System.out.println("5. 날짜별 조회  6. 날짜순 정렬  0. 돌아가기");
            try {
                System.out.print("선택 : ");
                switch (scan.nextLine().trim()) {
                    case "1" -> show(list.getSchedules());
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

    private static void addItem(ScheduleList list, Scanner scan) {
        String description;
        while (true) {
            System.out.print("내용 : ");
            description = scan.nextLine().trim();
            if (!description.isBlank()) {
                break;
            }
            System.out.println("필수 항목입니다.");
        }
        LocalDate start = editDate(scan, "시작일", null);
        LocalDate end = editDate(scan, "종료일", null);
        list.addSchedule(start, end, description);
    }

    private static void removeItem(ScheduleList list, Scanner scan) {
        show(list.getSchedules());
        if (list.getSchedules().isEmpty()) return;
        Schedule item = selectItem(list, scan);
        list.removeSchedule(item.getId());
    }

    private static void showByDate(ScheduleList list, Scanner scan) {
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
        show(list.getSchedules(date));
    }

    private static void editDescription(ScheduleList list, Schedule item, Scanner scan) {
        System.out.print("내용 (빈칸: 유지) : ");
        String description = scan.nextLine().trim();
        if (!description.isEmpty()) {
            list.setDescription(item.getId(), description);
        }
    }

    private static void showSorted(ScheduleList list) {
        list.sortByDate();
        show(list.getSchedules());
    }

    private static void editMenu(ScheduleList list, Scanner scan) {
        show(list.getSchedules());
        if (list.getSchedules().isEmpty()) return;
        Schedule item = selectItem(list, scan);
        System.out.print("수정 항목 (1: 내용, 2: 기간) : ");
        switch (scan.nextLine().trim()) {
            case "1" -> editDescription(list, item, scan);
            case "2" -> list.setDate(item.getId(),
                    editDate(scan, "시작일", item.getStartDate()),
                    editDate(scan, "종료일", item.getEndDate()));
            default -> System.out.println("잘못된 메뉴 번호입니다.");
        }
    }

    private static Schedule selectItem(ScheduleList list, Scanner scan) {
        while (true) {
            try {
                System.out.print("ID : ");
                int id = Integer.parseInt(scan.nextLine().trim());
                return list.findSchedule(id);
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
