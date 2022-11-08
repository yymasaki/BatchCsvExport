package com.example.demo.config.jdbc;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class JdbcCursorBatchConfig extends BaseConfig {

	@Autowired
	private DataSource dataSource;

	private static final String SELECT_EMPLOYEE_SQL = """
			SELECT id, name, age, gender FROM employee where gender = ?
			""";

	/**
	 * JdbcCursorItemReader
	 */
	@Bean
	@StepScope
	public JdbcCursorItemReader<Employee> jdbcCursorReader() {
		// クエリに渡すパラメータ
		Object[] params = new Object[] { 1 };
		RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);

		return new JdbcCursorItemReaderBuilder<Employee>() // builder生成
				.dataSource(dataSource) // DataSourceのセット
				.name("jdbcCursorItemReader") // 名前のセット
				.sql(SELECT_EMPLOYEE_SQL) // SQLのセット
				.queryArguments(params) // パラメータ
				.rowMapper(rowMapper) // RowMapperのセット
				.build();
	}

	@Bean
	public Step exportJdbcCursorStep() throws Exception {
		return this.stepBuilderFactory.get("ExportJdbcCursorStep") // builderの取得
				.<Employee, Employee>chunk(10) // chunkの設定
				.reader(jdbcCursorReader()).listener(readListener) // Reader
				.processor(this.genderConvertProcessor) // Processor
				.writer(csvWriter()).listener(writeListener) // Writer
				.build();
	}

	@Bean("JobCursorJob")
	public Job exportJdbcCursorJob() throws Exception {
		return this.jobBuilderFactory.get("ExportJdbcCursorJob") // builderの取得
				.incrementer(new RunIdIncrementer()) // IDのインクリメント
				.start(exportJdbcCursorStep()) // 最初のStep
				.build();
	}
}
