# KakaoTest

**개발환경**

- Backend
  - JAVA8
  - Spring Boot 2.1.1
  - JPA
  - gradle
- Frontend 
  - Jquery 
  - Bootstrap
  - Angular
  - DataTables
  - Chosen (https://harvesthq.github.io/chosen/)

**Execute**

    ./gradlew bootRun

**Build Encoding UTF-8 설정**

gradle을 실행할 때 한글 깨짐을 방지하기 위해 시스템 환경변수 옵션에 file.encoding 을 추가 (윈도우 시스템설정 - 환경변수)

	GRADLE_OPTS=-Dfile.encoding=UTF-8

**URL**

	http://localhost:8080/ 


## 1. Todo 리스트 기능 요건

### 1. 1. 기능 요건 정리

1. 사용자는 할일을 추가/수정(ex.완료처리, Todo 명 수정)/조회(페이징 기능 포함)할 수 있음.
2. 사용자는 텍스트로된 할일 추가할 시, 다른 할일을 참조할 수 있음.
3. 사용자는 선택한 Todo를 완료시, 참조가 걸린 Todo를 반드시 완료처리 해야함. 

## 2. 문제 해결 전략

### 2. 1. API 정리
  1. getTodoListForSelect(api/todo/list, GET 방식)
      - TodoList 목록, select box안에 참
  2. getTodoListForPage(api/todo/page, GET 방식)
      - Pageable 라이브러리를 활용하여 검색한 데이터의 해당 페이지, 보여줄 갯수, 정렬기준(ex. Todo의 Id/오름차순)을 정의하여 출력
  3. getTodo(api/todo/{jobid}, GET 방식)
      - Todo의 Id로 단건을 조회
  4. registerTodo(api/todo/save, POST 방식)
      - Todo를 단건으로 저장
  5. modifyTodo(todo/modify/{jobid}, PUT 방식)
      - Todo 단건으로 수정
  6. completeTodo(/todo/complete/{jobId}, PUT 방식)
      - Todo 단건을 완료처리

### 2. 2 JPA H2
  1. JPA Repository 클래스를 활용하며 데이터 CRUD 기능을 구현함.
  2. 데이터베이스는 H2를 통해 다음과 같이 구현하였음.
  
	      create table todo_list (
		      job_id bigint not null,
		      complete_yn number(255) default 0 not null,
		      frst_reg_timestamp timestamp not null,
		      job_name varchar(255) not null,
		      last_chng_timestamp timestamp,
		      ref_job_id varchar(255),
		      primary key (job_id)
	      )
   
   3. lombok 라이브러리를 통해 오브젝트 getter/setter/constructor 기능을 용이하게 함.

### 2. 3. 에러 처리

   1. RuntimeException을 상속하여 사용자 정의 예외처리를 생성.
   2. 각 개별 API의 업무조건에 맞지 않을시 SendMessageErrorException 발생.
   3. Todo.controller.js에서 function error(response)로 에러메세지를 출력.
       
            ex. throw new SendMessageErrorException("List is Null", "Failure of List Inqry" );
            
   4. 서버에서 받은 Error reponse를 이용하여 alert 메세지를 출력.
   
            ex. alert(response.data.message);

### 2. 4. View
   1. Chosen 오픈 소스 라이브러리를 통해 데이터 검색이 가능한 select 기능을 구현함.
   2. Pageable API를 통해 받은 페이지 데이터를 이용하여, 한 페이지에 보여지는 todo의 갯수를 정하여 출력하는 기능 구현.
   3. BootStrap 라이브러리를 사용하여 전체적인 UI를 구성하였음.
    
 ### 2. 5. 단위테스트
 
   1. 단위테스트는 JUnit Test로 수행.
   2. 각 단위 테스트 설명
       - saveTodoTest : 저장한 갯수와 조회 갯수가 일치하는지 확인.
       - pageTodoTest : 특정 페이지와 Size를 호출했을때, 반환되는 페이지사이즈가 일치하는지 확인.
       - completeTodoTest : 할일 완료 처리후, 완료여부의 리턴값이 1(Y)인지 확인.
       - modifyTodoTest : Todo의 이름을 수정후, 해당 Todo의 이름이 입력한 String 값과 동일한지 확인.
