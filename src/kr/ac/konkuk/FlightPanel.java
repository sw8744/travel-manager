package kr.ac.konkuk;

import javax.swing.JTextField;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

final class FlightPanel extends TripItemsPanel {
    private final FlightList items;
    private final JTextField flightNumber = new JTextField(20);
    private final JTextField departure = new JTextField(20);
    private final JTextField arrival = new JTextField(20);
    private final JTextField departureTime = new JTextField(20);
    private final JTextField departureOffset = new JTextField(20);
    private final JTextField arrivalTime = new JTextField(20);
    private final JTextField arrivalOffset = new JTextField(20);

    FlightPanel(FlightList items) {
        super("항공권", new String[]{"ID", "편명", "출발지", "도착지", "출발 현지 시각", "도착 현지 시각"});
        this.items = items;
        addField("편명", flightNumber);
        addField("출발지", departure);
        addField("도착지", arrival);
        addField("출발 시각 (YYYY-MM-DD HH:mm, 선택)", departureTime);
        addField("출발 UTC 시차 (예: +09:00)", departureOffset);
        addField("도착 시각 (YYYY-MM-DD HH:mm, 선택)", arrivalTime);
        addField("도착 UTC 시차 (예: +09:00)", arrivalOffset);
        refresh();
    }

    @Override
    protected void showItems(LocalDate date) {
        List<Flight> flights;
        if (date == null) {
            flights = items.getFlights();
        } else {
            flights = items.getFlights(date);
        }
        for (Flight flight : flights) {
            addRow(new Object[]{flight.getId(), flight.getFlightNumber(), flight.getDeparture(), flight.getArrival(),
                    displayTime(flight.getDepartureTime()), displayTime(flight.getArrivalTime())});
        }
    }

    @Override
    protected void loadForm(int id) {
        Flight flight = items.findFlight(id);
        flightNumber.setText(flight.getFlightNumber());
        departure.setText(flight.getDeparture());
        arrival.setText(flight.getArrival());

        ZonedDateTime departing = flight.getDepartureTime();
        departureTime.setText(localTimeText(departing));
        departureOffset.setText(offsetText(departing));

        ZonedDateTime arriving = flight.getArrivalTime();
        arrivalTime.setText(localTimeText(arriving));
        arrivalOffset.setText(offsetText(arriving));
    }

    @Override
    protected void clearForm() {
        flightNumber.setText("");
        departure.setText("");
        arrival.setText("");
        departureTime.setText("");
        departureOffset.setText("");
        arrivalTime.setText("");
        arrivalOffset.setText("");
    }

    @Override
    protected void saveForm(Integer id) {
        String number = requiredText(flightNumber, "편명");
        String from = requiredText(departure, "출발지");
        String to = requiredText(arrival, "도착지");

        ZonedDateTime oldDepartureTime = null;
        ZonedDateTime oldArrivalTime = null;
        if (id != null) {
            Flight flight = items.findFlight(id);
            oldDepartureTime = flight.getDepartureTime();
            oldArrivalTime = flight.getArrivalTime();
        }
        ZonedDateTime departing = readTime(departureTime, departureOffset, "출발", oldDepartureTime);
        ZonedDateTime arriving = readTime(arrivalTime, arrivalOffset, "도착", oldArrivalTime);
        if (departing != null && arriving != null && arriving.isBefore(departing)) {
            throw new IllegalArgumentException("도착 시각이 출발 시각보다 빠릅니다.");
        }
        if (id == null) {
            items.addFlight(number, from, to, departing, arriving);
        } else {
            items.updateFlight(id, number, from, to, departing, arriving);
        }
    }

    @Override
    protected void deleteItem(int id) {
        items.removeFlight(id);
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }

    private static String localTimeText(ZonedDateTime time) {
        if (time == null) {
            return "";
        }
        return time.toLocalDateTime().toString().replace('T', ' ');
    }

    private static String offsetText(ZonedDateTime time) {
        if (time == null) {
            return "";
        }
        return time.getOffset().toString();
    }

    private static String displayTime(ZonedDateTime time) {
        if (time == null) {
            return "미정";
        }
        return localTimeText(time) + " " + offsetText(time);
    }

    private static ZonedDateTime readTime(JTextField timeField, JTextField offsetField, String label,
                                          ZonedDateTime previousValue) {
        String timeText = timeField.getText().strip();
        if (timeText.isEmpty() || timeText.equals("없음")) {
            return null;
        }

        LocalDateTime localTime;
        try {
            localTime = LocalDateTime.parse(timeText.replace(' ', 'T'));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(label + " 시각을 YYYY-MM-DD HH:mm 형식으로 입력하세요.");
        }

        ZoneOffset offset;
        try {
            offset = ZoneOffset.of(offsetField.getText().strip());
        } catch (DateTimeException e) {
            throw new IllegalArgumentException(label + " UTC 시차를 +09:00 또는 -08:00 형식으로 입력하세요.");
        }

        if (previousValue != null && previousValue.toLocalDateTime().equals(localTime)
                && previousValue.getOffset().equals(offset)) {
            return previousValue;
        }
        return ZonedDateTime.of(localTime, offset);
    }
}
