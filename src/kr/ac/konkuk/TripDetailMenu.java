package kr.ac.konkuk;

import java.awt.GraphicsEnvironment;
import java.util.List;
import java.util.Scanner;

public class TripDetailMenu {
    public static void start(Trip trip, TravelCautionManager manager, Scanner scan) {
        start(trip, manager, scan, null, List.of(trip));
    }

    public static void start(Trip trip, TravelCautionManager manager, Scanner scan,
                             TripStore store, List<Trip> trips) {
        boolean canOpenSwing = !GraphicsEnvironment.isHeadless();
        if (manager != null) {
            manager.showCautions(trip.getCityId());
        }
        while (true) {
            System.out.println("\n===== 여행 상세 =====");
            System.out.println("여행 ID: " + trip.getTripId());
            System.out.println(trip.getStartDate() + " ~ " + trip.getEndDate());
            TodoMenu.show(trip.getTodoList().getTodos());
            FlightMenu.show(trip.getFlightList().getFlights());
            HotelMenu.show(trip.getHotelList().getHotels());
            ScheduleMenu.show(trip.getScheduleList().getSchedules());
            System.out.println("\n1. 할 일 관리  2. 항공 관리  3. 숙박 관리  4. 일정 관리");
            System.out.print("5. 여행 유의사항");
            if (canOpenSwing) {
                System.out.print("  6. Swing에서 여행 상세 관리");
            }
            System.out.println("  0. 여행 목록으로 돌아가기");
            System.out.print("선택 : ");
            String choice = scan.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> TodoMenu.start(trip.getTodoList(), scan);
                    case "2" -> FlightMenu.start(trip.getFlightList(), scan);
                    case "3" -> HotelMenu.start(trip.getHotelList(), scan);
                    case "4" -> ScheduleMenu.start(trip.getScheduleList(), scan);
                    case "5" -> manager.showCautions(trip.getCityId());
                    case "6" -> {
                        if (canOpenSwing) {
                            TripDetailPanel.showDialog(trip);
                        } else {
                            System.out.println("이 환경에서는 Swing 창을 열 수 없습니다.");
                        }
                    }
                    case "0" -> { return; }
                    default -> System.out.println("잘못된 메뉴 번호입니다.");
                }
            } finally {
                if (store != null) {
                    store.saveTrips(trip.getUserId(), trips);
                }
            }
        }
    }
}
