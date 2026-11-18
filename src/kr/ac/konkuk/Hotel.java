package kr.ac.konkuk;

import java.time.LocalDate;

public class Hotel {
    private final int id;
    private String name;
    private String country;
    private String city;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public Hotel(int id, String name, String country, String city, LocalDate checkInDate, LocalDate checkOutDate) {
        this.id = id;
        update(name, country, city, checkInDate, checkOutDate);
    }

    public void update(String name, String country, String city, LocalDate checkInDate, LocalDate checkOutDate) {
        if (name == null || name.isBlank() || country == null || country.isBlank()
                || city == null || city.isBlank()) {
            throw new IllegalArgumentException("숙소 이름·국가·도시는 필수입니다.");
        }
        if (checkInDate != null && checkOutDate != null && checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("체크아웃은 체크인보다 빠를 수 없습니다.");
        }
        this.name = name;
        this.country = country;
        this.city = city;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
}
