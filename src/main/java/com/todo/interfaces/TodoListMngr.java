package com.todo.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.todo.dto.TodoMngrIO;

public interface TodoListMngr{
	
	/**
	 * 할일 등록
	 * @param toDoCrtnIO
	 * @throws Exception
	 */
	public TodoMngrIO registerTodo(TodoMngrIO todoMngrIO);
	
	/**
	 * 할일 수정
	 * @param toDoMdfctnIO
	 */
	public TodoMngrIO modifyTodo(TodoMngrIO todoMngrIO);
	
	/**
	 * 할일 목록 조회  (select tag를 위함)
	 * @return
	 */
	public List<TodoMngrIO> getTodoListForSelect();
	
	/**
	 * 할일 목록 전체조회 (page)
	 * @param pageable
	 * @return
	 */
	public Page<TodoMngrIO> getTodoListForPage(Pageable pageable);

	/**
	 * 단일건 할일 조회 
	 * @param long1 
	 * @return 
	 */
	public TodoMngrIO getTodo(Long jobId);

	/**
	 * 완료 처리
	 * @param jobId
	 * @return
	 */
	public TodoMngrIO completeTodo(Long jobId);
	
}
