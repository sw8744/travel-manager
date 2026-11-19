package kr.ac.konkuk;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.function.Supplier;

class MenuInput {
    static String text(Scanner scan, String label) {
        System.out.print(label + " : ");
        return scan.nextLine().trim();
    }

    static String required(Scanner scan, String label) {
        while (true) {
            String value = text(scan, label);
            if (!value.isBlank()) return value;
            System.out.println("필수 항목입니다.");
        }
    }

    static String editText(Scanner scan, String label, String old) {
        String value = text(scan, label + " [" + old + ", 빈칸: 유지]");
        return value.isEmpty() ? old : value;
    }

    static <T> T retry(Supplier<T> action) {
        while (true) {
            try {
                return action.get();
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("입력 오류: " + e.getMessage());
            }
        }
    }

    static int id(Scanner scan) {
        return retry(() -> Integer.parseInt(text(scan, "ID")));
    }

    static LocalDate date(Scanner scan, String label) {
        return readDate(scan, label, null, false);
    }

    static LocalDate editDate(Scanner scan, String label, LocalDate old) {
        return readDate(scan, label, old, true);
    }

    private static LocalDate readDate(Scanner scan, String label, LocalDate old, boolean edit) {
        return retry(() -> {
            String value = text(scan, label + " (YYYY-MM-DD, 빈칸: "
                    + (edit ? "유지, 없음: 제거" : "미정") + ")");
            if (value.isEmpty()) return edit ? old : null;
            if (value.equals("없음")) return null;
            return LocalDate.parse(value);
        });
    }

    static ZonedDateTime time(Scanner scan, String label, ZonedDateTime old, boolean edit) {
        return retry(() -> {
            String value = text(scan, label + " (YYYY-MM-DD HH:mm, 빈칸: "
                    + (edit ? "유지, 없음: 제거" : "미정") + ")");
            if (value.isEmpty()) return edit ? old : null;
            if (value.equals("없음")) return null;
            LocalDateTime local = LocalDateTime.parse(value.replace(' ', 'T'));
            ZoneOffset offset = ZoneOffset.of(required(scan, "UTC 시차 (예: +09:00, -08:00)"));
            return ZonedDateTime.of(local, offset);
        });
    }

    static String display(Object value) {
        if (value == null) return "미정";
        if (value instanceof ZonedDateTime time)
            return time.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm XXX '['VV']'"));
        if (value instanceof LocalDateTime time)
            return time.format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm"));
        return value.toString();
    }
}
