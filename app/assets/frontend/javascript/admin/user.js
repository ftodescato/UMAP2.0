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
      Profile: $resource('/api/usersA/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      })
    }
  });

  umap.controller('UserControllerA',['$scope','UserServiceA','AccountService','$stateParams','$state','$window', function($scope, UserServiceA,AccountService, $stateParams,$state,$window) {
    $scope.user = {
       'name': '',
       'surname':'',
       'email':'',
       'password':'',
       'role': ''
     };
    //$scope.company = UserService.Identity.get();
    $scope.addUser = function(){
      UserServiceA.Profile.save($scope.user, function(){
        $state.go('root.admin.users')
      });
    };
    UserServiceA.Profile.query().$promise.then(function(users){
      AccountService.Profile.get().$promise.then(function(acc){
        $scope.me = acc.userID;
        $scope.users = [];
        for (var i = 0; i < users.length; i++) {
          if(users[i].userID != $scope.me && users[i].role != 'superAdmin'){
            $scope.users.push(users[i])
          }
        }
      });
      //$scope.users = users;
    });
    $scope.deleteUser = function(id){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        UserServiceA.Profile.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    };
    $scope.predicate = 'surname';
    $scope.reverse = true;
    $scope.order = function(predicate) {
      $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
      $scope.predicate = predicate;
    };
  }]);

  umap.controller('UserControllerDetailsA',['$scope','UserServiceA','$state','$stateParams', function($scope, UserServiceA,$state,$stateParams) {
    $scope.user = UserServiceA.Profile.get({ id:  $stateParams.id });
    $scope.editUser = function(){
      UserServiceA.Profile.update({id:  $stateParams.id}, $scope.user, function(){
        $state.go('root.admin.users')
      });
    };
  }]);
})();
