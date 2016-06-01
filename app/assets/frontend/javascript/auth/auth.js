(function(){
  'use strict';
  var umap = angular.module('umap.login',['ui.router','ngResource']);

  umap.config(['$stateProvider',function($stateProvider){
    $stateProvider.state('root.login',{
      url: '/login',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/index.html',
              controller:  'LoginController'
            }
        }
    });
  }]);
  umap.factory('Login',['$resource',function($resource){
    return{
      Login: $resource('/api/usersSA/:id',{})
    }
  }]);
  umap.controller('LoginController',['Login',function(Login){

  }]);
})();
