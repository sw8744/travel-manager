
package kr.ac.konkuk;

import java.sql.*;
import java.util.*;

public class TravelCautionManager {
    private Map<Integer, Country> countries = new HashMap<>();
    private Map<Integer, City> cities = new HashMap<>();

    public TravelCautionManager() throws SQLException {
        try (Connection conn = Database.openConnection()) {
            loadCountries(conn);
            loadCities(conn);
        }
    }

    private void loadCountries(Connection conn) throws SQLException {
        String sql = "SELECT id, name, electricity_note, safety_note, " +
                "food_note, climate_note, transport_note, travel_alert_note " +
                "FROM countries";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Country country = new Country(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("electricity_note"),
                        rs.getString("safety_note"),
                        rs.getString("food_note"),
                        rs.getString("climate_note"),
                        rs.getString("transport_note"),
                        rs.getString("travel_alert_note")
                );

                countries.put(country.getId(), country);
            }
        }
    }

    private void loadCities(Connection conn) throws SQLException {
        String sql = "SELECT id, name, country, electricity_note, " +
                "safety_note, food_note, climate_note, transport_note, " +
                "travel_alert_note FROM cities";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                City city = new City(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("country"),
                        rs.getString("electricity_note"),
                        rs.getString("safety_note"),
                        rs.getString("food_note"),
                        rs.getString("climate_note"),
                        rs.getString("transport_note"),
                        rs.getString("travel_alert_note")
                );

                cities.put(city.getId(), city);
            }
        }
    }

    public List<City> searchCities(String keyword) {
        List<City> results = new ArrayList<>();

        for (City city : cities.values()) {
            if (city.getName().contains(keyword)) {
                results.add(city);
            }
        }

        results.sort(new CityIdComparator());
        return results;
    }

    private static class CityIdComparator implements Comparator<City> {
        @Override
        public int compare(City first, City second) {
            return Integer.compare(first.getId(), second.getId());
        }
    }

    public void showCautions(int cityId) {
        City city = cities.get(cityId);

        if (city == null) {
            System.out.println("해당 도시가 없습니다.");
            return;
        }

        Country country = countries.get(city.getCountryId());

        if (country == null) {
            System.out.println("국가 정보를 찾을 수 없습니다.");
            return;
        }

        country.showCautions(city);
    }
}
