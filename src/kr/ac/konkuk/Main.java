package kr.ac.konkuk;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    Container frame = getContentPane();

    Main(String title) {
        this.setTitle(title);
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initLayout();

        this.setVisible(true);
    }

    private void initLayout() {
        frame.add(new JLabel("travel-manager"), "North");
    }

    public static void main(String[] args) {
        new Main("travel-manager");
    }
}
