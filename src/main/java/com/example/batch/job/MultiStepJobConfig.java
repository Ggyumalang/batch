package com.example.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.job", name = "names", havingValue = "multiStepJob")
public class MultiStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job MultiStepJob() {

        return jobBuilderFactory.get("multiStepJob")
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                .next(secondStep())
                .next(lastStep())
                .build();
    }

    @Bean
    @JobScope
    public Step firstStep() {
        return stepBuilderFactory.get("firstStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("firstStep!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step secondStep() {
        return stepBuilderFactory.get("secondStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("secondStep!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step lastStep() {
        return stepBuilderFactory.get("lastStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("lastStep!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
