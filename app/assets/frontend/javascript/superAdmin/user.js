(function(){
  'use strict';
  var umap = angular.module('umap.user',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.superAdmin.addUsers', {
      url: '/addUsers',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/users/addUser.html',
              controller:  'UserController'
            }
        }
  });
  $stateProvider.state('root.superAdmin.users', {
    url: '/users',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/users/index.html',
            controller:  'UserController'
          }
      }
  });
  $stateProvider.state('root.updateUser', {
    url: '/users/:id',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/users/updateUser.html',
            controller:  'UserControllerDetails'
          }
      }
  });
     //$locationProvider.html5Mode(true);
  }]);

    umap.factory('UserService', function($resource) {
      return $resource('/api/usersSA/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      });
    });

// TODO: rifare come per insert user su update
  umap.controller('UserController',['$scope','UserService','CompanyService','$stateParams','$state', function($scope, UserService,CompanyService, $stateParams,$state) {
    $scope.companies = CompanyService.query();
     $scope.user = {
       'name': '',
       'surname':'',
       'email':'',
       'password':'',
       'company':'',
       'role': ''
     };

      $scope.addUser = function(){
        UserService.save($scope.user, function(){
          $state.go('root.superAdmin.users')
        });
      };
    $scope.users = UserService.query();
    $scope.deleteUser = function(param){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        UserService.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    };
  }]);

  umap.controller('UserControllerDetails',['$scope','UserService','$state','$stateParams', function($scope, UserService,$state,$stateParams) {
    $scope.user = UserService.get({ id:  $stateParams.id });
    $scope.editUser = function(){
      UserService.update({id:  $stateParams.id}, $scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    }
  }]);
})();
