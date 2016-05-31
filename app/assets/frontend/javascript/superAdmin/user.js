(function(){
  'use strict';
  var umap = angular.module('umap.superAdmin.user',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.superAdmin.addUsers', {
      url: '/addUsers',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/users/addUser.html',
              controller:  'UserControllerSA'
            }
        }
  });
  $stateProvider.state('root.superAdmin.users', {
    url: '/users',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/users/index.html',
            controller:  'UserControllerSA'
          }
      }
  });
  $stateProvider.state('root.superAdmin.updateUser', {
    url: '/users/:id',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/users/updateUser.html',
            controller:  'UserControllerDetailsSA'
          }
      }
  });
     //$locationProvider.html5Mode(true);
  }]);

    umap.factory('UserServiceSA', function($resource) {
      return $resource('/api/usersSA/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      });
    });

// TODO: rifare come per insert user su update
  umap.controller('UserControllerSA',['$scope','UserServiceSA','CompanyService','$stateParams','$state','$window', function($scope, UserServiceSA,CompanyService, $stateParams,$state,$window) {
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
        UserServiceSA.save($scope.user, function(){
          $state.go('root.superAdmin.users')
        });
      };
    $scope.users = UserServiceSA.query();
    $scope.deleteUser = function(id){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        UserServiceSA.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    };
  }]);

  umap.controller('UserControllerDetailsSA',['$scope','UserServiceSA','$state','$stateParams', function($scope, UserServiceSA,$state,$stateParams) {
    $scope.user = UserServiceSA.get({ id:  $stateParams.id });

    $scope.editUser = function(){
      UserServiceSA.update({id:  $stateParams.id}, $scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    }
  }]);
})();
