package com.example.demo.listener;


import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

import com.example.demo.domain.model.Employee;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class WriteListener implements ItemWriteListener<Employee> {

	@Override
	public void beforeWrite(Chunk<? extends Employee> items) {
		// Do Nothing
	}

	@Override
	public void afterWrite(Chunk<? extends Employee> items) {
		log.debug("AfterWrite: count={}", items.size());
	}

	@Override
	public void onWriteError(Exception exception, Chunk<? extends Employee> items) {
		log.error("WriteError: errorMessage={}", exception.getMessage(), exception);
	}
}
