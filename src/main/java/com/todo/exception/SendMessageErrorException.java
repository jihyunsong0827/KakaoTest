package com.todo.exception;

public class SendMessageErrorException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private Object toDoValue;
	private String errorContent;
	
	public SendMessageErrorException(Object toDoValue, String errorContent) {
		super(String.format(errorContent + " (" + toDoValue + ")"));
		this.toDoValue = toDoValue;
		this.errorContent = errorContent;
	}

	public Object getToDoName() {
		return toDoValue;
	}

	public String getErrorContent() {
		return errorContent;
	}
}
