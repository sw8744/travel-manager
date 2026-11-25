package kr.ac.konkuk;

import javax.swing.JTextField;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

final class FlightPanel extends TripItemsPanel<Flight> {
    private final FlightList items;

    FlightPanel(FlightList items) {
        super("항공권", "ID", "편명", "출발지", "도착지", "출발 현지 시각", "도착 현지 시각");
        this.items = items;
        refresh();
    }

    @Override
    protected List<Flight> findItems(LocalDate date) {
        return date == null ? items.getFlights() : items.getFlights(date);
    }

    @Override
    protected Object[] rowValues(Flight item) {
        return new Object[]{item.getId(), item.getFlightNumber(), item.getDeparture(), item.getArrival(),
                displayTime(item.getDepartureTime()), displayTime(item.getArrivalTime())};
    }

    @Override
    protected boolean editItem(Flight item) {
        ItemForm form = new ItemForm();
        JTextField flightNumber = form.text("편명", item == null ? null : item.getFlightNumber());
        JTextField departure = form.text("출발지", item == null ? null : item.getDeparture());
        JTextField arrival = form.text("도착지", item == null ? null : item.getArrival());

        ZonedDateTime oldDepartureTime = item == null ? null : item.getDepartureTime();
        JTextField departureTime = form.text("출발 시각 (YYYY-MM-DD HH:mm, 선택)", localTimeText(oldDepartureTime));
        JTextField departureOffset = form.text("출발 UTC 시차 (예: +09:00)",
                oldDepartureTime == null ? null : oldDepartureTime.getOffset());

        ZonedDateTime oldArrivalTime = item == null ? null : item.getArrivalTime();
        JTextField arrivalTime = form.text("도착 시각 (YYYY-MM-DD HH:mm, 선택)", localTimeText(oldArrivalTime));
        JTextField arrivalOffset = form.text("도착 UTC 시차 (예: +09:00)",
                oldArrivalTime == null ? null : oldArrivalTime.getOffset());

        return form.show(this, item == null ? "항공권 추가" : "항공권 수정", () -> {
            String number = requiredText(flightNumber, "편명");
            String from = requiredText(departure, "출발지");
            String to = requiredText(arrival, "도착지");
            ZonedDateTime departing = readTime(departureTime, departureOffset, "출발", oldDepartureTime);
            ZonedDateTime arriving = readTime(arrivalTime, arrivalOffset, "도착", oldArrivalTime);
            if (departing != null && arriving != null && arriving.isBefore(departing)) {
                throw new IllegalArgumentException("도착 시각이 출발 시각보다 빠릅니다.");
            }
            if (item == null) {
                items.addFlight(number, from, to, departing, arriving);
            } else {
                items.updateFlight(item.getId(), number, from, to, departing, arriving);
            }
        });
    }

    @Override
    protected void deleteItem(Flight item) {
        items.removeFlight(item.getId());
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }

    private static String localTimeText(ZonedDateTime time) {
        return time == null ? "" : time.toLocalDateTime().toString().replace('T', ' ');
    }

    private static String displayTime(ZonedDateTime time) {
        return time == null ? "미정" : localTimeText(time) + " " + time.getOffset();
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
