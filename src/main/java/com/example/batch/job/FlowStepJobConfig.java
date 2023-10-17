package com.example.batch.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
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
@ConditionalOnProperty(prefix = "spring.batch.job", name = "names", havingValue = "flowStepJob")
public class FlowStepJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job FlowStepJob() {

        return jobBuilderFactory.get("flowStepJob")
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                    .on("FAILED") //firstStep의 ExitStatus가 Failed 일 경우
//                    .fail()//fail을 하면 오류가 떨어짐
                    .to(failedStep())
                    .on("*")//failedStep의 결과가 무엇이든 recordStep을 진행한다.
                    .to(recordStep())
                    .on("*")//recordStep의 결과가 무엇이든 끝낸다.
                    .end()

                .from(firstStep())
                    .on("COMPLETED") //firstStep의 ExitStatus가 Completed 일 경우
                    .to(completedStep())
                    .on("*")
                    .to(recordStep())
                    .on("*")
                    .end()

                .from(firstStep())
                    .on("*")//firstStep의 결과가 FAILED, COMPLETED를 제외한 다른 값일 경우
                    .to(otherStep())
                    .on("*")
                    .to(recordStep())
                    .on("*")
                    .end()
                .end()
                .build();
    }

    @Bean
    @JobScope
    public Step firstStep() {
        return stepBuilderFactory.get("firstStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("firstStep!");
//                    String result = "COMPLETED";
//                    String result = "FAILED";
                    String result = "UNKNOWN";

                    //Flow에서 on은 RepeatStatus가 아닌 ExitStatus를 바라본다.
                    switch (result) {
                        case "COMPLETED":
                            contribution.setExitStatus(ExitStatus.COMPLETED);
                            break;
                        case "FAILED":
                            contribution.setExitStatus(ExitStatus.FAILED);
                            break;
                        case "UNKNOWN":
                            contribution.setExitStatus(ExitStatus.UNKNOWN);
                            break;
                    }

                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step failedStep() {
        return stepBuilderFactory.get("failedStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("failedStep!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step completedStep() {
        return stepBuilderFactory.get("completedStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("completedStep!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step otherStep() {
        return stepBuilderFactory.get("otherStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("otherStep!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    @JobScope
    public Step recordStep() {
        return stepBuilderFactory.get("recordStep")
                .allowStartIfComplete(true)
                .tasklet((contribution, chunkContext) -> {
                    log.info("recordStep!");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

}
