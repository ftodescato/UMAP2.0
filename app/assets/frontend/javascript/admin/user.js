(function(){
  'use strict';
  var umap = angular.module('umap.admin.user',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.admin.addUsers', {
      url: '/addUsers',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/users/addUser.html',
              controller:  'UserController'
            }
        }
  });
  $stateProvider.state('root.admin.users', {
    url: '/users',
    views: {
          'content@': {
            templateUrl: 'assets/html/admin/users/index.html',
            controller:  'UserController'
          }
      }
  });
  $stateProvider.state('root.admin.updateUser', {
    url: '/users/:id',
    views: {
          'content@': {
            templateUrl: 'assets/html/admin/users/updateUser.html',
            controller:  'UserControllerDetails'
          }
      }
  });
     //$locationProvider.html5Mode(true);
  }]);

    umap.factory('UserService', function($resource) {
      return{
        Users: $resource('/api/usersSA/:id',{id: "@id"},{
          update: {
            method: 'PUT' // this method issues a PUT request
          }
        }),
        Identity: $resource('/api/getrole')
      }
    });

  umap.controller('UserController',['$scope','UserService','CompanyService','$stateParams','$state','$window', function($scope, UserService,CompanyService, $stateParams,$state,$window) {
    $scope.companies = CompanyService.query();
     $scope.user = {
       'name': '',
       'surname':'',
       'email':'',
       'password':'',
       'company':'',
       'role': ''
     };
    //$scope.company = UserService.Identity.get();
    $scope.addUser = function(){
      UserService.Users.save($scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    };
    $scope.users = UserService.Users.query();
    $scope.deleteUser = function(id){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        UserService.Users.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    };
  }]);

  umap.controller('UserControllerDetails',['$scope','UserService','$state','$stateParams', function($scope, UserService,$state,$stateParams) {
    $scope.user = UserService.Users.get({ id:  $stateParams.id });
    $scope.editUser = function(){
      UserService.Users.update({id:  $stateParams.id}, $scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    }
  }]);
})();
