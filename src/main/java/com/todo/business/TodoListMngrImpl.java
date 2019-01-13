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
			throw new SendMessageErrorException("Object is null", "Empty Object");
		}

		if (null == todoMngrIO.getJobName() && "" == todoMngrIO.getJobName().trim()) {
			throw new SendMessageErrorException("jobName is null", "Empty Value");
		}
		
		String[] redJobIdArr = todoMngrIO.getRefJobId().split(" ");
		for (int i = 0; i < redJobIdArr.length; i++) {
			if (null != redJobIdArr[i] && redJobIdArr[i].length() > 0) {
				if (redJobIdArr[i].indexOf("@") == 0) {
					String tempRefJobId = redJobIdArr[i].substring(1);
					try {
						Long jobId = Long.parseLong(tempRefJobId);
						if (jobId == todoMngrIO.getJobId()) {
							throw new SendMessageErrorException(
									todoMngrIO.getJobId() + " : " + todoMngrIO.getRefJobId(), "self reference is not allowed.");
						}
					} catch (NumberFormatException e) {
						throw new SendMessageErrorException(
								todoMngrIO.getJobId() + " : " + todoMngrIO.getRefJobId(), "Wrong Input");
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
			throw new SendMessageErrorException("Object is null", "Empty Object");
		}
		if (null == todoMngrIO.getJobId() && null == todoMngrIO.getJobName() 
				&& "" == todoMngrIO.getJobName().trim()) {
			throw new SendMessageErrorException("jobName or jobId is null", "Empty Value");
		}
		
		// 찾는 도중에 삭제되는 것을 방지하기 위해서 sync
		synchronized (this) {
			modifyMngrIO = _checkExistTodo(todoMngrIO.getJobId());
			
			String[] redJobIdArr = todoMngrIO.getRefJobId().split(" ");
			
			for (int i = 0; i < redJobIdArr.length; i++) {
				if (null != redJobIdArr[i] && redJobIdArr[i].length() > 0) {
					if (redJobIdArr[i].indexOf("@") == 0) {
						String tempRefJobId = redJobIdArr[i].substring(1);
						
						try {
							Long jobId = Long.parseLong(tempRefJobId);
							TodoMngrIO tempTodoMngrIO = _checkExistTodo(jobId);
							String[] conflictJobId = tempTodoMngrIO.getRefJobId().split(" ");
							
							for (int j = 0; j < conflictJobId.length; j++) {
								if (null != conflictJobId[j] && conflictJobId[j].length() > 0) {
									if (conflictJobId[j].indexOf("@") == 0) {
										String conflictRefJobId = conflictJobId[j].substring(1);
										Long conflictJobIdLong = Long.parseLong(conflictRefJobId);
										
										if (conflictJobIdLong == todoMngrIO.getJobId()) {
											throw new SendMessageErrorException(
													todoMngrIO.getJobId() + " : " + todoMngrIO.getRefJobId(), "Conflict Job");
										}
									}
								}
							}
						} catch (NumberFormatException e) {
							throw new SendMessageErrorException(
									todoMngrIO.getJobId() + " : " + todoMngrIO.getRefJobId(), "Wrong Input");
						}
					}
				}
			}
			
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
			throw new SendMessageErrorException("List is Null", "Failure of List Inqry" );
		}
		return toDoRepository.findAll();
	}

	@SuppressWarnings("null")
	@Override
	public Page<TodoMngrIO> getTodoListForPage(Pageable pageable) {
		Page<TodoMngrIO> todoMngrIOList = toDoRepository.findAll(pageable);
		if (null == todoMngrIOList && todoMngrIOList.getContent().size() < 1) {
			throw new SendMessageErrorException("List is Null", "Failure of List Inqry" );
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
				throw new SendMessageErrorException(jobId + ", " + todoMngrIO.getJobName(), "The job is already complete.");
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
									jobId + ", " + tempRefJobId, "Wrong Input");
						}
						
						if (refTodoMngrIO.getCompleteYN() == 0) {
							throw new SendMessageErrorException(
									refTodoMngrIO.getJobId() + ", " + refTodoMngrIO.getJobName(), "Incomplete referenced job exist.");
						}
					}
				}
			}

			todoMngrIO.setCompleteYN(1);
			todoMngrIO = toDoRepository.save(todoMngrIO);
		}

		return todoMngrIO;
	}


	private TodoMngrIO _checkExistTodo(Long jobId) {
		if (null == jobId) {
			throw new SendMessageErrorException(jobId, "Result doesn't exist");
		}
		
		Optional<TodoMngrIO> todoMngrIOOptional = toDoRepository.findById(jobId);
		TodoMngrIO todoMngrIO = null;
		System.out.println(todoMngrIO);
		if (!todoMngrIOOptional.isPresent()) {
			throw new SendMessageErrorException(jobId, "Result doesn't exist");
		} else {
			todoMngrIO = todoMngrIOOptional.get();
		}
		
		return todoMngrIOOptional.get();
	}
}
