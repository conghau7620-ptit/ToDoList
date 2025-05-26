package aws.demo.todolist.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import aws.demo.todolist.dto.ToDoList;
import aws.demo.todolist.service.ToDoListService;

@RestController
public class ToDoListController {
	private final ToDoListService toDoListService;

    public ToDoListController(ToDoListService toDoListService) {
        this.toDoListService = toDoListService;
    }

    @PostMapping("/api/todolist")
    public ResponseEntity<Void> createToDo(@RequestBody ToDoList toDoList) {
        toDoListService.createToDoList(toDoList);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/api/todolist/{id}")
    public ResponseEntity<ToDoList> getToDo(@PathVariable Integer id) { // Changed to Integer
        ToDoList toDoList = toDoListService.getToDoList(id);
        if (toDoList == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(toDoList, HttpStatus.OK);
    }
    
    @GetMapping("/api/todolist/")
    public ResponseEntity<List<ToDoList>> getListToDos() {
        List<ToDoList> items = toDoListService.getListToDoItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @PutMapping("/api/todolist/{id}")
    public ResponseEntity<Void> updateToDo(@PathVariable Integer id, @RequestBody ToDoList toDoList) { // Changed to Integer
        toDoListService.updateToDoList(id, toDoList);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/api/todolist/{id}")
    public ResponseEntity<Void> deleteToDo(@PathVariable Integer id) { // Changed to Integer
        toDoListService.deleteToDoList(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
