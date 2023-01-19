package com.growfin.todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TodoController {
    @Autowired
    TodoService todoService;

    @PostMapping(value = "/todo/items")
    int addItem(@RequestBody Item item) {
        return todoService.addItem(item);

    }

    @DeleteMapping(value = "/todo/items/{id}")
    void removeItem(@PathVariable int id) {
        todoService.removeItem(id);
    }

    @GetMapping( "/todo/items")
    public List<Item> viewItem() {
        return todoService.viewItem();
    }
}
