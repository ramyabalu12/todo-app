package com.growfin.todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    @Autowired
    TodoRepository todoRepository;

    public int addItem(Item item) {
        Item itemSaved =todoRepository.save(item);
        return itemSaved.getId();
    }

    public void removeItem(int id) {
        todoRepository.deleteById(id);
    }

    public List<Item> viewItem() {
        return todoRepository.findAll();
    }
}
