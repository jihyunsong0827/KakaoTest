app.service('TodoService', [ '$http', function($http) {
	this.getTodoListForSelect = function getTodoListForSelect() {
		return $http({
			method : 'GET',
			url : '/api/todo/list',
		});
	}
	
	
	// 호출 방법 - http://localhost:8080/api/todo/page?size=100&page=0&sort=jobId,asc
	this.getTodoListForPage = function getTodoListForPage(page, size) {
		return $http({
			method : 'GET',
			url : '/api/todo/page?page=' + page + '&size=' + size + '&sort=jobId,desc'
		});
	}

	this.registerTodo = function registerTodo(todoMngrIO) {
		return $http({
			method : 'POST',
			url : '/api/todo/save',
			data : {
				jobName: todoMngrIO.jobName,
				refJobId: todoMngrIO.refJobId
			}
		});
	}
	
	this.modifyTodo = function modifyTodo(todoMngrIO) {
		return $http({
			method : 'PUT',
			url : '/api/todo/modify/' + todoMngrIO.jobId,
			data : {
				jobName: todoMngrIO.jobName,
				refJobId: todoMngrIO.refJobId
			}
		});
	}
	
	this.completeTodo = function completeTodo(jobId) {
		return $http({
			method : 'PUT',
			url : '/api/todo/complete/' + jobId
		});
	}
	
	this.getTodo = function completeTodo(jobId) {
		return $http({
			method : "GET",
			url : '/api/todo/one/' + jobId
		});
	}

}]);