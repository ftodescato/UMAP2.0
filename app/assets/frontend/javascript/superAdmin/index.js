(function(){
  'use strict';

  var umap = angular.module('umap.superAdmin',['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider',function($stateProvider, $urlRouterProvider){
    $stateProvider.state('root.superAdmin',{
      url: 'superAdmin',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/home.html',
              controller:  'SuperAdminController'
            },
            'header@':{
              templateUrl: 'assets/html/superAdmin/header.html'
            }
        }
    });
  }]);

  umap.controller('SuperAdminController',['$scope',function($scope){

  }]);
})();
