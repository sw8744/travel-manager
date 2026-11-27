package kr.ac.konkuk;

import javax.swing.JTextField;
import java.time.LocalDate;
import java.util.List;

final class HotelPanel extends TripItemsPanel {
    private final HotelList items;
    private final JTextField nameField = new JTextField();
    private final JTextField countryField = new JTextField();
    private final JTextField cityField = new JTextField();
    private final JTextField checkInField = new JTextField();
    private final JTextField checkOutField = new JTextField();

    HotelPanel(HotelList items) {
        super("숙박", new String[]{"ID", "숙소 이름", "국가", "도시", "체크인", "체크아웃"});
        this.items = items;
        addField("숙소 이름", nameField);
        addField("국가", countryField);
        addField("도시", cityField);
        addField("체크인 (선택, YYYY-MM-DD)", checkInField);
        addField("체크아웃 (선택, YYYY-MM-DD)", checkOutField);
        refresh();
    }

    @Override
    protected void showItems(LocalDate date) {
        List<Hotel> hotels;
        if (date == null) {
            hotels = items.getHotels();
        } else {
            hotels = items.getHotels(date);
        }
        for (Hotel hotel : hotels) {
            addRow(new Object[]{hotel.getId(), hotel.getName(), hotel.getCountry(), hotel.getCity(),
                    hotel.getCheckInDate(), hotel.getCheckOutDate()});
        }
    }

    @Override
    protected void loadForm(int id) {
        Hotel hotel = items.findHotel(id);
        nameField.setText(hotel.getName());
        countryField.setText(hotel.getCountry());
        cityField.setText(hotel.getCity());
        checkInField.setText(fieldText(hotel.getCheckInDate()));
        checkOutField.setText(fieldText(hotel.getCheckOutDate()));
    }

    @Override
    protected void clearForm() {
        nameField.setText("");
        countryField.setText("");
        cityField.setText("");
        checkInField.setText("");
        checkOutField.setText("");
    }

    @Override
    protected void saveForm(Integer id) {
        String name = requiredText(nameField, "숙소 이름");
        String country = requiredText(countryField, "국가");
        String city = requiredText(cityField, "도시");
        LocalDate checkIn = optionalDate(checkInField, "체크인");
        LocalDate checkOut = optionalDate(checkOutField, "체크아웃");
        if (checkIn != null && checkOut != null && checkIn.isAfter(checkOut)) {
            throw new IllegalArgumentException("체크아웃은 체크인보다 빠를 수 없습니다.");
        }
        if (id == null) {
            items.addHotel(name, country, city, checkIn, checkOut);
        } else {
            items.updateHotel(id, name, country, city, checkIn, checkOut);
        }
    }

    @Override
    protected void deleteItem(int id) {
        items.removeHotel(id);
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }
}
