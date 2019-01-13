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
	 * 현재시간을 조회
	 * @return
	 */
	public String getCurrentTimeStamp() {
		LocalDate currentDate = LocalDate.now();
		return currentDate.toString();
	}
}