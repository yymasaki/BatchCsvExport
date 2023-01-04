//package com.example.demo.config.jpa;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import javax.persistence.EntityManagerFactory;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.database.JpaCursorItemReader;
//import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
//import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.example.demo.config.BaseConfig;
//import com.example.demo.domain.model.Employee;
//
//@Configuration
//public class JpaCursorBatchConfig extends BaseConfig {
//
//	@Autowired
//	private EntityManagerFactory entityManagerFactory;
//
//	@Bean
//	@StepScope
//	public JpaCursorItemReader<Employee> jpaCursorReader() {
//		String sql = """
//				SELECT id, name, age, gender
//				FROM employee
//				WHERE gender = :genderParam
//				""";
//		JpaNativeQueryProvider<Employee> queryProvider = new JpaNativeQueryProvider<>();
//		queryProvider.setSqlQuery(sql);
//		queryProvider.setEntityClass(Employee.class);
//
//		Map<String, Object> parameterValues = new HashMap<>();
//		parameterValues.put("genderParam", 1);
//
//		return new JpaCursorItemReaderBuilder<Employee>() // builderの生成
//				.entityManagerFactory(entityManagerFactory) // EntityManagerFactoryの指定
//				.name("jpaCursorItemReader") // 名前の設定
//				.queryProvider(queryProvider) // クエリ
//				.parameterValues(parameterValues) // パラメータ
//				.build();
//	}
//
//	@Bean
//	public Step exportJpaCursorStep() throws Exception {
//		return this.stepBuilderFactory.get("ExportJpaCursorStep") // builderの取得
//				.<Employee, Employee>chunk(10) // chunkの設定
//				.reader(jpaCursorReader()).listener(readListener) // Reader
//				.processor(genderConvertProcessor) // Processor
//				.writer(csvWriter()).listener(writeListener) // Writer
//				.build();
//	}
//
//	@Bean("JpaCursorJob")
//	public Job exportJpaCursorJob() throws Exception {
//		return this.jobBuilderFactory.get("ExportJpaCursorJob") // builderの取得
//				.incrementer(new RunIdIncrementer()) // IDのインクリメント
//				.start(exportJpaCursorStep()) // 最初のStep
//				.build();
//	}
//}
