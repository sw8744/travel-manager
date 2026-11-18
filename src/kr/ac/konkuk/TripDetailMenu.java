package kr.ac.konkuk;

import java.util.Scanner;

public class TripDetailMenu {
    public static void start(Trip trip, TravelCautionManager manager, Scanner scan) {
        while (true) {
            System.out.println("\n===== 여행 상세 =====");
            System.out.println("여행 ID: " + trip.getTripId());
            System.out.println(trip.getStartDate() + " ~ " + trip.getEndDate());
            TodoMenu.show(trip.getTodoList().getTodos());
            FlightMenu.show(trip.getFlightList().getFlights());
            HotelMenu.show(trip.getHotelList().getHotels());
            ScheduleMenu.show(trip.getScheduleList().getSchedules());
            System.out.println("\n1. 할 일 관리  2. 항공 관리  3. 숙박 관리  4. 일정 관리");
            System.out.println("5. 여행 유의사항  0. 여행 목록으로 돌아가기");
            switch (MenuInput.text(scan, "선택")) {
                case "1" -> TodoMenu.start(trip.getTodoList(), scan);
                case "2" -> FlightMenu.start(trip.getFlightList(), scan);
                case "3" -> HotelMenu.start(trip.getHotelList(), scan);
                case "4" -> ScheduleMenu.start(trip.getScheduleList(), scan);
                case "5" -> manager.showCautions(trip.getCityId());
                case "0" -> { return; }
                default -> System.out.println("잘못된 메뉴 번호입니다.");
            }
        }
    }
}
