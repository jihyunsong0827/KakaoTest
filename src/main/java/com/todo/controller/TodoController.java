package com.todo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.todo.dto.TodoMngrIO;
import com.todo.interfaces.TodoListMngr;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class TodoController {
	@Autowired
	TodoListMngr todoListMngr;

	@RequestMapping(value = "/todo/list", method = RequestMethod.GET)
	public List<TodoMngrIO> getTodoListForSelect(){
		return todoListMngr.getTodoListForSelect();
	}

	@RequestMapping(value = "/todo/page", method = RequestMethod.GET)
	public Page<TodoMngrIO> getTodoListForPage(Pageable pageable) {
		return todoListMngr.getTodoListForPage(pageable);
	}
	
	@RequestMapping(value = "/todo/one/{jobId}", method = RequestMethod.GET)
	public TodoMngrIO getTodo(@PathVariable(value = "jobId") Long jobId) {
		return todoListMngr.getTodo(jobId);
	}
	
	@RequestMapping(value = "/todo/save", method = RequestMethod.POST)
	public TodoMngrIO registerTodo(@RequestBody TodoMngrIO todoMngrIO) {
		return todoListMngr.registerTodo(todoMngrIO);
	}
	
	@RequestMapping(value = "/todo/modify/{jobId}", method = RequestMethod.PUT)
	public TodoMngrIO modifyTodo(@PathVariable(value = "jobId") Long jobId, 
			@RequestBody TodoMngrIO todoMngrIO) {
		todoMngrIO.setJobId(jobId);
		return todoListMngr.modifyTodo(todoMngrIO);
	}
	
	@RequestMapping(value = "/todo/complete/{jobId}", method = RequestMethod.PUT)
	public TodoMngrIO completeTodo(@PathVariable(value = "jobId") Long jobId) {
		return todoListMngr.completeTodo(jobId);
	}
	
}
