package com.growfin.todo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.ItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.Job;


@Configuration
@EnableBatchProcessing

public class BatchConfig {
    private static final Logger log = LoggerFactory.getLogger(BatchConfig.class);
    @Autowired
    TodoRepository todoRepository;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;


    @Bean
    public FlatFileItemReader<Item> reader() {

        FlatFileItemReader<Item> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("/records.csv"));


        reader.setLineMapper(new DefaultLineMapper<>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setDelimiter(DELIMITER_COMMA);
                setNames("name", "description");
            }});

            setFieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                setTargetType(Item.class);
            }});
        }});

        return reader;
    }

    @Bean
    public ItemWriter<Item> writer() {
        return items -> {
            log.info("Saving items Records:{}", items);
            todoRepository.saveAll(items);
        };
    }

    @Bean
    public ItemProcessor<Item, Item> processor() {
        return item -> {
            String name = item.getName().toUpperCase();
            item.setName(name);
            return item;
        };
    }

    @Bean
    public Step stepA() {
        return stepBuilderFactory.get("stepA")
                .<Item, Item>chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build()
                ;
    }

    @Bean
    public Job jobA() {
        return jobBuilderFactory.get("jobA")
                .start(stepA())
                .build()
                ;
    }


}
