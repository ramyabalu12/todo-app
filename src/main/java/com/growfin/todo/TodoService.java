package com.growfin.todo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routingKey.name}")
    private String routingKey;
    @Autowired
    TodoRepository todoRepository;

    @Autowired
    public RabbitTemplate rabbitTemplate;

    public int addItem(Item item) {
        Item itemSaved =todoRepository.save(item);
        int id = itemSaved.getId();
        rabbitTemplate.convertAndSend(exchange,routingKey,id);
        return id;
    }

    public void removeItem(int id) {
        todoRepository.deleteById(id);
    }

    public List<Item> viewItem() {
        List<Item> items = todoRepository.findAll();
        System.out.println("message sent ....");
        rabbitTemplate.convertAndSend(exchange,routingKey,
                items);
        return items;
    }

    public void updateItem(Item item) {
        int id = item.getId();
        Item existingitem = todoRepository.findById(id).orElseThrow(() -> new RuntimeException());
        existingitem.setName(item.getName());
        existingitem.setDescription(item.getDescription());
        todoRepository.save(existingitem);
    }
}
