package kr.ac.konkuk;

import java.time.ZonedDateTime;

public class Flight {
    private final int id;
    private String flightNumber;
    private String departure;
    private String arrival;
    private ZonedDateTime departureTime;
    private ZonedDateTime arrivalTime;

    public Flight(int id, String flightNumber, String departure, String arrival, ZonedDateTime departureTime, ZonedDateTime arrivalTime) {
        this.id = id;
        update(flightNumber, departure, arrival, departureTime, arrivalTime);
    }

    public void update(String flightNumber, String departure, String arrival, ZonedDateTime departureTime, ZonedDateTime arrivalTime) {
        if (flightNumber == null || flightNumber.isBlank() || departure == null || departure.isBlank()
                || arrival == null || arrival.isBlank()) {
            throw new IllegalArgumentException("편명·출발지·도착지는 필수입니다.");
        }
        if (departureTime != null && arrivalTime != null
                && arrivalTime.toInstant().isBefore(departureTime.toInstant())) {
            throw new IllegalArgumentException("도착 시각이 출발 시각보다 빠릅니다.");
        }
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.arrival = arrival;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getDeparture() {
        return departure;
    }

    public String getArrival() {
        return arrival;
    }

    public ZonedDateTime getDepartureTime() {
        return departureTime;
    }

    public ZonedDateTime getArrivalTime() {
        return arrivalTime;
    }
}
