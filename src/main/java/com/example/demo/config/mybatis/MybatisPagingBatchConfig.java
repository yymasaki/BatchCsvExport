package com.example.demo.config.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class MybatisPagingBatchConfig extends BaseConfig {

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Bean
	@StepScope
	public MyBatisPagingItemReader<Employee> mybatisPagingReader() throws Exception {
		// クエリに渡すパラメータ
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);

		return new MyBatisPagingItemReaderBuilder<Employee>() // builderの生成
				.sqlSessionFactory(sqlSessionFactory) // SqlSessionFactory
				.queryId("com.example.demo.repository.EmployeeMapper.findByGenderPaging") // クエリ
				.parameterValues(parameterValues) // パラメータ
				.pageSize(10) // ページサイズ
				.build();
	}

	@Bean
	public Step exportMybatisPagingStep() throws Exception {
		return this.stepBuilderFactory.get("ExportMybatisPagingStep") // builderの取得
				.<Employee, Employee>chunk(10) // chunkの設定
				.reader(mybatisPagingReader()).listener(readListener) // Reader
				.processor(genderConvertProcessor) // Processor
				.writer(csvWriter()).listener(writeListener) // Writer
				.build();
	}

	@Bean("MybatisPagingJob")
	public Job exportMybatisPagingJob() throws Exception {
		return this.jobBuilderFactory.get("ExportMybatisPagingJob") // builderの取得
				.incrementer(new RunIdIncrementer()) // IDのインクリメント
				.start(exportMybatisPagingStep()) // 最初のStep
				.build();
	}
}
