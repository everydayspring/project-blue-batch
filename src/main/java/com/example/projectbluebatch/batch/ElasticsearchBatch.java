//package com.example.projectbluebatch.batch;
//
//import com.example.projectbluebatch.config.JobTimeExecutionListener;
//import com.example.projectbluebatch.dto.SearchDocument;
//import com.example.projectbluebatch.repository.ESRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
//@Configuration
//@EnableBatchProcessing
//@AllArgsConstructor
//public class ElasticsearchBatch {
//
//    private final JobRepository jobRepository;
//    private final JobTimeExecutionListener jobTimeExecutionListener;
//    private final JdbcTemplate jdbcTemplate;
//    private final ESRepository elasticsearchRepository;
//    private final PlatformTransactionManager transactionManager;
//
//    @Autowired
//    public ElasticsearchBatch(JobRepository jobRepository, JobTimeExecutionListener jobTimeExecutionListener, @Qualifier("dataDBSource") DataSource dataDBSource,
//                              ESRepository elasticsearchRepository, PlatformTransactionManager transactionManager) {
//        this.jobRepository = jobRepository;
//        this.jobTimeExecutionListener = jobTimeExecutionListener;
//        this.jdbcTemplate = new JdbcTemplate(dataDBSource);
//        this.elasticsearchRepository = elasticsearchRepository;
//        this.transactionManager = transactionManager;
//    }
//
//    @Bean
//    public Job elasticsearchSyncJob() {
//        return new JobBuilder("elasticsearchSyncJob", jobRepository)
//                .listener(jobTimeExecutionListener)
//                .start(elasticsearchSyncStep())
//                .build();
//    }
//
//    @Bean
//    public Step elasticsearchSyncStep() {
//        return new StepBuilder("elasticsearchSyncStep", jobRepository)
//                .<SearchDocument, SearchDocument>chunk(10, transactionManager)
//                .reader(jdbcReader())
//                .writer(elasticsearchWriter())
//                .build();
//    }
//
//    @Bean
//    public ElasticsearchReader jdbcReader() {
//        return new ElasticsearchReader(jdbcTemplate);
//    }
//
//    @Bean
//    public ElasticsearchWriter elasticsearchWriter() {
//        return new ElasticsearchWriter(elasticsearchRepository);
//    }
//}
