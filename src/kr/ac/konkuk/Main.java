package kr.ac.konkuk;

import javax.swing.*;
import java.awt.Dimension;
import java.time.LocalDate;
import java.util.Arrays;

public class Main extends JFrame {
    private Main(Trip trip) {
        super("여행 상세 미리보기 · 변경사항은 저장되지 않습니다");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setContentPane(new TripDetailPanel(trip));
        pack();
        setMinimumSize(new Dimension(760, 440));
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--console")) {
            MainConsole.main(args);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            LocalDate today = LocalDate.now();
            Trip preview = new Trip(1, 0, 0, today, today.plusDays(3));
            new Main(preview).setVisible(true);
        });
    }
}
