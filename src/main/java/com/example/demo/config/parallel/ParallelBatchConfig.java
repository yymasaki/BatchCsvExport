//package com.example.demo.config.parallel;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.launch.support.RunIdIncrementer;
//import org.springframework.batch.item.database.JdbcPagingItemReader;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//import org.springframework.core.task.TaskExecutor;
//
//import com.example.demo.config.BaseConfig;
//import com.example.demo.domain.model.Employee;
//
//@Configuration
//public class ParallelBatchConfig extends BaseConfig {
//
//	@Autowired
//	private JdbcPagingItemReader<Employee> jdbcPagingReader;
//
//	@Bean
//	public TaskExecutor asyncTaskExecutor() {
//		return new SimpleAsyncTaskExecutor("parallel_");
//	}
//
//	@Bean
//	public Step exportParallelStep() throws Exception {
//		return stepBuilderFactory.get("ExportParallelStep") // builderの取得
//				.<Employee, Employee>chunk(10) // chunkの設定
//				.reader(jdbcPagingReader).listener(readListener) // Reader
//				.processor(genderConvertProcessor) // Processor
//				.writer(csvWriter()).listener(writeListener) // Writer
//				.taskExecutor(asyncTaskExecutor()) // executor
//				.throttleLimit(3) // 同時実行数
//				.build();
//	}
//
//	@Bean
//	public Job exportParallelJob() throws Exception {
//		return jobBuilderFactory.get("ExportParallelJob") // builderの取得
//				.incrementer(new RunIdIncrementer()) // IDのインクリメント
//				.start(exportParallelStep()) // 最初のStep
//				.build();
//	}
//}
