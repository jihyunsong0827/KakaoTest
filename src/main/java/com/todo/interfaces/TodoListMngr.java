package com.todo.interfaces;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.todo.dto.TodoMngrIO;

public interface TodoListMngr{
	
	/**
	 * ���� ���
	 * @param toDoCrtnIO
	 * @throws Exception
	 */
	public TodoMngrIO registerTodo(TodoMngrIO todoMngrIO);
	
	/**
	 * ���� ����
	 * @param toDoMdfctnIO
	 */
	public TodoMngrIO modifyTodo(TodoMngrIO todoMngrIO);
	
	/**
	 * ���� ��� ��ȸ  (select tag�� ����)
	 * @return
	 */
	public List<TodoMngrIO> getTodoListForSelect();
	
	/**
	 * ���� ��� ��ü��ȸ (page)
	 * @param pageable
	 * @return
	 */
	public Page<TodoMngrIO> getTodoListForPage(Pageable pageable);

	/**
	 * ���ϰ� ���� ��ȸ 
	 * @param long1 
	 * @return 
	 */
	public TodoMngrIO getTodo(Long jobId);

	/**
	 * �Ϸ� ó��
	 * @param jobId
	 * @return
	 */
	public TodoMngrIO completeTodo(Long jobId);
	
}
