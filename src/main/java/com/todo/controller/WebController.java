package com.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.todo.dto.TodoMngrIO;
import com.todo.repository.TodoRepository;

import lombok.extern.slf4j.Slf4j;

// for develop spring4 restapi and webapi annotation
@Slf4j
@RestController
public class WebController {
	
	@Autowired
	TodoRepository todoRepository;
	
	/**
	 * index.html
	 * init sample data
	 * @return
	 */
	@RequestMapping("/")
	public ModelAndView home() {
		// init data
		todoRepository.save(new TodoMngrIO("笼救老", ""));
		todoRepository.save(new TodoMngrIO("弧贰", "@1"));
		todoRepository.save(new TodoMngrIO("没家", "@1"));
		todoRepository.save(new TodoMngrIO("规没家", "@1 @3"));
		todoRepository.save(new TodoMngrIO("氓沥府", "@2"));
		
		// setting index.html
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		return modelAndView;
	}
}
