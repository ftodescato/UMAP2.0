(function(){
  'use strict';

  var umap = angular.module('umap.user',['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider',function($stateProvider, $urlRouterProvider){
    $stateProvider.state('root.user',{
      url: 'user',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/home.html',
              controller:  'UserController'
            },
            'header@':{
              templateUrl: 'assets/html/user/header.html'
            }
        }
    });
  }]);

  umap.controller('UserController',['$scope',function($scope){

  }]);
})();
