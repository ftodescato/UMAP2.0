(function(){
  'use strict';

  var umap = angular.module('umap.admin',['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider',function($stateProvider, $urlRouterProvider){
    $stateProvider.state('root.admin',{
      url: 'admin',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/home.html',
              controller:  'AdminController'
            },
            'header@':{
              templateUrl: 'assets/html/admin/header.html'
            }
        }
    });
  }]);

  umap.controller('AdminController',['$scope',function($scope){

  }]);
})();
