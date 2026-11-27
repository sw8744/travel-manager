package kr.ac.konkuk;

import javax.swing.JTextField;
import java.time.LocalDate;
import java.util.List;

final class SchedulePanel extends TripItemsPanel {
    private final ScheduleList items;
    private final JTextField descriptionField = new JTextField();
    private final JTextField startDateField = new JTextField();
    private final JTextField endDateField = new JTextField();

    SchedulePanel(ScheduleList items) {
        super("일정", new String[]{"ID", "내용", "시작일", "종료일"});
        this.items = items;

        addField("내용", descriptionField);
        addField("시작일 (선택, YYYY-MM-DD)", startDateField);
        addField("종료일 (선택, YYYY-MM-DD)", endDateField);
        refresh();
    }

    @Override
    protected void showItems(LocalDate date) {
        List<Schedule> visibleItems;
        if (date == null) {
            visibleItems = items.getSchedules();
        } else {
            visibleItems = items.getSchedules(date);
        }

        for (Schedule item : visibleItems) {
            addRow(new Object[]{item.getId(), item.getDescription(), item.getStartDate(), item.getEndDate()});
        }
    }

    @Override
    protected void loadForm(int id) {
        Schedule item = items.findSchedule(id);
        descriptionField.setText(fieldText(item.getDescription()));
        startDateField.setText(fieldText(item.getStartDate()));
        endDateField.setText(fieldText(item.getEndDate()));
    }

    @Override
    protected void clearForm() {
        descriptionField.setText("");
        startDateField.setText("");
        endDateField.setText("");
    }

    @Override
    protected void saveForm(Integer id) {
        String description;
        if (id != null && descriptionField.getText().isBlank()) {
            description = items.findSchedule(id).getDescription();
        } else {
            description = requiredText(descriptionField, "내용");
        }
        LocalDate start = optionalDate(startDateField, "시작일");
        LocalDate end = optionalDate(endDateField, "종료일");
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦습니다.");
        }

        if (id == null) {
            items.addSchedule(start, end, description);
        } else {
            items.setDate(id, start, end);
            items.setDescription(id, description);
        }
    }

    @Override
    protected void deleteItem(int id) {
        items.removeSchedule(id);
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }
}
