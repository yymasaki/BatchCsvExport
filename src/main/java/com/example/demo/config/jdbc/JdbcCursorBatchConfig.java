//package com.example.demo.config.jdbc;
//
//import javax.sql.DataSource;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.database.JdbcCursorItemReader;
//import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.RowMapper;
//
//import com.example.demo.config.BatchConfig;
//import com.example.demo.domain.model.Employee;
//import org.springframework.transaction.PlatformTransactionManager;
//
//@Configuration
//public class JdbcCursorBatchConfig extends BatchConfig {
//
//	@Autowired
//	private DataSource dataSource;
//
//	private static final String SELECT_EMPLOYEE_SQL = """
//			SELECT id, name, age, gender FROM employee where gender = ?
//			""";
//
//	/**
//	 * JdbcCursorItemReader
//	 */
//	@Bean
//	@StepScope
//	public JdbcCursorItemReader<Employee> jdbcCursorReader() {
//		// クエリに渡すパラメータ
//		Object[] params = new Object[] { 1 };
//		RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);
//
//		return new JdbcCursorItemReaderBuilder<Employee>() // builder生成
//				.dataSource(dataSource) // DataSourceのセット
//				.name("jdbcCursorItemReader") // 名前のセット
//				.sql(SELECT_EMPLOYEE_SQL) // SQLのセット
//				.queryArguments(params) // パラメータ
//				.rowMapper(rowMapper) // RowMapperのセット
//				.build();
//	}
//
//	@Bean
//	public Step exportJdbcCursorStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
//		return new StepBuilder("exportJdbcCursorStep", jobRepository) // builderの取得
//				.<Employee, Employee>chunk(10, transactionManager) // chunkの設定
//				.reader(jdbcCursorReader()).listener(readListener) // Reader
//				.processor(genderConvertProcessor) // Processor
//				.writer(csvWriter()).listener(writeListener) // Writer
//				.build();
//	}
//
//	@Bean("exportJdbcCursorJob")
//	public Job exportJdbcCursorJob(JobRepository jobRepository, Step exportJdbcCursorStep) throws Exception {
//		return new JobBuilder("exportJdbcCursorJob", jobRepository) // builderの取得
//				.incrementer(new RunIdIncrementer()) // IDのインクリメント
//				.start(exportJdbcCursorStep) // 最初のStep
//				.build();
//	}
//}
