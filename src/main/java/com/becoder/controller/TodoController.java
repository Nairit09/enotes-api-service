package com.becoder.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import com.becoder.dto.TodoDto;
import com.becoder.endpoint.TodoEndpoint;
import com.becoder.service.TodoService;
import com.becoder.util.CommonUtil;

@RestController
public class TodoController implements TodoEndpoint {

	private final TodoService todoService;

	@Autowired
	public TodoController(TodoService todoService) {
		this.todoService = todoService;
	}

	@Override
	public ResponseEntity<?> saveTodo(TodoDto todoDto) throws Exception {

		boolean saveTodo = todoService.saveTodo(todoDto);

		if (saveTodo) {
			return CommonUtil.createBuildResponseMessage("Todo saved success", HttpStatus.CREATED);

		} else {
			return CommonUtil.createErrorResponseMessage("Todo not saved", HttpStatus.INSUFFICIENT_STORAGE);
		}

	}

	@Override
	public ResponseEntity<?> getTodoById(Integer id) throws Exception {
		TodoDto todo = todoService.getTodoById(id);
		return CommonUtil.createBuildResponse(todo, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAllTodoByUser() throws Exception {
		List<TodoDto> todoList = todoService.getTodoByUser();
		if (CollectionUtils.isEmpty(todoList)) {
			return ResponseEntity.noContent().build();
		}
		return CommonUtil.createBuildResponse(todoList, HttpStatus.OK);
	}
}