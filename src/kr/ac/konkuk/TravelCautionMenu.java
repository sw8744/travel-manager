
package kr.ac.konkuk;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class TravelCautionMenu {
    public static void start() {
        try {
            TravelCautionManager manager = new TravelCautionManager();
            Scanner scan = new Scanner(System.in);

            System.out.print("여행지 검색 : ");
            String keyword = scan.nextLine().trim();

            if (keyword.isEmpty()) {
                System.out.println("검색어를 입력하세요.");
                return;
            }

            List<City> results = manager.searchCities(keyword);

            if (results.isEmpty()) {
                System.out.println("검색된 여행지가 없습니다.");
                return;
            }

            for (City city : results) {
                System.out.println(city);
            }

            System.out.print("여행지 번호 선택 : ");
            int cityId = scan.nextInt();

            boolean found = false;

            for (City city : results) {
                if (city.getId() == cityId) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                System.out.println("잘못된 여행지 번호입니다.");
                return;
            }

            int choice = -1;

            while (choice != 0) {
                System.out.println("\n===== 여행 관리 =====");
                System.out.println("1. 여행 유의사항 확인");
                System.out.println("0. 종료");
                System.out.print("메뉴 선택 : ");

                choice = scan.nextInt();

                switch (choice) {
                    case 1 -> manager.showCautions(cityId);
                    case 0 -> System.out.println("프로그램 종료");
                    default -> System.out.println("잘못된 입력입니다.");
                }
            }

        } catch (SQLException e) {
            System.out.println("DB 오류 : " + e.getMessage());
        } catch (java.util.InputMismatchException e) {
            System.out.println("숫자를 입력하세요.");
        }
    }
}

