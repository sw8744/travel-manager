package kr.ac.konkuk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;

public class HotelList {
    private int nextId = 1;
    private final List<Hotel> hotelList = new ArrayList<>();

    void restore(Hotel hotel) {
        hotelList.add(hotel);
        nextId = Math.max(nextId, hotel.getId() + 1);
    }

    public void addHotel(String name, String country, String city, LocalDate checkInDate, LocalDate checkOutDate) {
        hotelList.add(new Hotel(nextId++, name, country, city, checkInDate, checkOutDate));
    }

    public List<Hotel> getHotels() {
        return List.copyOf(hotelList);
    }

    public void removeHotel(int id) {
        hotelList.remove(findHotel(id));
    }

    public void updateHotel(int id, String name, String country, String city, LocalDate checkInDate, LocalDate checkOutDate) {
        findHotel(id).update(name, country, city, checkInDate, checkOutDate);
    }

    public Hotel findHotel(int id) {
        for (Hotel hotel : hotelList) {
            if (hotel.getId() == id) {
                return hotel;
            }
        }
        throw new IllegalArgumentException("해당 ID가 존재하지 않습니다.");
    }

    public List<Hotel> getHotels(LocalDate date) {
        Objects.requireNonNull(date, "조회 날짜는 필수입니다.");
        List<Hotel> result = new ArrayList<>();
        for (Hotel item : hotelList) {
            if ((item.getCheckInDate() != null || item.getCheckOutDate() != null)
                    && (item.getCheckInDate() == null || !date.isBefore(item.getCheckInDate()))
                    && (item.getCheckOutDate() == null || !date.isAfter(item.getCheckOutDate()))) {
                result.add(item);
            }
        }
        return List.copyOf(result);
    }

    public void sortByDate() {
        hotelList.sort(Comparator.comparing((Hotel item) -> item.getCheckInDate(),
                Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
