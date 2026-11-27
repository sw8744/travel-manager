package kr.ac.konkuk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TripDetailPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);

    public TripDetailPanel(Trip trip) {
        super(new BorderLayout(0, 12));
        setPreferredSize(new Dimension(900, 560));

        JPanel header = new JPanel(new BorderLayout(0, 6));
        header.add(new JLabel("여행 #" + trip.getTripId() + "    "
                + trip.getStartDate() + " ~ " + trip.getEndDate()), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(1, 4, 6, 0));
        String[] names = {"할 일", "항공권", "숙박", "일정"};
        ActionListener listener = new CardButtonListener();
        for (String name : names) {
            JButton button = new JButton(name);
            button.addActionListener(listener);
            buttons.add(button);
        }
        header.add(buttons, BorderLayout.CENTER);

        cards.add(new TodoPanel(trip.getTodoList()), "할 일");
        cards.add(new FlightPanel(trip.getFlightList()), "항공권");
        cards.add(new HotelPanel(trip.getHotelList()), "숙박");
        cards.add(new SchedulePanel(trip.getScheduleList()), "일정");

        add(header, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
    }

    static void showDialog(Trip trip) {
        JOptionPane.showMessageDialog(null, new TripDetailPanel(trip),
                "여행 상세", JOptionPane.PLAIN_MESSAGE);
    }

    private class CardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            cardLayout.show(cards, event.getActionCommand());
        }
    }
}
