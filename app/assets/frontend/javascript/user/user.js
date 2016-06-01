(function(){
  'use strict';
  var umap = angular.module('umap.user',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.addUser', {
      url: '/superAdmin/addUser',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/user/addUser.html',
              controller:  'UserController'
            }
        }
  });
  $stateProvider.state('root.users', {
    url: '/superAdmin/users',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/users/index.html',
            controller:  'UserController'
          }
      }
});
$stateProvider.state('root.updateUser', {
  url: '/superAdmin/users/:id',
  views: {
        'content@': {
          templateUrl: 'assets/html/superAdmin/users/updateCompany.html',
          controller:  'UserControllerDetails'
        }
    }
});

     //$locationProvider.html5Mode(true);
  }]);

    umap.factory('UserService', function($resource) {
      return $resource('/api/users/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      });
    });


  umap.controller('UserController',['$scope','UserService','$http','$stateParams','$location', function($scope, UserService,$http, $stateParams,$location) {
     $scope.user = {
       'email':'',
       'role': 'superAdmin'};
      $scope.addUser = function(){
        UserService.save($scope.user, function(){
          $location.path('/superAdmin/users')
        });
      };
    $scope.users = UserService.query();
    $scope.deleteUser = function(param){
      UserService.delete({id:  param}, function(){
        $location.path('/superAdmin/users')
      });
    };
  }]);

  umap.controller('UserControllerDetails',['$scope','UserService','$http','$stateParams', function($scope, UserService,$http,$stateParams) {
    $scope.user = CompanyService.get({ id:  $stateParams.id });
    $scope.editUser = function(){
      UserService.update({id:  $stateParams.id}, $scope.user, function(){
        $location.path('/superAdmin/users')
      });
    }
  }]);
})();
