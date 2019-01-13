package com.todo.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.todo.dto.TodoMngrIO;
import com.todo.exception.SendMessageErrorException;
import com.todo.interfaces.TodoListMngr;
import com.todo.repository.TodoRepository;

@Service
public class TodoListMngrImpl implements TodoListMngr {
	@Autowired
	TodoRepository toDoRepository;

	@Override
	public TodoMngrIO registerTodo(TodoMngrIO todoMngrIO) {
		if (null == todoMngrIO) {
			throw new SendMessageErrorException("Object is null", "등록 객체가 존재하지 않습니다.");
		}

		if (null == todoMngrIO.getJobName() && "" == todoMngrIO.getJobName().trim()) {
			throw new SendMessageErrorException("jobName is null", "비어 있는 값이 존재합니다.");
		}
		
		String[] redJobIdArr = todoMngrIO.getRefJobId().split(" ");
		for (int i = 0; i < redJobIdArr.length; i++) {
			if (null != redJobIdArr[i] && redJobIdArr[i].length() > 0) {
				if (redJobIdArr[i].indexOf("@") != 0) {
					String tempRefJobId = redJobIdArr[i].substring(1);
					TodoMngrIO refTodoMngrIO = null;
					try {
						Long jobId = Long.parseLong(tempRefJobId);
					} catch (NumberFormatException e) {
						throw new SendMessageErrorException(
								todoMngrIO.getJobId() + " : " + todoMngrIO.getRefJobId(), "잘못된 입력");
					}
				}
			}
		}

		return toDoRepository.save(todoMngrIO);
	}

	@Override
	public TodoMngrIO modifyTodo(TodoMngrIO todoMngrIO) {
		TodoMngrIO modifyMngrIO = null;
		
		if (null == todoMngrIO) {
			throw new SendMessageErrorException("Object is null", "등록 객체가 존재 하지 않습니다.");
		}
		if (null == todoMngrIO.getJobId() && null == todoMngrIO.getJobName() 
				&& "" == todoMngrIO.getJobName().trim()) {
			throw new SendMessageErrorException("jobName or jobId is null", "비어 있는 값이 존재합니다.");
		}
		
		// 찾는 도중에 삭제되는 것을 방지하기 위해서 sync
		synchronized (this) {
			modifyMngrIO = _checkExistTodo(todoMngrIO.getJobId());
			
			modifyMngrIO.setJobName(todoMngrIO.getJobName());
			modifyMngrIO.setRefJobId(todoMngrIO.getRefJobId());

			modifyMngrIO = toDoRepository.save(modifyMngrIO);
		}
		return modifyMngrIO;
	}

	@SuppressWarnings("null")
	@Override
	public List<TodoMngrIO> getTodoListForSelect() {
		List<TodoMngrIO> todoMngrIOList = toDoRepository.findAll();
		if (null == todoMngrIOList && todoMngrIOList.size() < 1) {
			throw new SendMessageErrorException("목록 조회 실패", "저장한 데이터가 없습니다.");
		}
		return toDoRepository.findAll();
	}

	@SuppressWarnings("null")
	@Override
	public Page<TodoMngrIO> getTodoListForPage(Pageable pageable) {
		Page<TodoMngrIO> todoMngrIOList = toDoRepository.findAll(pageable);
		if (null == todoMngrIOList && todoMngrIOList.getContent().size() < 1) {
			throw new SendMessageErrorException("목록 조회 실패", "저장한 데이터가 없습니다.");
		}
		return toDoRepository.findAll(pageable);
	}

	@Override
	public TodoMngrIO getTodo(Long jobId) {
		TodoMngrIO todoMngrIO = _checkExistTodo(jobId);
		return todoMngrIO;
	}

	@Override
	public TodoMngrIO completeTodo(Long jobId) {
		TodoMngrIO todoMngrIO = null;

		// 처리 되기 전까지 다른 곳이 처리가 되면 lock이 걸릴 수도 있음
		synchronized (this) {
			todoMngrIO = _checkExistTodo(jobId);

			// 이미 처리를 완료 했을 때 에러
			if (todoMngrIO.getCompleteYN() == 1) {
				throw new SendMessageErrorException(jobId + ", " + todoMngrIO.getJobName(), "이미 해당 할일을 완료 처리했습니다.");
			}
			String refJobId = todoMngrIO.getRefJobId();

			// 처리되지 않았다면 refJobId 존재를 확인한 후 있다면 다른 것들이 처리가 되었는지 확인하기
			if (null != refJobId && refJobId.length() > 0) {
				String[] redJobIdArr = refJobId.split(" ");
				for (int i = 0; i < redJobIdArr.length; i++) {
					System.out.println(redJobIdArr[i].length()) ;
					if (null != redJobIdArr[i] && redJobIdArr[i].length() > 0) {
						String tempRefJobId = redJobIdArr[i].substring(1);
						TodoMngrIO refTodoMngrIO = null;
						try {
							refTodoMngrIO = _checkExistTodo(Long.parseLong(tempRefJobId));
						} catch (NumberFormatException e) {
							throw new SendMessageErrorException(
									jobId + ", " + tempRefJobId, "잘못된 입력");
						}
						
						if (refTodoMngrIO.getCompleteYN() == 0) {
							throw new SendMessageErrorException(
									refTodoMngrIO.getJobId() + ", " + refTodoMngrIO.getJobName(), "참조된 할일이 존재합니다.");
						}
					}
				}
			}

			todoMngrIO.setCompleteYN(1);
			todoMngrIO = toDoRepository.save(todoMngrIO);
		}

		return todoMngrIO;
	}

	@Override
	public void deleteTodo(List<TodoMngrIO> todoDeleteList) {

	}

	private TodoMngrIO _checkExistTodo(Long jobId) {
		if (null == jobId) {
			throw new SendMessageErrorException(jobId, "해당 할일이 존재하지 않습니다.");
		}
		
		Optional<TodoMngrIO> todoMngrIOOptional = toDoRepository.findById(jobId);
		TodoMngrIO todoMngrIO = null;
		System.out.println(todoMngrIO);
		if (!todoMngrIOOptional.isPresent()) {
			throw new SendMessageErrorException(jobId, "해당 할일이 존재하지 않습니다.");
		} else {
			todoMngrIO = todoMngrIOOptional.get();
			System.out.println("song : " + todoMngrIO);
		}
		
		return todoMngrIOOptional.get();
	}
}
