var app = angular.module("myApp", []);

app.controller('ToDoCtrl', ['$scope','TodoService', function ($scope, TodoService) {
	$scope.getTodoListForSelect = function () {
		TodoService.getTodoListForSelect()
		.then(function success(response){
			console.log(response);
			$('.chosen').empty();
			response.data.forEach(function(value) {
				var str = '<option value="' + value.jobId +'">' + value.jobId + "(" + value.jobName + ")" + "</option>";
				$('.chosen').append(str);
			});
			$('.chosen').trigger('chosen:updated');
        },
        function error(response){
        	alert(response.data.message);
        });
	}
	
	$scope.getTodoListForPage = function (index) {
		var page = $scope.currentPage;
		var size = $scope.selectSize;
		
		if (size != undefined && size != null && page != undefined && page != null) {
			if (index == 0) {
				page = index;
			} else {
				page += index;
			}
			
			TodoService.getTodoListForPage(page, size)
			.then(function success(response){
				$scope.todoList = response.data.content;
				$scope.currentPage = response.data.number;
				
				$("#prev-button").removeAttr('disabled');
				$("#next-button").removeAttr('disabled');
				
				if (response.data.number == 0) {
					$("#prev-button").attr('disabled', 'disabled');
				}
				if (response.data.number == (response.data.totalPages - 1)) {
					$("#next-button").attr('disabled', 'disabled');
				}
			},
			function error(response){
				alert(response.data.message);
			});
		} else {
			alert("페이지 갯수를 선택해주세요!");
		}
	}
	
	$scope.registerTodo = function () {
		var todoMngrIO = [];
		var jobName = $scope.jobName;
		if (jobName != undefined && jobName != null && jobName.trim() != "") {
			todoMngrIO.jobName = jobName;
			todoMngrIO.refJobId = $scope.refJobId;
			if (todoMngrIO.refJobId == undefined && todoMngrIO.refJobId == null) {
				todoMngrIO.refJobId = "";
			}
			TodoService.registerTodo(todoMngrIO)
			.then(function success(response){
		        console.log(response);
		        $scope.toDoList = response.data;
		        $scope.message='success';
		        alert("등록 성공!");
			},
			function error(response){
				$scope.message='';
				$scope.errorMessage = 'Error adding Todo!'; 
				alert($scope.errorMessage);
			});
		} else {
			alert("jobName을 입력해주세요!");
		}
	}
	
	// 참조하는 JOB 추가
	$scope.updateRefJobId = function () {
		var tempJobId = $scope.refJobId;
		if (tempJobId != undefined && tempJobId != null) {
			var jobSplit = tempJobId.split(" ");
			
			for (var index in jobSplit) {
				if (jobSplit[index].substring(1) == $scope.selectRefJobId) {
					alert("해당 일은 이미 입력되었습니다. (" + $scope.selectRefJobId + ")");
					return;
				}
			}
			$scope.refJobId = tempJobId + " @" + $scope.selectRefJobId;
		} else {
			$scope.refJobId = "@" + $scope.selectRefJobId;
		}
	}
	
	// 완료하기
	$scope.completeTodo = function (jobId) {
		TodoService.completeTodo(jobId)
		.then(function success(response){
			console.log(response);
			var completeYN = response.data.completeYN;
			if (completeYN == 1) {
				alert("job complete!")
				$("#time-" + jobId).text(response.data.lastChngTimeStamp);
				$("#complete-" + jobId).text("Y");
			}
		},
		function error(response){
			alert(response.data.message);
		});
	}
	
	$scope.initTodo = function () {
		$scope.jobName = "";
		$scope.refJobId = "";
		$('.chosen').empty();
		$('.chosen').trigger('chosen:updated');
	}
	
	$scope.initPageNumber = function() {
		$('.todoTable').empty();
		$scope.pageNumber = 0;
	}
	
	$scope.getTodo = function (jobId) {
		TodoService.getTodo(jobId)
		.then(function success(response) {
			console.log(response);
			$scope.modifyJopId = response.data.jobId;
			$scope.modifyJobName = response.data.jobName;
			$scope.modifyRefJobId = response.data.refJobId;
			$('.chosen-modify').chosen();
		},
		function error(response) {
			$('#modifyTodo').modal('hide');
			alert(response.data.message);
		})
	}
	
	// JOB 이름과 참조하는 JOB을 변경
	$scope.modifyTodo = function () {
		if ($scope.modifyJobName != undefined && $scope.modifyJobName != null && $scope.modifyJobName.trim() != "") {
			var todoMngrIO = [];
			todoMngrIO.jobId   = $scope.modifyJopId;
			todoMngrIO.jobName = $scope.modifyJobName;
			todoMngrIO.refJobId = $scope.modifyRefJobId;
			
			TodoService.modifyTodo(todoMngrIO)
			.then(function success(response){
				console.log(response);
				alert("수정 성공!");
				
				$scope.modifyJobId = "";
				$scope.modifyJobName = "";
				$scope.modifyRefJobId = "";
				$('#modifyTodo').modal('hide');
				
				$("#jobName-" + todoMngrIO.jobId).text(response.data.jobName + " " + response.data.refJobId);
				$("#time-" + todoMngrIO.jobId).text(response.data.lastChngTimeStamp);
			},
			function error(response){
				alert(response.data.message);
			});
		} else {
			alert("jobName을 입력해주세요!");
		}
	}
	
	// 수정화면 참조값 가져오기
	$scope.getTodoListForSelectModify = function () {
		TodoService.getTodoListForSelect()
		.then(function success(response){
			console.log(response);
			$('.chosen-modify').empty();
			response.data.forEach(function(value) {
				var str = '<option value="' + value.jobId +'">' + value.jobId + "(" + value.jobName + ")" + "</option>";
				$('.chosen-modify').append(str);
			});
			$('.chosen-modify').trigger('chosen:updated');
        },
        function error(response){
        	alert(response.data.message);
        });
	}
	
	// 수정 화면 참조하는 JOB 추가
	$scope.updateModifyRefJobId = function () {
		var tempJobId = $scope.modifyRefJobId;
		if (tempJobId != undefined && tempJobId != null) {
			var jobSplit = tempJobId.split(" ");
			
			for (var index in jobSplit) {
				if (jobSplit[index].substring(1) == $scope.selectModifyRefJobId) {
					alert("해당 일은 이미 입력되었습니다. (" + $scope.selectModifyRefJobId + ")");
					return;
				}
			}
			$scope.modifyRefJobId = tempJobId + " @" + $scope.selectModifyRefJobId;
		} else {
			$scope.modifyRefJobId = "@" + $scope.selectModifyRefJobId;
		}
	}
	
	$scope.initModifyTodo = function() {
		$scope.modifyJobName = "";
		$scope.modifyRefJobId = "";
	}
}]);