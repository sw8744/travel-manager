package kr.ac.konkuk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.Objects;

public class TodoList {
    private int nextId = 1;
    private final List<Todo> todoList = new ArrayList<>();

    void restore(Todo todo) {
        todoList.add(todo);
        nextId = Math.max(nextId, todo.getId() + 1);
    }

    public void addTodo(boolean done, LocalDate startDate, LocalDate endDate, String description) {
        todoList.add(new Todo(nextId++, done, startDate, endDate, description));
    }

    public void removeTodo(int id) {
        todoList.remove(findTodo(id));
    }

    public List<Todo> getTodos() {
        return List.copyOf(todoList);
    }

    public List<Todo> getTodos(boolean isDone) {
        List<Todo> result = new ArrayList<>();
        for (Todo todo : todoList) {
            if (todo.isDone() == isDone) {
                result.add(todo);
            }
        }
        return List.copyOf(result);
    }

    public Todo findTodo(int id) {
        for (Todo todo : todoList) {
            if (todo.getId() == id) {
                return todo;
            }
        }
        throw new IllegalArgumentException("해당 ID의 할 일이 존재하지 않습니다.");
    }
    public void setDone(int id, boolean done) {
        Todo t = findTodo(id);
        t.setDone(done);
    }
    public void setDescription(int id, String description) {
        Todo t = findTodo(id);
        t.setDescription(description);
    }
    public void setDate(int id, LocalDate startDate, LocalDate endDate) {
        Todo t = findTodo(id);
        t.setDate(startDate, endDate);
    }

    public List<Todo> getTodos(LocalDate date) {
        Objects.requireNonNull(date, "조회 날짜는 필수입니다.");
        List<Todo> result = new ArrayList<>();
        for (Todo item : todoList) {
            if ((item.getStartDate() != null || item.getEndDate() != null)
                    && (item.getStartDate() == null || !date.isBefore(item.getStartDate()))
                    && (item.getEndDate() == null || !date.isAfter(item.getEndDate()))) {
                result.add(item);
            }
        }
        return List.copyOf(result);
    }

    public void sortByDate() {
        todoList.sort(Comparator.comparing((Todo item) -> item.getStartDate(),
                Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
