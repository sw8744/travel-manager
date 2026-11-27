package kr.ac.konkuk;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.time.LocalDate;

public class Main extends JFrame {
    private Main(Trip trip) {
        super("여행 상세 미리보기 · 변경사항은 저장되지 않습니다");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(new TripDetailPanel(trip));
        pack();
        setMinimumSize(new Dimension(760, 440));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.equals("--console")) {
                MainConsole.main(args);
                return;
            }
        }

        LocalDate today = LocalDate.now();
        Trip preview = new Trip(1, 0, 0, today, today.plusDays(3));
        new Main(preview);
    }
}
