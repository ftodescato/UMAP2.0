(function(){
  'use strict';
  var umap = angular.module('umap.admin.user',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.admin.addUsers', {
      url: '/addUsers',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/users/addUser.html',
              controller:  'UserControllerA'
            }
        }
  });
  $stateProvider.state('root.admin.users', {
    url: '/users',
    views: {
          'content@': {
            templateUrl: 'assets/html/admin/users/index.html',
            controller:  'UserControllerA'
          }
      }
  });
  $stateProvider.state('root.admin.updateUser', {
    url: '/users/:id',
    views: {
          'content@': {
            templateUrl: 'assets/html/admin/users/updateUser.html',
            controller:  'UserControllerDetailsA'
          }
      }
  });
     //$locationProvider.html5Mode(true);
  }]);

    umap.factory('UserServiceA', function($resource) {
      return{
        Users: $resource('/api/usersA/:id',{id: "@id"},{
          update: {
            method: 'PUT' // this method issues a PUT request
          }
        }),
        Identity: $resource('/api/getrole')
      }
    });

  umap.controller('UserControllerA',['$scope','UserServiceA','CompanyService','$stateParams','$state','$window', function($scope, UserServiceA,CompanyService, $stateParams,$state,$window) {
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
      UserServiceA.Users.save($scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    };
    $scope.users = UserServiceA.Users.query();
    //$scope.user.name = userOriginal.name;
    //$scope.user.surname = userOriginal.surname;
    //$scope.user.email = userOriginal.loginInfo.providerKey;
    $scope.deleteUser = function(id){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        UserServiceA.Users.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    };
  }]);

  umap.controller('UserControllerDetailsA',['$scope','UserServiceA','$state','$stateParams', function($scope, UserServiceA,$state,$stateParams) {
    $scope.user = UserServiceA.Users.get({ id:  $stateParams.id });
    $scope.user.oldEmail = '';
    $scope.oldEmail = $scope.user.email;
    $scope.editUser = function(){
      console.log($scope.user);
      UserServiceA.Users.update({id:  $stateParams.id}, $scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    }
  }]);
})();
