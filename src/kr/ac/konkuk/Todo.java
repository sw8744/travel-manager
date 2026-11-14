package kr.ac.konkuk;

import java.time.LocalDate;

public class Todo {
    private int id;
    private Boolean isDone;
    private LocalDate startDate, endDate;
    private String description;

    public Todo(int id, Boolean isDone, LocalDate startDate, LocalDate endDate, String description) {
        this.id = id;
        this.isDone = isDone;
        setDate(startDate, endDate);
        this.description = description;
    }

    public Todo(int id, LocalDate startDate, LocalDate endDate, String description) {
        this(id, false, startDate, endDate, description);
    }

    public Todo(int id, LocalDate startDate, LocalDate endDate) {
        this(id, false, startDate, endDate, "");
    }

    public void setDate(LocalDate start, LocalDate end){
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("시작일아 종료일보다 늦습니다.");
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

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }
}
