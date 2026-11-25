package kr.ac.konkuk;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

abstract class TripItemsPanel<T> extends JPanel {
    private final String itemName;
    private final JTextField dateField = new JTextField(10);
    private final JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 0));
    private final JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEADING, 6, 0));
    private final JLabel itemCount = new JLabel();
    private final List<JButton> selectionButtons = new ArrayList<>();
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<T> rows = List.of();
    private LocalDate filterDate;

    TripItemsPanel(String itemName, String... columnNames) {
        super(new BorderLayout(0, 12));
        this.itemName = itemName;
        setBorder(BorderFactory.createEmptyBorder(14, 12, 12, 12));

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component cell = super.prepareRenderer(renderer, row, column);
                if (cell instanceof JComponent component) {
                    Object value = getValueAt(row, column);
                    component.setToolTipText(value == null ? null : value.toString());
                }
                return cell;
            }
        };
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.getColumnModel().getColumn(0).setPreferredWidth(48);
        table.getColumnModel().getColumn(0).setMaxWidth(68);
        table.getSelectionModel().addListSelectionListener(event -> {
            boolean hasSelection = table.getSelectedRow() >= 0;
            for (JButton button : selectionButtons) {
                button.setEnabled(hasSelection);
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(event)
                        && table.rowAtPoint(event.getPoint()) >= 0) {
                    applyToSelection(TripItemsPanel.this::editItem);
                }
            }
        });

        JLabel dateLabel = new JLabel("날짜 (YYYY-MM-DD)");
        dateLabel.setLabelFor(dateField);
        filters.add(dateLabel);
        filters.add(dateField);
        addButton(filters, "날짜 조회", this::applyDateFilter);
        dateField.addActionListener(event -> applyDateFilter());
        addButton(filters, "전체 날짜", () -> {
            dateField.setText("");
            filterDate = null;
            refresh();
        });
        addButton(filters, "날짜순 정렬", () -> {
            sortItemsByDate();
            refresh();
        });
        addButton(actions, "추가", () -> {
            if (editItem(null)) {
                refresh();
            }
        });
        addItemAction("수정", this::editItem);
        addItemAction("삭제", this::confirmDelete);

        JPanel footer = new JPanel(new BorderLayout());
        footer.add(actions, BorderLayout.LINE_START);
        footer.add(itemCount, BorderLayout.LINE_END);
        add(filters, BorderLayout.PAGE_START);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(footer, BorderLayout.PAGE_END);
    }

    protected abstract List<T> findItems(LocalDate date);

    protected abstract Object[] rowValues(T item);

    protected abstract boolean editItem(T item);

    protected abstract void deleteItem(T item);

    protected abstract void sortItemsByDate();

    protected final void addFilter(JComponent component) {
        filters.add(component);
    }

    protected final void addItemAction(String label, Predicate<T> action) {
        JButton button = addButton(actions, label, () -> applyToSelection(action));
        button.setEnabled(false);
        selectionButtons.add(button);
    }

    protected final void refresh() {
        T selected = selectedItem();
        rows = findItems(filterDate);
        tableModel.setRowCount(0);
        for (T row : rows) {
            tableModel.addRow(rowValues(row));
        }
        int newSelection = selected == null ? -1 : rows.indexOf(selected);
        if (newSelection >= 0) {
            table.setRowSelectionInterval(newSelection, newSelection);
        }
        itemCount.setText(rows.isEmpty() ? "표시할 항목이 없습니다" : rows.size() + "개");
    }

    private void applyDateFilter() {
        try {
            filterDate = optionalDate(dateField, "조회 날짜");
            refresh();
        } catch (IllegalArgumentException exception) {
            showError(this, exception);
        }
    }

    private T selectedItem() {
        int row = table.getSelectedRow();
        return row >= 0 ? rows.get(table.convertRowIndexToModel(row)) : null;
    }

    private void applyToSelection(Predicate<T> action) {
        T selected = selectedItem();
        if (selected != null && action.test(selected)) {
            refresh();
        }
    }

    private boolean confirmDelete(T item) {
        int result = JOptionPane.showConfirmDialog(this, "선택한 " + itemName + " 항목을 삭제할까요?",
                itemName + " 삭제", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return false;
        }
        deleteItem(item);
        return true;
    }

    private static JButton addButton(JPanel panel, String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(event -> action.run());
        panel.add(button);
        return button;
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

    private static void showError(Component owner, IllegalArgumentException exception) {
        JOptionPane.showMessageDialog(owner, exception.getMessage(), "입력 확인", JOptionPane.ERROR_MESSAGE);
    }

    protected static final class ItemForm {
        private final JPanel panel = new JPanel(new GridBagLayout());
        private int row;

        JTextField text(String label, Object value) {
            JTextField field = new JTextField(value == null ? "" : value.toString(), 23);
            addField(label, field);
            return field;
        }

        JCheckBox checkBox(String label, boolean selected) {
            JCheckBox field = new JCheckBox(label, selected);
            addField("", field);
            return field;
        }

        private void addField(String text, JComponent field) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridy = row++;
            constraints.anchor = GridBagConstraints.LINE_START;
            constraints.insets = new Insets(4, 0, 4, 12);
            if (!text.isEmpty()) {
                JLabel label = new JLabel(text);
                label.setLabelFor(field);
                panel.add(label, constraints);
            }
            constraints.gridx = 1;
            constraints.weightx = 1;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.insets = new Insets(4, 0, 4, 0);
            panel.add(field, constraints);
        }

        boolean show(Component owner, String title, Runnable submit) {
            Object[] options = {"저장", "취소"};
            while (JOptionPane.showOptionDialog(owner, panel, title, JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]) == 0) {
                try {
                    submit.run();
                    return true;
                } catch (IllegalArgumentException exception) {
                    showError(owner, exception);
                }
            }
            return false;
        }
    }
}
