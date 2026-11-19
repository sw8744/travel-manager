package kr.ac.konkuk;

import kr.ac.konkuk.Exceptions.UserAlreadyExistsExeption;
import kr.ac.konkuk.Exceptions.UserNotFoundException;
import kr.ac.konkuk.Exceptions.PasswordDoesNotMatchException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        Database db = new Database();
        TripStore tripStore = new TripStore();
        try {
            db.initialize();
            tripStore.initialize();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            System.out.println("프로젝트 루트에서 ./mvnw compile exec:java로 실행하세요.");
            return;
        }

        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n===== 여행 관리 프로그램 =====");
                System.out.println("1. 로그인");
                System.out.println("2. 회원가입");
                System.out.println("0. 종료");
                System.out.print("선택 : ");

                String choice = scan.nextLine().trim();
                if (choice.equals("0")) {
                    System.out.println("프로그램을 종료합니다.");
                    return;
                }
                if (!choice.equals("1") && !choice.equals("2")) {
                    System.out.println("잘못된 메뉴 번호입니다.");
                    continue;
                }

                String name = "";
                if (choice.equals("2")) {
                    System.out.print("이름 : ");
                    name = scan.nextLine().trim();
                }
                System.out.print("아이디 : ");
                String id = scan.nextLine().trim();
                System.out.print("비밀번호 : ");
                String passwordText = scan.nextLine();

                if ((choice.equals("2") && name.isEmpty()) || id.isEmpty() || passwordText.isEmpty()) {
                    System.out.println("필수 항목을 모두 입력하세요.");
                    continue;
                }

                try {
                    int password = passwordText.hashCode();
                    if (choice.equals("2")) {
                        userManager.register(new User(name, id, password));
                        System.out.println("회원가입이 완료되었습니다. 로그인하세요.");
                        continue;
                    }

                    User user = userManager.login(id, password);
                    int userId = db.getUserNumber(user.id);
                    List<Trip> trips = tripStore.loadTrips(userId);
                    System.out.println(user.name + "님, 환영합니다.");
                    Runnable save = () -> tripStore.saveTrips(userId, trips);
                    try {
                        TripMenu.start(userId, trips, scan, save);
                    } finally {
                        save.run();
                    }
                } catch (UserAlreadyExistsExeption e) {
                    System.out.println("이미 사용 중인 아이디입니다.");
                } catch (UserNotFoundException | PasswordDoesNotMatchException e) {
                    System.out.println("로그인에 실패했습니다. 아이디와 비밀번호를 확인하세요.");
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("\n입력이 종료되어 프로그램을 종료합니다.");
        }
    }
}
