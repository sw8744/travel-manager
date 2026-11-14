package kr.ac.konkuk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TodoList {
    int nextId = 1;
    List<Todo> todoList = new ArrayList<Todo>();

    public void addToDo(Boolean isDone, LocalDate startDate, LocalDate endDate, String description){
        todoList.add(new Todo(nextId++, isDone, startDate, endDate, description));
    }

    private Todo findToDo(int id){
        for(Todo t : todoList){
            if(t != null){
                if (t.getId() == id){
                    return t;
                }
            }
        }
        throw new IllegalArgumentException("해당 id의 todo 객체가 존재하지 않습니다");
    }
    public void setIsDone(int id ,Boolean isDone){
        Todo t = findToDo(id);
        t.setDone(isDone);
    }
    public void setDescription(int id ,String Description){
        Todo t = findToDo(id);
        t.setDescription(Description);
    }
    public void setDate(int id, LocalDate startDate, LocalDate endDate){
        Todo t = findToDo(id);
        t.setDate(startDate, endDate);
    }

}
