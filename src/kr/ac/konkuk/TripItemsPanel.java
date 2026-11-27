package kr.ac.konkuk;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

abstract class TripItemsPanel extends JPanel {
    private final String itemName;
    private final String[] columnNames;
    private final JTable table;
    private DefaultTableModel model;

    private final JTextField dateField = new JTextField(10);
    private final JTextField idField = new JTextField(6);
    private final JPanel filters = new JPanel();
    private final JPanel actions = new JPanel();
    private final JLabel itemCount = new JLabel();

    private final CardLayout pages = new CardLayout();
    private final JLabel formTitle = new JLabel();
    private final JPanel formFields = new JPanel(new GridLayout(0, 2, 8, 5));
    private final JLabel deleteMessage = new JLabel();
    private final ButtonListener buttonListener = new ButtonListener();

    private LocalDate filterDate;
    private Integer editingId;
    private int deletingId;

    TripItemsPanel(String itemName, String[] columnNames) {
        setLayout(pages);
        this.itemName = itemName;
        this.columnNames = columnNames;
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        table.setEnabled(false);

        add(createListPage(), "목록");
        add(createFormPage(), "입력");
        add(createDeletePage(), "삭제 확인");
    }

    private JPanel createListPage() {
        JPanel page = new JPanel(new BorderLayout(0, 8));

        filters.add(new JLabel("날짜 (YYYY-MM-DD)"));
        filters.add(dateField);
        dateField.addActionListener(buttonListener);
        addButton(filters, "날짜 조회");
        addButton(filters, "전체 날짜");
        addButton(filters, "날짜순 정렬");

        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel(itemName + " 관리"), BorderLayout.NORTH);
        header.add(filters, BorderLayout.CENTER);
        page.add(header, BorderLayout.NORTH);
        page.add(new JScrollPane(table), BorderLayout.CENTER);

        addButton(actions, "추가");
        actions.add(new JLabel("표의 ID"));
        actions.add(idField);
        idField.addActionListener(buttonListener);
        addButton(actions, "수정");
        addButton(actions, "삭제");
        JPanel footer = new JPanel(new BorderLayout());
        footer.add(actions, BorderLayout.CENTER);
        footer.add(itemCount, BorderLayout.EAST);
        page.add(footer, BorderLayout.SOUTH);
        return page;
    }

    private JPanel createFormPage() {
        JPanel page = new JPanel(new BorderLayout(0, 8));
        page.add(formTitle, BorderLayout.NORTH);

        JPanel fields = new JPanel(new BorderLayout());
        fields.add(formFields, BorderLayout.NORTH);
        page.add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        addButton(buttons, "저장");
        addButton(buttons, "취소");
        page.add(buttons, BorderLayout.SOUTH);
        return page;
    }

    private JPanel createDeletePage() {
        JPanel page = new JPanel(new BorderLayout(0, 8));
        page.add(deleteMessage, BorderLayout.NORTH);
        JPanel buttons = new JPanel();
        addButton(buttons, "삭제 확인");
        addButton(buttons, "취소");
        page.add(buttons, BorderLayout.SOUTH);
        return page;
    }

    protected abstract void showItems(LocalDate date);

    protected abstract void loadForm(int id);

    protected abstract void clearForm();

    protected abstract void saveForm(Integer id);

    protected abstract void deleteItem(int id);

    protected abstract void sortItemsByDate();

    protected final void addField(String label, JComponent field) {
        formFields.add(new JLabel(label));
        formFields.add(field);
        if (field instanceof JTextField) {
            ((JTextField) field).addActionListener(buttonListener);
        }
    }

    protected final void addFilter(JComponent component) {
        filters.add(component);
    }

    protected final void addActionButton(JButton button) {
        actions.add(button);
    }

    protected final void addRow(Object[] values) {
        model.addRow(values);
    }

    protected final void refresh() {
        model = new DefaultTableModel(columnNames, 0);
        table.setModel(model);
        showItems(filterDate);
        itemCount.setText(model.getRowCount() + "개");
    }

    protected final int selectedId() {
        try {
            return Integer.parseInt(idField.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("표에 표시된 ID를 숫자로 입력하세요.");
        }
    }

    protected final void showError(IllegalArgumentException exception) {
        JOptionPane.showMessageDialog(this, exception.getMessage());
    }

    private void openAddForm() {
        editingId = null;
        clearForm();
        formTitle.setText(itemName + " 추가");
        pages.show(this, "입력");
    }

    private void openEditForm() {
        int id = selectedId();
        loadForm(id);
        editingId = id;
        formTitle.setText(itemName + " 수정 · ID " + id);
        pages.show(this, "입력");
    }

    private void save() {
        saveForm(editingId);
        refresh();
        pages.show(this, "목록");
    }

    private void openDeletePage() {
        deletingId = selectedId();
        deleteMessage.setText(itemName + " ID " + deletingId + " 항목을 삭제할까요?");
        pages.show(this, "삭제 확인");
    }

    private void delete() {
        deleteItem(deletingId);
        idField.setText("");
        refresh();
        pages.show(this, "목록");
    }

    private void applyDateFilter() {
        filterDate = optionalDate(dateField, "조회 날짜");
        refresh();
    }

    private void addButton(JPanel panel, String label) {
        JButton button = new JButton(label);
        button.addActionListener(buttonListener);
        panel.add(button);
    }

    protected static String fieldText(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    protected static String requiredText(JTextField field, String label) {
        String text = field.getText().strip();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label + " 항목을 입력하세요.");
        }
        return text;
    }

    protected static LocalDate optionalDate(JTextField field, String label) {
        String text = field.getText().strip();
        if (text.isEmpty() || text.equals("없음")) {
            return null;
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(label + " 형식은 YYYY-MM-DD입니다.");
        }
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            try {
                if (event.getSource() == dateField) {
                    applyDateFilter();
                } else if (event.getSource() == idField) {
                    openEditForm();
                } else if (event.getSource() instanceof JTextField) {
                    save();
                } else {
                    switch (event.getActionCommand()) {
                        case "날짜 조회" -> applyDateFilter();
                        case "전체 날짜" -> {
                            dateField.setText("");
                            applyDateFilter();
                        }
                        case "날짜순 정렬" -> {
                            sortItemsByDate();
                            refresh();
                        }
                        case "추가" -> openAddForm();
                        case "수정" -> openEditForm();
                        case "삭제" -> openDeletePage();
                        case "저장" -> save();
                        case "삭제 확인" -> delete();
                        case "취소" -> pages.show(TripItemsPanel.this, "목록");
                    }
                }
            } catch (IllegalArgumentException exception) {
                showError(exception);
            }
        }
    }
}
