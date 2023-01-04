//package com.example.demo.config.jdbc;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.sql.DataSource;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.database.JdbcPagingItemReader;
//import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
//import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.RowMapper;
//
//import com.example.demo.config.BaseConfig;
//import com.example.demo.domain.model.Employee;
//
//@Configuration
//public class JdbcPagingBatchConfig extends BaseConfig {
//
//	@Autowired
//	private DataSource dataSource;
//
//	/**
//	 * Pagingのクエリ設定
//	 */
//	@Bean
//	public SqlPagingQueryProviderFactoryBean queryProvider() {
//		// SQL
//		SqlPagingQueryProviderFactoryBean provider = new SqlPagingQueryProviderFactoryBean();
//		provider.setDataSource(dataSource);
//		provider.setSelectClause("SELECT id, name, age, gender");
//		provider.setFromClause("FROM employee");
//		provider.setWhereClause("WHERE gender = :genderParam");
//		provider.setSortKey("id");
//
//		return provider;
//	}
//
//	@Bean
//	@StepScope
//	public JdbcPagingItemReader<Employee> jdbcPagingReader() throws Exception {
//		// クエリに渡すパラメータ
//		Map<String, Object> parameterValues = new HashMap<>();
//		parameterValues.put("genderParam", 1);
//
//		RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);
//
//		return new JdbcPagingItemReaderBuilder<Employee>() // builderの取得
//				.name("JdbcPagingItemReader") // 名前の設定
//				.dataSource(dataSource) // DataSourceを指定
//				.queryProvider(queryProvider().getObject()) // SQL
//				.parameterValues(parameterValues) // パラメータ
//				.rowMapper(rowMapper) // RowMapper
//				.pageSize(5) // 一度に読み取る件数
//				.build();
//	}
//
//	@Bean
//	public Step exportJdbcPagingStep() throws Exception {
//		return this.stepBuilderFactory.get("ExportJdbcPagingStep") // builderの取得
//				.<Employee, Employee>chunk(10) // chunkの設定
//				.reader(jdbcPagingReader()).listener(readListener) // Reader
//				.processor(genderConvertProcessor) // Processor
//				.writer(csvWriter()).listener(writeListener) // Writer
//				.build();
//	}
//
//	@Bean("JdbcPagingJob")
//	public Job exportJdbcPagingJob() throws Exception {
//		return this.jobBuilderFactory.get("ExportJdbcPagingJob") // builderの取得
//				.incrementer(new RunIdIncrementer()) // IDのインクリメント
//				.start(exportJdbcPagingStep()) // 最初のStep
//				.build();
//	}
//
//}
