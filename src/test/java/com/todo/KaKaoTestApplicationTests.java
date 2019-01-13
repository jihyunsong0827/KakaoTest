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
			todoListMngr.registerTodo(new TodoMngrIO("집안일", ""));
		}
		
		List<TodoMngrIO> todoMngrIOList = todoListMngr.getTodoListForSelect();
		
		System.out.println(todoMngrIOList.size());
		
		// completeTodoTest가 먼저 호출됨 : 따라서 + 1
		Assert.assertEquals(101, todoMngrIOList.size());
	}
	
	/**
	 * page Todo test
	 */
	@Test
	public void pageTodoTest() {
		for (int i = 0; i < 100; i++) {
			todoListMngr.registerTodo(new TodoMngrIO("집안일", ""));
		}
		
		Pageable pageable = new PageRequest(0, 10);
		Page<TodoMngrIO> page = todoListMngr.getTodoListForPage(pageable);
		
		Assert.assertEquals(10, page.getSize());
	}
	
	/**
	 * 할일 완료 test
	 */
	@Test
	public void completeTodoTest() {
		TodoMngrIO todoMngrIO = todoListMngr.registerTodo(new TodoMngrIO("집안일", ""));
		
		System.out.println("song" + todoMngrIO);
		
		todoMngrIO = todoListMngr.completeTodo(1L);
		
		// 완료 처리는 1
		Assert.assertEquals(1, todoMngrIO.getCompleteYN());
	}
	
	/**
	 * 수정 test
	 */
	@Test
	public void modifyTodoTest() {
		TodoMngrIO todoMngrIO = todoListMngr.registerTodo(new TodoMngrIO("집안일", ""));
		
		System.out.println("song" + todoMngrIO);
		
		todoMngrIO.setJobName("카카오페이를 내일 처럼");
		
		todoMngrIO = todoListMngr.modifyTodo(todoMngrIO);
		
		Assert.assertEquals("카카오페이를 내일 처럼", todoMngrIO.getJobName());
	}

}

