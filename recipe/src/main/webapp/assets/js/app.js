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
	_self.serverAPI = 'recipe-project/ask?q=';

	$scope.loading = false;
	$scope.formIsSubmitted = false;
	$scope.dataIsReady = false;
	$scope.mainAnswer = '';
	$scope.facts = [];

	$scope.search = function (form) {
		if(form.$valid){
			$scope.facts = [];
			$scope.mainAnswer = '';
			$scope.dataIsReady = false;
			$scope.loading = true;
	
			if ( $scope.loading ) {
				$http.get( _self.serverAPI + $scope.q)
				.then(function(res) {
					if ( res.data.code === 200) {
						var data = res.data.answer;
						$scope.mainAnswer = res.data.answer.text;
						$scope.facts = [];
						
						data.inferedFacts.forEach(function (value) {
							if( value !== null ) {
								var fact = {
									about: value.about,
									data:[]
								};	
								
								for (var key in value.data ) {
									if ( value.data.hasOwnProperty(key) ) {
										var dataItem = {
											isSpan	: false,
											name 	: key,
											value 	: value.data[key]
										};
									}
								}
								$scope.facts.push(fact);
							}
						});
	
					} else {
						$scope.mainAnswer = res.data.message;
					}
	
					$scope.loading = false;
					$scope.dataIsReady = true;
				});
				console.log($scope.input)
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
