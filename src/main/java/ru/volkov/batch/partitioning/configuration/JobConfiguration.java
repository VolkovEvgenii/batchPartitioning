package ru.volkov.batch.partitioning.configuration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import ru.volkov.batch.partitioning.components.ColumnRangePartitioner;
import ru.volkov.batch.partitioning.components.Rand;
import ru.volkov.batch.partitioning.components.RandRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private DataSource dataSource;

    public JobConfiguration(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory,
            DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public ColumnRangePartitioner partitioner() {
        ColumnRangePartitioner partitioner = new ColumnRangePartitioner();
        partitioner.setColumn("id");
        partitioner.setTable("scaling.random_data");
        partitioner.setDataSource(this.dataSource);

        return partitioner;
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Rand> pagingItemReader(
            @Value("#{stepExecutionContext['minValue']}") Long minValue,
            @Value("#{stepExecutionContext['maxValue']}") Long maxValue) {
        System.out.println("Reading " + minValue + " to " + maxValue);
        JdbcPagingItemReader<Rand> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setFetchSize(1000);
        reader.setRowMapper(new RandRowMapper());

        PostgresPagingQueryProvider provider = new PostgresPagingQueryProvider();
        provider.setSelectClause("id, random_text, random_data");
        provider.setFromClause("scaling.random_data");
        provider.setWhereClause("id >=" + minValue + " and id <" + maxValue);

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        provider.setSortKeys(sortKeys);

        reader.setQueryProvider(provider);

        return reader;
    }


    @Bean
    public JdbcBatchItemWriter<Rand> jdbcBatchItemWriter() throws Exception {

        JdbcBatchItemWriter<Rand> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(this.dataSource);
        writer.setSql("INSERT INTO scaling.new_random_data (random_text, random_data) VALUES (:randomText, :randomData)");
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.afterPropertiesSet();

        return writer;
    }

    @Bean
    public Step step() throws Exception {
        return stepBuilderFactory.get("step")
                .partitioner(slaveStep().getName(), partitioner())
                .step(slaveStep())
                .gridSize(4)
                .taskExecutor(new SimpleAsyncTaskExecutor())
                .build();
    }

    public Step slaveStep() throws Exception {
        return stepBuilderFactory.get("slaveStep")
                .<Rand, Rand> chunk(1000)
                .reader(pagingItemReader(null, null))
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public Job job() throws Exception {
        return jobBuilderFactory.get("job")
                .start(step())
                .build();
    }
}
