
package kr.ac.konkuk;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class TripMenu {
    private static int nextTripId = 1;

    public static void start(int userId, List<Trip> trips, Scanner scan) {
        TravelCautionManager manager;

        try {
            manager = new TravelCautionManager();
        } catch (SQLException e) {
            System.out.println("DB 오류 : " + e.getMessage());

            if (e.getMessage() != null &&
                    e.getMessage().contains("No suitable driver")) {
                System.out.println("SQLite JDBC 드라이버가 없습니다. Maven 프로젝트를 로드하세요.");
                System.out.println("Maven 설치 후 프로젝트 루트에서: mvn clean compile exec:java");
            } else {
                System.out.println("src/data.db 경로와 DB 테이블을 확인하세요.");
            }
            return;
        }

        for (Trip trip : trips) {
            if (trip.getTripId() >= nextTripId) {
                nextTripId = trip.getTripId() + 1;
            }
        }

        while (true) {
            System.out.println("\n===== 나의 여행 관리 =====");
            System.out.println("1. 새 여행 등록");
            System.out.println("2. 여행 목록 조회");
            System.out.println("3. 기존 여행 선택");
            System.out.println("0. 종료");
            System.out.print("선택 : ");

            String choice = scan.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    Trip trip = createTrip(userId, manager, scan);

                    if (trip != null) {
                        trips.add(trip);
                        TripDetailMenu.start(trip, manager, scan);
                    }
                }

                case "2" -> showTrips(userId, trips, manager);

                case "3" -> {
                    Trip trip = selectTrip(userId, trips, manager, scan);

                    if (trip != null) {
                        TripDetailMenu.start(trip, manager, scan);
                    }
                }

                case "0" -> {
                    return;
                }

                default -> System.out.println("잘못된 메뉴 번호입니다.");
            }
        }
    }

    private static Trip createTrip(int userId,
                                   TravelCautionManager manager,
                                   Scanner scan) {

        System.out.print("여행지 검색 : ");
        String keyword = scan.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("여행지를 입력하세요.");
            return null;
        }

        List<City> results = manager.searchCities(keyword);

        if (results.isEmpty()) {
            System.out.println("검색된 여행지가 없습니다.");
            return null;
        }

        for (City city : results) {
            System.out.println(city);
        }

        try {
            System.out.print("도시 번호 선택 : ");
            int cityId = Integer.parseInt(scan.nextLine().trim());

            boolean found = false;

            for (City city : results) {
                if (city.getId() == cityId) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("검색 결과에 없는 도시 번호입니다.");
                return null;
            }

            System.out.print("여행 시작일 (YYYY-MM-DD) : ");
            LocalDate startDate = LocalDate.parse(scan.nextLine().trim());

            System.out.print("여행 종료일 (YYYY-MM-DD) : ");
            LocalDate endDate = LocalDate.parse(scan.nextLine().trim());

            Trip trip = new Trip(nextTripId, userId, cityId, startDate, endDate);
            nextTripId++;

            System.out.println("여행 등록 완료! 여행 ID : " + trip.getTripId());

            return trip;

        } catch (DateTimeParseException e) {
            System.out.println("날짜 형식은 YYYY-MM-DD로 입력하세요.");
        } catch (IllegalArgumentException e) {
            System.out.println("입력 오류 : " + e.getMessage());
        }

        return null;
    }

    private static void showTrips(int userId, List<Trip> trips,
                                  TravelCautionManager manager) {

        System.out.println("\n===== 등록된 여행 =====");

        int count = 0;

        for (Trip trip : trips) {
            if (trip.getUserId() == userId) {
                System.out.println(
                        trip.getTripId() + " : " +
                                cityName(trip.getCityId(), manager) +
                                " (" + trip.getStartDate() +
                                " ~ " + trip.getEndDate() + ")"
                );
                count++;
            }
        }

        if (count == 0) {
            System.out.println("등록된 여행이 없습니다.");
        }
    }

    private static Trip selectTrip(int userId, List<Trip> trips,
                                   TravelCautionManager manager,
                                   Scanner scan) {

        showTrips(userId, trips, manager);

        System.out.print("여행 ID 선택 (0: 취소) : ");

        try {
            int tripId = Integer.parseInt(scan.nextLine().trim());

            if (tripId == 0) {
                return null;
            }

            for (Trip trip : trips) {
                if (trip.getTripId() == tripId &&
                        trip.getUserId() == userId) {
                    return trip;
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("숫자를 입력하세요.");
            return null;
        }

        System.out.println("해당 여행이 없습니다.");
        return null;
    }

    private static String cityName(int cityId, TravelCautionManager manager) {

        for (City city : manager.searchCities("")) {
            if (city.getId() == cityId) {
                return city.getName();
            }
        }

        return "도시 ID " + cityId;
    }
}
