package kr.ac.konkuk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;

public class ScheduleList {
    private int nextId = 1;
    private final List<Schedule> scheduleList = new ArrayList<>();

    void restore(Schedule schedule) {
        scheduleList.add(schedule);
        nextId = Math.max(nextId, schedule.getId() + 1);
    }

    public Schedule findSchedule(int id) {
        for (Schedule schedule : scheduleList) {
            if (schedule.getId() == id) {
                return schedule;
            }
        }
        throw new IllegalArgumentException("해당 ID의 일정이 존재하지 않습니다.");
    }

    public void addSchedule(LocalDate startDate, LocalDate endDate, String description) {
        scheduleList.add(new Schedule(nextId++, startDate, endDate, description));
    }

    public void removeSchedule(int id) {
        scheduleList.remove(findSchedule(id));
    }

    public List<Schedule> getSchedules() {
        return List.copyOf(scheduleList);
    }

    public void setDate(int id, LocalDate startDate, LocalDate endDate) {
        Schedule s = findSchedule(id);
        s.setDate(startDate, endDate);
    }

    public void setDescription(int id, String description) {
        Schedule s = findSchedule(id);
        s.setDescription(description);
    }

    public List<Schedule> getSchedules(LocalDate date) {
        Objects.requireNonNull(date, "조회 날짜는 필수입니다.");
        List<Schedule> result = new ArrayList<>();
        for (Schedule item : scheduleList) {
            if ((item.getStartDate() != null || item.getEndDate() != null)
                    && (item.getStartDate() == null || !date.isBefore(item.getStartDate()))
                    && (item.getEndDate() == null || !date.isAfter(item.getEndDate()))) {
                result.add(item);
            }
        }
        return List.copyOf(result);
    }

    public void sortByDate() {
        scheduleList.sort(Comparator.comparing((Schedule item) -> item.getStartDate(),
                Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
