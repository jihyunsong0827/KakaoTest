package com.todo;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import com.todo.dto.TodoMngrIO;
import com.todo.interfaces.TodoListMngr;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KaKaoTestApplicationTests {

	@Autowired
	TodoListMngr todoListMngr;
	
	/**
	 * save test
	 */
	@Test
	public void saveTodoTest() {
		for (int i = 0; i < 100; i++) {
			todoListMngr.registerTodo(new TodoMngrIO("������", ""));
		}
		
		List<TodoMngrIO> todoMngrIOList = todoListMngr.getTodoListForSelect();
		
		System.out.println(todoMngrIOList.size());
		
		// completeTodoTest�� ���� ȣ��� : ���� + 1
		Assert.assertEquals(101, todoMngrIOList.size());
	}
	
	/**
	 * page Todo test
	 */
	@Test
	public void pageTodoTest() {
		for (int i = 0; i < 100; i++) {
			todoListMngr.registerTodo(new TodoMngrIO("������", ""));
		}
		
		Pageable pageable = new PageRequest(0, 10);
		Page<TodoMngrIO> page = todoListMngr.getTodoListForPage(pageable);
		
		Assert.assertEquals(10, page.getSize());
	}
	
	/**
	 * ���� �Ϸ� test
	 */
	@Test
	public void completeTodoTest() {
		TodoMngrIO todoMngrIO = todoListMngr.registerTodo(new TodoMngrIO("������", ""));
		
		System.out.println("song" + todoMngrIO);
		
		todoMngrIO = todoListMngr.completeTodo(1L);
		
		// �Ϸ� ó���� 1
		Assert.assertEquals(1, todoMngrIO.getCompleteYN());
	}
	
	/**
	 * ���� test
	 */
	@Test
	public void modifyTodoTest() {
		TodoMngrIO todoMngrIO = todoListMngr.registerTodo(new TodoMngrIO("������", ""));
		
		System.out.println("song" + todoMngrIO);
		
		todoMngrIO.setJobName("īī�����̸� ���� ó��");
		
		todoMngrIO = todoListMngr.modifyTodo(todoMngrIO);
		
		Assert.assertEquals("īī�����̸� ���� ó��", todoMngrIO.getJobName());
	}

}

