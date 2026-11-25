package kr.ac.konkuk;

import javax.swing.JTextField;
import java.time.LocalDate;
import java.util.List;

final class SchedulePanel extends TripItemsPanel<Schedule> {
    private final ScheduleList items;

    SchedulePanel(ScheduleList items) {
        super("일정", "ID", "내용", "시작일", "종료일");
        this.items = items;
        refresh();
    }

    @Override
    protected List<Schedule> findItems(LocalDate date) {
        return date == null ? items.getSchedules() : items.getSchedules(date);
    }

    @Override
    protected Object[] rowValues(Schedule item) {
        return new Object[]{item.getId(), item.getDescription(), item.getStartDate(), item.getEndDate()};
    }

    @Override
    protected boolean editItem(Schedule item) {
        ItemForm form = new ItemForm();
        JTextField description = form.text(item == null ? "내용" : "내용 (빈칸: 유지)",
                item == null ? null : item.getDescription());
        JTextField startDate = form.text("시작일 (선택, YYYY-MM-DD)", item == null ? null : item.getStartDate());
        JTextField endDate = form.text("종료일 (선택, YYYY-MM-DD)", item == null ? null : item.getEndDate());

        return form.show(this, item == null ? "일정 추가" : "일정 수정", () -> {
            String text = item != null && description.getText().isBlank()
                    ? item.getDescription() : requiredText(description, "내용");
            LocalDate start = optionalDate(startDate, "시작일");
            LocalDate end = optionalDate(endDate, "종료일");
            if (start != null && end != null && start.isAfter(end)) {
                throw new IllegalArgumentException("시작일이 종료일보다 늦습니다.");
            }
            if (item == null) {
                items.addSchedule(start, end, text);
            } else {
                int id = item.getId();
                items.setDate(id, start, end);
                items.setDescription(id, text);
            }
        });
    }

    @Override
    protected void deleteItem(Schedule item) {
        items.removeSchedule(item.getId());
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }
}
