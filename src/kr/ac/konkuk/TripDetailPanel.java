package kr.ac.konkuk;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class TripDetailPanel extends JPanel {
    public TripDetailPanel(Trip trip) {
        super(new BorderLayout(0, 18));
        setBorder(BorderFactory.createEmptyBorder(22, 22, 16, 22));
        setPreferredSize(new Dimension(960, 580));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        JLabel title = new JLabel("여행 상세");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 25f));
        header.add(title, BorderLayout.PAGE_START);
        header.add(new JLabel("여행 #" + trip.getTripId() + "    ·    "
                + trip.getStartDate() + " ~ " + trip.getEndDate()), BorderLayout.CENTER);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("할 일", new TodoPanel(trip.getTodoList()));
        tabs.addTab("항공권", new FlightPanel(trip.getFlightList()));
        tabs.addTab("숙박", new HotelPanel(trip.getHotelList()));
        tabs.addTab("일정", new SchedulePanel(trip.getScheduleList()));

        add(header, BorderLayout.PAGE_START);
        add(tabs, BorderLayout.CENTER);
    }

    static void showDialog(Trip trip) {
        Runnable show = () -> {
            JDialog dialog = new JDialog((Frame) null, "여행 상세", true);
            dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            dialog.add(new TripDetailPanel(trip));
            JButton back = new JButton("돌아가기");
            back.addActionListener(event -> dialog.dispose());
            JPanel footer = new JPanel(new FlowLayout(FlowLayout.TRAILING, 22, 0));
            footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
            footer.add(back);
            dialog.add(footer, BorderLayout.PAGE_END);
            dialog.pack();
            dialog.setMinimumSize(new Dimension(760, 440));
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        };
        try {
            SwingUtilities.invokeAndWait(show);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("여행 상세 창을 기다리는 중 중단되었습니다.", exception);
        } catch (InvocationTargetException exception) {
            throw new IllegalStateException("여행 상세 화면을 열 수 없습니다.", exception.getCause());
        }
    }
}
