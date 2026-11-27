package kr.ac.konkuk;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

final class TodoPanel extends TripItemsPanel {
    private final TodoList items;
    private final JTextField descriptionField = new JTextField();
    private final JTextField startDateField = new JTextField();
    private final JTextField endDateField = new JTextField();
    private final JCheckBox completed = new JCheckBox("완료");
    private final JComboBox<String> completionFilter = new JComboBox<>(
            new String[]{"전체 상태", "완료", "미완료"});

    TodoPanel(TodoList items) {
        super("할 일", new String[]{"ID", "내용", "상태", "시작일", "종료일"});
        this.items = items;

        addField("내용", descriptionField);
        addField("시작일 (선택, YYYY-MM-DD)", startDateField);
        addField("종료일 (선택, YYYY-MM-DD)", endDateField);
        addField("상태", completed);

        addFilter(completionFilter);
        completionFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                refresh();
            }
        });

        JButton changeCompletionButton = new JButton("완료 상태 변경");
        changeCompletionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                try {
                    int id = selectedId();
                    Todo item = items.findTodo(id);
                    items.setDone(id, !item.isDone());
                    refresh();
                } catch (IllegalArgumentException exception) {
                    showError(exception);
                }
            }
        });
        addActionButton(changeCompletionButton);
        refresh();
    }

    @Override
    protected void showItems(LocalDate date) {
        List<Todo> visibleItems;
        if (date == null) {
            visibleItems = items.getTodos();
        } else {
            visibleItems = items.getTodos(date);
        }

        String filter = (String) completionFilter.getSelectedItem();
        for (Todo item : visibleItems) {
            String status = "미완료";
            if (item.isDone()) {
                status = "완료";
            }
            if ("전체 상태".equals(filter) || status.equals(filter)) {
                addRow(new Object[]{item.getId(), item.getDescription(), status,
                        item.getStartDate(), item.getEndDate()});
            }
        }
    }

    @Override
    protected void loadForm(int id) {
        Todo item = items.findTodo(id);
        descriptionField.setText(fieldText(item.getDescription()));
        startDateField.setText(fieldText(item.getStartDate()));
        endDateField.setText(fieldText(item.getEndDate()));
        completed.setSelected(item.isDone());
    }

    @Override
    protected void clearForm() {
        descriptionField.setText("");
        startDateField.setText("");
        endDateField.setText("");
        completed.setSelected(false);
    }

    @Override
    protected void saveForm(Integer id) {
        String description;
        if (id != null && descriptionField.getText().isBlank()) {
            description = items.findTodo(id).getDescription();
        } else {
            description = requiredText(descriptionField, "내용");
        }
        LocalDate start = optionalDate(startDateField, "시작일");
        LocalDate end = optionalDate(endDateField, "종료일");
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦습니다.");
        }

        if (id == null) {
            items.addTodo(completed.isSelected(), start, end, description);
        } else {
            items.setDate(id, start, end);
            items.setDescription(id, description);
            items.setDone(id, completed.isSelected());
        }
    }

    @Override
    protected void deleteItem(int id) {
        items.removeTodo(id);
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }
}
