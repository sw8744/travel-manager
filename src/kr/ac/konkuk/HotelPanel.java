package kr.ac.konkuk;

import javax.swing.JTextField;
import java.time.LocalDate;
import java.util.List;

final class HotelPanel extends TripItemsPanel<Hotel> {
    private final HotelList items;

    HotelPanel(HotelList items) {
        super("숙박", "ID", "숙소 이름", "국가", "도시", "체크인", "체크아웃");
        this.items = items;
        refresh();
    }

    @Override
    protected List<Hotel> findItems(LocalDate date) {
        return date == null ? items.getHotels() : items.getHotels(date);
    }

    @Override
    protected Object[] rowValues(Hotel item) {
        return new Object[] {
                item.getId(), item.getName(), item.getCountry(), item.getCity(),
                item.getCheckInDate(), item.getCheckOutDate()
        };
    }

    @Override
    protected boolean editItem(Hotel item) {
        ItemForm form = new ItemForm();
        JTextField nameField = form.text("숙소 이름", item == null ? null : item.getName());
        JTextField countryField = form.text("국가", item == null ? null : item.getCountry());
        JTextField cityField = form.text("도시", item == null ? null : item.getCity());
        JTextField checkInField = form.text("체크인 (선택, YYYY-MM-DD)", item == null ? null : item.getCheckInDate());
        JTextField checkOutField = form.text("체크아웃 (선택, YYYY-MM-DD)", item == null ? null : item.getCheckOutDate());

        return form.show(this, item == null ? "숙박 추가" : "숙박 수정", () -> {
            String name = requiredText(nameField, "숙소 이름");
            String country = requiredText(countryField, "국가");
            String city = requiredText(cityField, "도시");
            LocalDate checkIn = optionalDate(checkInField, "체크인");
            LocalDate checkOut = optionalDate(checkOutField, "체크아웃");
            if (checkIn != null && checkOut != null && checkIn.isAfter(checkOut)) {
                throw new IllegalArgumentException("체크아웃은 체크인보다 빠를 수 없습니다.");
            }

            if (item == null) {
                items.addHotel(name, country, city, checkIn, checkOut);
            } else {
                items.updateHotel(item.getId(), name, country, city, checkIn, checkOut);
            }
        });
    }

    @Override
    protected void deleteItem(Hotel item) {
        items.removeHotel(item.getId());
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }
}
