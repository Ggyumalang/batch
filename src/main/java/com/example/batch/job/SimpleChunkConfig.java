package com.example.batch.job;

import com.example.batch.domain.Member;
import com.example.batch.dto.MemberDTO;
import com.example.batch.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ConditionalOnProperty(prefix = "spring.batch.job", name = "names", havingValue = "SimpleChunkJob")
public class SimpleChunkConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MemberRepository memberRepository;
    private final EntityManagerFactory entityManagerFactory;
    private static final int CHUNK_SIZE = 3;

    @Bean
    public Job simpleChunkJob() throws SQLException {
        log.info(">>> Started simpleChunkJob");

        return jobBuilderFactory.get("simpleChunkJob")
                .start(simpleChunkStep())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step simpleChunkStep() throws SQLException {
        return stepBuilderFactory.get("simpleChunkStep")
                .<Member, MemberDTO>chunk(CHUNK_SIZE)
                .reader(simpleReader())
                .processor(simpleProcessor())
                .writer(simpleWriter())
                .build();
    }

    @Bean
    public ItemStreamReader<Member> simpleReader() throws SQLException {
        log.info(">>> Started ItemReader");

        return new JpaPagingItemReaderBuilder<Member>()
                .name("JpaPagingItemReaderMember")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select m from Member m ORDER BY id")
                .build();
    }

    @Bean
    public ItemProcessor<Member, MemberDTO> simpleProcessor() {

        return items -> {
            log.info(items.getName());
            MemberDTO memberDTO = MemberDTO.fromEntity(items);
            memberDTO.changeName(memberDTO.getName() + System.currentTimeMillis());
            return memberDTO;
        };
    }

    @Bean
    public ItemWriter<MemberDTO> simpleWriter() {
        return new ItemWriter<MemberDTO>() {
            @Override
            public void write(List<? extends MemberDTO> items) throws Exception {
                log.info(">>> Started ItemWriter results : {}", items);
                memberRepository.saveAll(items.stream().map(Member::fromDto).collect(Collectors.toList()));
            }
        };
    }
}
