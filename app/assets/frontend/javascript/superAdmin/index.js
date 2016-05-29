(function(){
  'use strict';

  var umap = angular.module('umap.superAdminHome',['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider',function($stateProvider, $urlRouterProvider){
    $stateProvider.state('root.superAdmin',{
      url: 'superAdmin',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/home.html',
              controller:  'SuperAdminController'
            }
        }
    });
  }]);

  umap.controller('SuperAdminController',['$scope',function($scope){

  }]);
})();
