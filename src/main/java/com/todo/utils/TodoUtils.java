package com.todo.utils;

import java.time.LocalDate;

/**
 * ToDo Utility
 * @author Jihyun Song
 * @history 
 * 2019.01.10 initial version
 *
 */

public class TodoUtils{

	/**
	 * ����ð��� ��ȸ
	 * @return
	 */
	public String getCurrentTimeStamp() {
		LocalDate currentDate = LocalDate.now();
		return currentDate.toString();
	}
}