package com.todo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todo.dto.TodoMngrIO;

// CrudRepository는 iterator 반환 하지만 JpaRepository는 List로 반환
@Repository
public interface TodoRepository extends JpaRepository<TodoMngrIO, Long> {

}
