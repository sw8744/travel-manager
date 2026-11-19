package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class TodoMenu {
    public static void show(List<Todo> items) {
        System.out.println("\n[할 일]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Todo item : items) {
            String startDateText = "미정";
            if (item.getStartDate() != null) {
                startDateText = item.getStartDate().toString();
            }
            String endDateText = "미정";
            if (item.getEndDate() != null) {
                endDateText = item.getEndDate().toString();
            }
            String status = "[ ] ";
            if (item.isDone()) {
                status = "[x] ";
            }
            System.out.println(item.getId() + " | " + status + item.getDescription()
                    + " | " + startDateText + " ~ " + endDateText);
        }
    }

    public static void start(TodoList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 할 일 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제");
            System.out.println("5. 완료 상태 변경  6. 완료 조회  7. 미완료 조회");
            System.out.println("8. 날짜별 조회  9. 날짜순 정렬  0. 돌아가기");
            try {
                System.out.print("선택 : ");
                switch (scan.nextLine().trim()) {
                    case "1" -> show(list.getTodos());
                    case "2" -> addItem(list, scan);
                    case "3" -> editMenu(list, scan);
                    case "4" -> removeItem(list, scan);
                    case "5" -> changeDone(list, scan);
                    case "6" -> show(list.getTodos(true));
                    case "7" -> show(list.getTodos(false));
                    case "8" -> showByDate(list, scan);
                    case "9" -> showSorted(list);
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 메뉴 번호입니다.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }

    private static void addItem(TodoList list, Scanner scan) {
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
        list.addTodo(false, start, end, description);
    }

    private static void removeItem(TodoList list, Scanner scan) {
        show(list.getTodos());
        if (list.getTodos().isEmpty()) return;
        Todo item = selectItem(list, scan);
        list.removeTodo(item.getId());
    }

    private static void changeDone(TodoList list, Scanner scan) {
        if (list.getTodos().isEmpty()) { show(list.getTodos()); return; }
        Todo item = selectItem(list, scan);
        while (true) {
            System.out.print("완료 여부 (true/false) : ");
            String value = scan.nextLine().trim();
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                list.setDone(item.getId(), Boolean.parseBoolean(value));
                return;
            }
            System.out.println("true 또는 false를 입력하세요.");
        }
    }

    private static void showByDate(TodoList list, Scanner scan) {
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
        show(list.getTodos(date));
    }

    private static void editDescription(TodoList list, Todo item, Scanner scan) {
        System.out.print("내용 (빈칸: 유지) : ");
        String description = scan.nextLine().trim();
        if (!description.isEmpty()) {
            list.setDescription(item.getId(), description);
        }
    }

    private static void showSorted(TodoList list) {
        list.sortByDate();
        show(list.getTodos());
    }

    private static void editMenu(TodoList list, Scanner scan) {
        show(list.getTodos());
        if (list.getTodos().isEmpty()) return;
        Todo item = selectItem(list, scan);
        System.out.print("수정 항목 (1: 내용, 2: 기간) : ");
        switch (scan.nextLine().trim()) {
            case "1" -> editDescription(list, item, scan);
            case "2" -> list.setDate(item.getId(),
                    editDate(scan, "시작일", item.getStartDate()),
                    editDate(scan, "종료일", item.getEndDate()));
            default -> System.out.println("잘못된 메뉴 번호입니다.");
        }
    }

    private static Todo selectItem(TodoList list, Scanner scan) {
        while (true) {
            try {
                System.out.print("ID : ");
                int id = Integer.parseInt(scan.nextLine().trim());
                return list.findTodo(id);
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
