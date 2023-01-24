package com.growfin.todo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import java.util.List;

@Service
public class TodoService {
    private static final Logger log = LoggerFactory.getLogger(RabbitMqConsumer.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routingKey.name}")
    private String routingKey;
    @Autowired
    TodoRepository todoRepository;

    @Autowired
    public RabbitTemplate rabbitTemplate;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job1;

    public int addItem(Item item) {
        Item itemSaved = todoRepository.save(item);
        int id = itemSaved.getId();
        rabbitTemplate.convertAndSend(exchange, routingKey, id);
        return id;
    }

    public void removeItem(int id) {
        todoRepository.deleteById(id);
    }

    public List<Item> viewItem() {
        List<Item> items = todoRepository.findAll();
        log.info("message sent ....");
        rabbitTemplate.convertAndSend(exchange, routingKey,
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

    public void loadDataFromCsv() {
        JobParameters jobParameters =
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters();

        try {
            jobLauncher.run(job1, jobParameters);
        } catch (JobExecutionAlreadyRunningException e) {
            throw new RuntimeException(e);
        } catch (JobRestartException e) {
            throw new RuntimeException(e);
        } catch (JobInstanceAlreadyCompleteException e) {
            throw new RuntimeException(e);
        } catch (JobParametersInvalidException e) {
            throw new RuntimeException(e);
        }
        log.info("JOB Execution completed!");
    }
}
