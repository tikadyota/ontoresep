'use strict';

angular.module('myApp',['ngAnimate','ngSanitize','ngRoute'])

.directive('questionBox', function () {
	return {
		restrict: 'E',
		templateUrl: 'qform.html'
	}
})

.directive('answerBox', function () {
	return {
		restrict: 'E',
		templateUrl: 'answer.html'
	}
})

.directive('loader', function () {
	return {
		restrict: 'E',
		templateUrl: 'loader.html'
	}
})

.controller('appController', function ($http, $scope) {
	var _self = this;
	_self.serverAPI = 'api/ask?q=';

	$scope.loading = false;
	$scope.formIsSubmitted = false;
	$scope.dataIsReady = false;
	$scope.mainAnswer = '';
	$scope.reseps = [];

	$scope.search = function (form) {
		if(form.$valid){
			$scope.reseps = [];
			$scope.mainAnswer = '';
			$scope.dataIsReady = false;
			$scope.loading = true;
	
			if ( $scope.loading ) {
				$http.get( _self.serverAPI + $scope.q)
				.then(function(res) {
					if ( res.data.code === 200) {
						var data = res.data.answer;
						$scope.mainAnswer = res.data.answer.text;
						$scope.reseps = [];
						
						data.inferedFacts.map( function(value) {							
							if( value !== null ) {
								var resep = {}
								var dataItem = {};
								var entries = Object.entries(value.data);
								
								resep = {
										about: value.about,
									};	
								$scope.reseps.push(resep);
								console.log(entries);
								
								entries.map(function(entry) {
									dataItem = {
										key:entry[0],
										value: entry[1]
									};
									$scope.reseps.push(dataItem);
//									console.log(key);
//									console.log(entry[0]);
//									console.log(entry[1]);
								})
							}
						});
	
					} else {
						$scope.mainAnswer = res.data.message;
					}
	
					$scope.loading = false;
					$scope.dataIsReady = true;
				});
			}
		} else {
			alert("Invalid")
		}
	}	
});

angular.module('myApp').config(function($routeProvider){
	$routeProvider
	.when('/',{
		templateUrl: 'main.html',
		controller: 'appController',
	})
	.otherwise({
		redirectTo:'/'
	});
	
})