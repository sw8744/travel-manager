
package kr.ac.konkuk;

import java.util.Scanner;

public class TravelCautionMenu {

    public static void start(Trip trip,
                             TravelCautionManager manager,
                             Scanner scan) {

        manager.showCautions(trip.getCityId());

        while (true) {
            System.out.println("\n===== 여행 관리 =====");
            System.out.println("1. TodoList 관리");
            System.out.println("2. 여행 유의사항 다시 보기");
            System.out.println("0. 돌아가기");
            System.out.print("선택 : ");

            String choice = scan.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    TodoList todoList = trip.getTodoList();
                    System.out.println("TodoList 메뉴 연결 예정");
                }

                case "2" -> manager.showCautions(trip.getCityId());

                case "0" -> {
                    return;
                }

                default -> System.out.println("잘못된 입력입니다.");
            }
        }
    }
}
