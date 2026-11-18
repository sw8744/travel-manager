package kr.ac.konkuk;

import java.time.*;
import java.util.List;
import java.util.Scanner;

public class TodoMenu {
    public static void show(List<Todo> items) {
        System.out.println("\n[할 일]");
        if (items.isEmpty()) System.out.println("등록된 정보가 없습니다.");
        for (Todo item : items) {
            System.out.println(item.getId() + " | " + (item.isDone() ? "[x] " : "[ ] ") + item.getDescription()
                    + " | " + MenuInput.display(item.getStartDate()) + " ~ " + MenuInput.display(item.getEndDate()));
        }
    }

    public static void start(TodoList list, Scanner scan) {
        while (true) {
            System.out.println("\n===== 할 일 관리 =====");
            System.out.println("1. 전체 조회  2. 추가  3. 수정  4. 삭제  0. 돌아가기");
            System.out.println("5. 완료 상태 변경  6. 완료 조회  7. 미완료 조회");
            System.out.println("8. 날짜별 조회  9. 날짜순 정렬");
            try {
                switch (MenuInput.text(scan, "선택")) {
                    case "1" -> show(list.getTodos());
                    case "2" -> {
                        String description = MenuInput.required(scan, "내용");
                        LocalDate start = MenuInput.date(scan, "시작일");
                        LocalDate end = MenuInput.date(scan, "종료일");
                        list.addTodo(false, start, end, description);
                    }
                    case "3" -> {
                        show(list.getTodos());
                        if (list.getTodos().isEmpty()) break;
                        Todo item = MenuInput.retry(() -> list.findTodo(MenuInput.id(scan)));
                        switch (MenuInput.text(scan, "수정 항목 (1: 내용, 2: 기간)")) {
                            case "1" -> list.setDescription(item.getId(), MenuInput.editText(scan, "내용", item.getDescription()));
                            case "2" -> list.setDate(item.getId(),
                                    MenuInput.editDate(scan, "시작일", item.getStartDate()),
                                    MenuInput.editDate(scan, "종료일", item.getEndDate()));
                            default -> System.out.println("잘못된 메뉴 번호입니다.");
                        }
                    }
                    case "4" -> {
                        show(list.getTodos());
                        if (list.getTodos().isEmpty()) break;
                        Todo item = MenuInput.retry(() -> list.findTodo(MenuInput.id(scan)));
                        list.removeTodo(item.getId());
                    }
                    case "5" -> {
                        if (list.getTodos().isEmpty()) { show(list.getTodos()); break; }
                        Todo item = MenuInput.retry(() -> list.findTodo(MenuInput.id(scan)));
                        list.setDone(item.getId(), MenuInput.text(scan, "완료 여부 (true/false)").equals("true"));
                    }
                    case "6" -> show(list.getTodos(true));
                    case "7" -> show(list.getTodos(false));
                    case "8" -> {
                        LocalDate date = MenuInput.retry(() -> LocalDate.parse(MenuInput.required(scan, "조회 날짜 (YYYY-MM-DD)")));
                        show(list.getTodos(date));
                    }
                    case "9" -> { list.sortByDate(); show(list.getTodos()); }
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 메뉴 번호입니다.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }
}
