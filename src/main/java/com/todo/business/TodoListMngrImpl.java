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
			throw new SendMessageErrorException("Object is null", "��� ��ü�� �������� �ʽ��ϴ�.");
		}

		if (null == todoMngrIO.getJobName() && "" == todoMngrIO.getJobName().trim()) {
			throw new SendMessageErrorException("jobName is null", "��� �ִ� ���� �����մϴ�.");
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
								todoMngrIO.getJobId() + " : " + todoMngrIO.getRefJobId(), "�߸��� �Է�");
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
			throw new SendMessageErrorException("Object is null", "��� ��ü�� ���� ���� �ʽ��ϴ�.");
		}
		if (null == todoMngrIO.getJobId() && null == todoMngrIO.getJobName() 
				&& "" == todoMngrIO.getJobName().trim()) {
			throw new SendMessageErrorException("jobName or jobId is null", "��� �ִ� ���� �����մϴ�.");
		}
		
		// ã�� ���߿� �����Ǵ� ���� �����ϱ� ���ؼ� sync
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
			throw new SendMessageErrorException("��� ��ȸ ����", "������ �����Ͱ� �����ϴ�.");
		}
		return toDoRepository.findAll();
	}

	@SuppressWarnings("null")
	@Override
	public Page<TodoMngrIO> getTodoListForPage(Pageable pageable) {
		Page<TodoMngrIO> todoMngrIOList = toDoRepository.findAll(pageable);
		if (null == todoMngrIOList && todoMngrIOList.getContent().size() < 1) {
			throw new SendMessageErrorException("��� ��ȸ ����", "������ �����Ͱ� �����ϴ�.");
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

		// ó�� �Ǳ� ������ �ٸ� ���� ó���� �Ǹ� lock�� �ɸ� ���� ����
		synchronized (this) {
			todoMngrIO = _checkExistTodo(jobId);

			// �̹� ó���� �Ϸ� ���� �� ����
			if (todoMngrIO.getCompleteYN() == 1) {
				throw new SendMessageErrorException(jobId + ", " + todoMngrIO.getJobName(), "�̹� �ش� ������ �Ϸ� ó���߽��ϴ�.");
			}
			String refJobId = todoMngrIO.getRefJobId();

			// ó������ �ʾҴٸ� refJobId ���縦 Ȯ���� �� �ִٸ� �ٸ� �͵��� ó���� �Ǿ����� Ȯ���ϱ�
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
									jobId + ", " + tempRefJobId, "�߸��� �Է�");
						}
						
						if (refTodoMngrIO.getCompleteYN() == 0) {
							throw new SendMessageErrorException(
									refTodoMngrIO.getJobId() + ", " + refTodoMngrIO.getJobName(), "������ ������ �����մϴ�.");
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
			throw new SendMessageErrorException(jobId, "�ش� ������ �������� �ʽ��ϴ�.");
		}
		
		Optional<TodoMngrIO> todoMngrIOOptional = toDoRepository.findById(jobId);
		TodoMngrIO todoMngrIO = null;
		System.out.println(todoMngrIO);
		if (!todoMngrIOOptional.isPresent()) {
			throw new SendMessageErrorException(jobId, "�ش� ������ �������� �ʽ��ϴ�.");
		} else {
			todoMngrIO = todoMngrIOOptional.get();
			System.out.println("song : " + todoMngrIO);
		}
		
		return todoMngrIOOptional.get();
	}
}
