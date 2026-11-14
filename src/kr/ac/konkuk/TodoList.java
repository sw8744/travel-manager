package kr.ac.konkuk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoList {
    private int nextId = 1;
    private final List<Todo> todoList = new ArrayList<>();

    public void addTodo(boolean done, LocalDate startDate, LocalDate endDate, String description) {
        todoList.add(new Todo(nextId++, done, startDate, endDate, description));
    }

    private Todo findTodo(int id) {
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

}
