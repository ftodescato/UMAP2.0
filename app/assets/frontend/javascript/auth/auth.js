(function(){
  'use strict';
  var umap = angular.module('umap.login',['ui.router','ngResource','ngCookies']);

  umap.config(['$stateProvider',function($stateProvider){
    $stateProvider.state('root.login',{
      url: 'login',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/index.html',
              controller:  'LoginController'
            }
        }
    });
    $stateProvider.state('root.unauthorized',{
      url: 'unauthorized',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/401.html',
              //controller:  'UnauthorizedController'
            }
        }
    });
  }]);
  umap.factory('LoginService',['$resource',function($resource){
    return {
      Login: $resource('/signIn'),
      Role: $resource('/api/getrole')
    }
  }]);
  umap.controller('LoginController',['LoginService','$scope','LoginService','$cookies','$state',function(Login,$scope,LoginService,$cookies,$state){
    $scope.credentials = {'email':'','password':'','rememberMe':false};
    $scope.login = function (){
      LoginService.Login.save({},$scope.credentials,
        function(success){
          $cookies.put('X-Auth-Token', success.token);
          LoginService.Role.get({}, function(success){
            if(success !== null)
              $cookies.put('Role', success.role);
              if(success.role === 'superAdmin')
                $state.go('root.superAdmin');
              else if (success.role === 'admin') {
                  //  $state.go('root.adminHome');
              }
          });

          //$state.go('root.home');
        });
    };
  }]);
})();
