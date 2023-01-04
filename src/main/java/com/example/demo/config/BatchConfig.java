package com.example.demo.config;

import java.nio.charset.StandardCharsets;

import com.example.demo.listener.ReadListener;
import com.example.demo.listener.WriteListener;
import com.example.demo.processor.GenderConvertProcessor;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.example.demo.domain.model.Employee;
import org.springframework.core.io.WritableResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig  extends DefaultBatchConfiguration{

	@Autowired
	protected GenderConvertProcessor genderConvertProcessor;

	@Autowired
	protected SampleProperty property;

	@Autowired
	protected ReadListener readListener;

	@Autowired
	protected WriteListener writeListener;

	@Autowired
	protected FlatFileHeaderCallback csvHeaderCallback;

	@Autowired
	protected FlatFileFooterCallback csvFooterCallback;

	@Autowired
	private DataSource dataSource;

	private static final String SELECT_EMPLOYEE_SQL = """
			SELECT id, name, age, gender FROM employee where gender = ?
			""";

	/**
	 * JdbcCursorItemReader
	 */
	@Bean
	public JdbcCursorItemReader<Employee> jdbcCursorReader() {
		// クエリに渡すパラメータ
		Object[] params = new Object[] { 1 };
		RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);

		return new JdbcCursorItemReaderBuilder<Employee>() // builder生成
				.name("jdbcCursorItemReader") // 名前のセット
				.sql(SELECT_EMPLOYEE_SQL) // SQLのセット
				.queryArguments(params) // パラメータ
				.rowMapper(rowMapper) // RowMapperのセット
				.dataSource(dataSource) // DataSourceのセット
				.build();
	}

	/**
	 * CSV出力のWriter生成
	 */
	@Bean
	public FlatFileItemWriter<Employee> csvWriter() {
		// ファイル出力先
		String filePath = property.outputPath();
		WritableResource outputResource = new FileSystemResource(filePath);

		// 区切り文字設定
		DelimitedLineAggregator<Employee> aggregator = new DelimitedLineAggregator<>();
		aggregator.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);

		// 出力フィールドの設定
		BeanWrapperFieldExtractor<Employee> extractor = new BeanWrapperFieldExtractor<>();
		extractor.setNames(new String[] { "id", "name", "age", "genderString" });
		aggregator.setFieldExtractor(extractor);

		return new FlatFileItemWriterBuilder<Employee>() // builderの取得
				.name("employeeCsvWriter") // 名前の設定
				.resource(outputResource) // ファイル出力先
				.lineAggregator(aggregator) // 区切り文字
//				.headerCallback(csvHeaderCallback) // ヘッダー
//				.footerCallback(csvFooterCallback) // フッター
				.encoding(StandardCharsets.UTF_8.name()) // 文字コード
				.build();
	}

	@Bean
	public Step exportJdbcCursorStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) throws Exception {
		return new StepBuilder("exportJdbcCursorStep", jobRepository) // builderの取得
				.<Employee, Employee>chunk(10, transactionManager) // chunkの設定
				.reader(jdbcCursorReader()).listener(readListener) // Reader
				.processor(genderConvertProcessor) // Processor
				.writer(csvWriter()).listener(writeListener) // Writer
				.build();
	}

	@Bean
	public Job exportJdbcCursorJob(JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource) throws Exception {
		return new JobBuilder("exportJdbcCursorJob", jobRepository) // builderの取得
				.incrementer(new RunIdIncrementer()) // IDのインクリメント
				.start(exportJdbcCursorStep(jobRepository, transactionManager)) // 最初のStep
				.build();
	}
}
