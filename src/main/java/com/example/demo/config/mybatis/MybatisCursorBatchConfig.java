//package com.example.demo.config.mybatis;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.batch.MyBatisCursorItemReader;
//import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.example.demo.config.BaseConfig;
//import com.example.demo.domain.model.Employee;
//
//@Configuration
//public class MybatisCursorBatchConfig extends BaseConfig {
//
//	@Autowired
//	private SqlSessionFactory sqlSessionFactory;
//
//	@Bean
//	@StepScope
//	public MyBatisCursorItemReader<Employee> mybatisCursorReader() {
//		// クエリに渡すパラメータ
//		Map<String, Object> parameterValues = new HashMap<>();
//		parameterValues.put("genderParam", 1);
//
//		return new MyBatisCursorItemReaderBuilder<Employee>() // builder生成
//				.sqlSessionFactory(sqlSessionFactory) // SqlSessionFactory
//				.queryId("com.example.demo.repository.EmployeeMapper.findByGender") // クエリ
//				.parameterValues(parameterValues) // パラメータ
//				.build();
//	}
//
//	@Bean
//	public Step exportMybatisCursorStep() throws Exception {
//		return this.stepBuilderFactory.get("ExportMybatisCursorStep") // builderの取得
//				.<Employee, Employee>chunk(10) // chunkの設定
//				.reader(mybatisCursorReader()).listener(readListener) // Reader
//				.processor(genderConvertProcessor) // Processor
//				.writer(csvWriter()).listener(writeListener) // Writer
//				.build();
//	}
//
//	@Bean("MybatisCursorJob")
//	public Job exportMybatisCursorJob() throws Exception {
//		return this.jobBuilderFactory.get("ExportMybatisCursorJob") // builderの取得
//				.incrementer(new RunIdIncrementer()) // IDのインクリメント
//				.start(exportMybatisCursorStep()) // 最初のStep
//				.build();
//	}
//}
