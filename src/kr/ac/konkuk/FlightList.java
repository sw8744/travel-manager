package kr.ac.konkuk;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;
import java.time.LocalDate;

public class FlightList {
    private int nextId = 1;
    private final List<Flight> flightList = new ArrayList<>();

    public void addFlight(String flightNumber, String departure, String arrival, ZonedDateTime departureTime, ZonedDateTime arrivalTime) {
        flightList.add(new Flight(nextId++, flightNumber, departure, arrival, departureTime, arrivalTime));
    }

    public List<Flight> getFlights() {
        return List.copyOf(flightList);
    }

    public void removeFlight(int id) {
        flightList.remove(findFlight(id));
    }

    public void updateFlight(int id, String flightNumber, String departure, String arrival, ZonedDateTime departureTime, ZonedDateTime arrivalTime) {
        findFlight(id).update(flightNumber, departure, arrival, departureTime, arrivalTime);
    }

    public Flight findFlight(int id) {
        for (Flight flight : flightList) {
            if (flight.getId() == id) {
                return flight;
            }
        }
        throw new IllegalArgumentException("해당 ID가 존재하지 않습니다.");
    }

    public List<Flight> getFlights(LocalDate date) {
        Objects.requireNonNull(date, "조회 날짜는 필수입니다.");
        List<Flight> result = new ArrayList<>();
        for (Flight item : flightList) {
            if ((item.getDepartureTime() != null && item.getDepartureTime().toLocalDate().equals(date))
                    || (item.getArrivalTime() != null && item.getArrivalTime().toLocalDate().equals(date))) {
                result.add(item);
            }
        }
        return List.copyOf(result);
    }

    public void sortByDate() {
        flightList.sort(Comparator.comparing((Flight item) -> {
            if (item.getDepartureTime() == null) {
                return null;
            }
            return item.getDepartureTime().toInstant();
        },
                Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
