package com.growfin.todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@RestController
public class TodoController {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqConsumer.class);
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
    @PutMapping("/todo/items")
    void updateItem(@RequestBody Item item){
        try {
            todoService.updateItem(item);
        }
        catch ( RuntimeException e){
            log.info("Item not found....");
        }
    }

}
