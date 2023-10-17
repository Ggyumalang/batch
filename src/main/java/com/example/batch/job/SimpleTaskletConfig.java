package com.example.batch.job;

import com.example.batch.domain.Member;
import com.example.batch.repository.MemberRepository;
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

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.job" , name = "names", havingValue = "simpleTaskletJob")
public class SimpleTaskletConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MemberRepository memberRepository;

    @Bean
    public Job simpleTaskletJob() {
        log.info(">>> Started simpleTaskletJob");

        return jobBuilderFactory.get("simpleTaskletJob")
                .start(simpleTaskletStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    @Transactional
    @JobScope
    public Step simpleTaskletStep() {
        return stepBuilderFactory.get("simpleTaskletStep")
                .tasklet((contribution, chunkContext) -> {
                    log.info("Step!");
                    memberRepository.saveAll(setMemberList());
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    private List<Member> setMemberList() {
        return List.of(Member.builder()
                        .memberId("khg1")
                        .name("kim")
                        .password("1")
                        .build(),
                Member.builder()
                        .memberId("khg2")
                        .name("han")
                        .password("2")
                        .build(),
                Member.builder()
                        .memberId("khg3")
                        .name("gyu")
                        .password("3")
                        .build()
        );
    }
}
