package com.growfin.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
@Service
public class RabbitMqConsumer {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqConsumer.class);
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessage(Object response) {
        log.info("Received message as a generic AMQP 'Message' wrapper: {}", response.toString());
    }

}
