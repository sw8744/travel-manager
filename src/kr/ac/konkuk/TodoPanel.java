package kr.ac.konkuk;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.time.LocalDate;
import java.util.List;

final class TodoPanel extends TripItemsPanel<Todo> {
    private final TodoList items;
    private final JComboBox<Completion> completionFilter = new JComboBox<>(Completion.values());

    TodoPanel(TodoList items) {
        super("할 일", "ID", "내용", "상태", "시작일", "종료일");
        this.items = items;

        completionFilter.getAccessibleContext().setAccessibleName("할 일 완료 상태");
        completionFilter.addActionListener(event -> refresh());
        addFilter(completionFilter);
        addItemAction("완료 상태 변경", item -> {
            items.setDone(item.getId(), !item.isDone());
            return true;
        });
        refresh();
    }

    @Override
    protected List<Todo> findItems(LocalDate date) {
        Completion completion = (Completion) completionFilter.getSelectedItem();
        if (date == null) {
            if (completion == Completion.ALL) {
                return items.getTodos();
            }
            return items.getTodos(completion == Completion.DONE);
        }
        List<Todo> datedItems = items.getTodos(date);
        if (completion == Completion.ALL) {
            return datedItems;
        }
        boolean done = completion == Completion.DONE;
        return datedItems.stream().filter(item -> item.isDone() == done).toList();
    }

    @Override
    protected Object[] rowValues(Todo item) {
        return new Object[]{
                item.getId(), item.getDescription(), item.isDone() ? "완료" : "미완료",
                item.getStartDate(), item.getEndDate()
        };
    }

    @Override
    protected boolean editItem(Todo item) {
        ItemForm form = new ItemForm();
        JTextField description = form.text(item == null ? "내용" : "내용 (빈칸: 유지)",
                item == null ? null : item.getDescription());
        JTextField startDate = form.text("시작일 (선택, YYYY-MM-DD)", item == null ? null : item.getStartDate());
        JTextField endDate = form.text("종료일 (선택, YYYY-MM-DD)", item == null ? null : item.getEndDate());
        JCheckBox completed = form.checkBox("완료", item != null && item.isDone());

        return form.show(this, item == null ? "할 일 추가" : "할 일 수정", () -> {
            String text = item != null && description.getText().isBlank()
                    ? item.getDescription() : requiredText(description, "내용");
            LocalDate start = optionalDate(startDate, "시작일");
            LocalDate end = optionalDate(endDate, "종료일");
            if (start != null && end != null && start.isAfter(end)) {
                throw new IllegalArgumentException("시작일이 종료일보다 늦습니다.");
            }
            if (item == null) {
                items.addTodo(completed.isSelected(), start, end, text);
            } else {
                int id = item.getId();
                items.setDate(id, start, end);
                items.setDescription(id, text);
                items.setDone(id, completed.isSelected());
            }
        });
    }

    @Override
    protected void deleteItem(Todo item) {
        items.removeTodo(item.getId());
    }

    @Override
    protected void sortItemsByDate() {
        items.sortByDate();
    }

    private enum Completion {
        ALL("전체 상태"),
        DONE("완료"),
        INCOMPLETE("미완료");

        private final String label;

        Completion(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
