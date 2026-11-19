
package kr.ac.konkuk;

import java.time.LocalDate;

public class Trip {
    private final int tripId;
    private final int userId;
    private final int cityId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final TodoList todoList;
    private final FlightList flightList;
    private final HotelList hotelList;
    private final ScheduleList scheduleList;

    public Trip(int tripId, int userId, int cityId, LocalDate startDate, LocalDate endDate) {

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("여행 날짜가 올바르지 않습니다.");
        }

        this.tripId = tripId;
        this.userId = userId;
        this.cityId = cityId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.todoList = new TodoList();
        this.flightList = new FlightList();
        this.hotelList = new HotelList();
        this.scheduleList = new ScheduleList();
    }

    public int getTripId() {
        return tripId;
    }

    public int getUserId() {
        return userId;
    }

    public int getCityId() {
        return cityId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public TodoList getTodoList() {
        return todoList;
    }
    public FlightList getFlightList() {
        return flightList;
    }

    public HotelList getHotelList() {
        return hotelList;
    }

    public ScheduleList getScheduleList() {
        return scheduleList;
    }
}
