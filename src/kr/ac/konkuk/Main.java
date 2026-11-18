package kr.ac.konkuk;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // 로그인 연결 전 임시 사용자
        TripMenu.start(1, new ArrayList<>(), new Scanner(System.in));
    }
}
