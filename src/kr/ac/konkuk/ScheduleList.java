package kr.ac.konkuk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ScheduleList {
    private int nextId = 1;
    private final List<Schedule> scheduleList = new ArrayList<>();

    private Schedule findSchedule(int id) {
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

    public void setDate(int id, LocalDate startDate, LocalDate endDate) {
        Schedule s = findSchedule(id);
        s.setDate(startDate, endDate);
    }

    public void setDescription(int id, String description) {
        Schedule s = findSchedule(id);
        s.setDescription(description);
    }
}
