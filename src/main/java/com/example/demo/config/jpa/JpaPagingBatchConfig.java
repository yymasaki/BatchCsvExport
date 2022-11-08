package com.example.demo.config.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class JpaPagingBatchConfig extends BaseConfig {

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Bean
	@StepScope
	public JpaPagingItemReader<Employee> jpaPagingReader() {
		String sql = """
				SELECT id, name, age, gender
				FROM employee
				WHERE gender = :genderParam
				ORDER BY id
				""";
		JpaNativeQueryProvider<Employee> queryProvider = new JpaNativeQueryProvider<>();
		queryProvider.setSqlQuery(sql);
		queryProvider.setEntityClass(Employee.class);

		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);

		return new JpaPagingItemReaderBuilder<Employee>() // builderの生成
				.entityManagerFactory(entityManagerFactory) // EntityManagerFactoryの指定
				.name("jpaPagingItemReader") // 名前の設定
				.queryProvider(queryProvider) // クエリ
				.parameterValues(parameterValues) // パラメータ
				.pageSize(5) // 一度に処理する件数
				.build();
	}

	@Bean
	public Step exportJpaPagingStep() throws Exception {
		return this.stepBuilderFactory.get("ExportJpaPagingStep") // bulderの取得
				.<Employee, Employee>chunk(10) // chunkの設定
				.reader(jpaPagingReader()).listener(readListener) // Reader
				.processor(genderConvertProcessor) // Processor
				.writer(csvWriter()).listener(writeListener) // Writer
				.build();
	}

	@Bean("JpaPagingJob")
	public Job exportJpaPagingJob() throws Exception {
		return this.jobBuilderFactory.get("ExportJpaPagingJob") // builderの取得
				.incrementer(new RunIdIncrementer()) // IDのインクリメント
				.start(exportJpaPagingStep()) // 最初のStep
				.build();
	}
}
