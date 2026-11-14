package kr.ac.konkuk;

import java.time.LocalDate;

public class Todo {
    private final int id;
    private boolean done;
    private LocalDate startDate, endDate;
    private String description;

    public Todo(int id, boolean done, LocalDate startDate, LocalDate endDate, String description) {
        this.id = id;
        this.done = done;
        setDate(startDate, endDate);
        this.description = description;
    }

    public Todo(int id, LocalDate startDate, LocalDate endDate, String description) {
        this(id, false, startDate, endDate, description);
    }

    public Todo(int id, LocalDate startDate, LocalDate endDate) {
        this(id, false, startDate, endDate, "");
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
