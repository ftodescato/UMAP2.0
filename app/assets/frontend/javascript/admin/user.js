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

  umap.controller('UserControllerA',['$scope','UserServiceA','CompanyService','$stateParams','$state','$window', function($scope, UserServiceA,CompanyService, $stateParams,$state,$window) {
    CompanyService.query().$promise.then(function(companies){
      $scope.hash = {}
      for (var i = 0; i < companies.length; i++) {
        $scope.hash[companies[i].companyID] = companies[i].companyName;
      }
      //console.log($scope.hash['17fd5bc4-974e-4e5f-a9bc-e89128197ca2']);
    });
    $scope.companies = CompanyService.query();
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
      $scope.users = users;
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
    }
  }]);
})();
