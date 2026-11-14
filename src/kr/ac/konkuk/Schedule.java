package kr.ac.konkuk;

import java.time.LocalDate;

public class Schedule {
    private final int id;
    private LocalDate startDate, endDate;
    private String description;

    public Schedule(int id, LocalDate startDate, LocalDate endDate, String description) {
        this.id = id;
        setDate(startDate, endDate);
        this.description = description;
    }

    public Schedule(int id, LocalDate startDate, LocalDate endDate) {
        this(id, startDate, endDate, "");
    }

    public void setDate(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦습니다.");
        }
        this.startDate = start;
        this.endDate = end;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }
}
