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
  $stateProvider.state('root.superAdmin.updateUser.updatePsw', {
    url: '/password',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/users/updateUserPassword.html',
            controller:  'UserControllerDetailsSA'
          }
      }
  });
     //$locationProvider.html5Mode(true);
  }]);

  umap.factory('UserServiceSA', function($resource) {
    return{
      Profile: $resource('/api/usersSA/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      }),
      password: $resource('/api/passwordsSA/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      })
    }
  });

// TODO: rifare come per insert user su update
  umap.controller('UserControllerSA',['$scope','UserServiceSA','CompanyService','AccountService','$stateParams','$state','$window', function($scope, UserServiceSA,CompanyService,AccountService, $stateParams,$state,$window) {
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
       'company':'',
       'role': ''
     };
    $scope.addUser = function(){
      UserServiceSA.Profile.save($scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    };
    UserServiceSA.Profile.query().$promise.then(function(users){
      AccountService.query().$promise.then(function(acc){
        $scope.me = acc.userID;
        $scope.users = [];
        for (var i = 0; i < users.length; i++) {
          if(users[i].userID != $scope.me){
            $scope.users.push(users[i])
          }
        }
      });
      //$scope.users = users;
    });
    $scope.deleteUser = function(id){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        UserServiceSA.Profile.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        })
      }
    };
    $scope.predicate = 'surname';
    $scope.reverse = true;
    $scope.order = function(predicate) {
      $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
      $scope.predicate = predicate;
    };
  }]);

  umap.controller('UserControllerDetailsSA',['$scope','UserServiceSA','CompanyService','$state','$stateParams', function($scope, UserServiceSA,CompanyService,$state,$stateParams) {
    $scope.user = UserServiceSA.Profile.get({ id:  $stateParams.id });
    CompanyService.query().$promise.then(function(response){
      $scope.companies = response;
    });
    $scope.editUser = function(){
      UserServiceSA.Profile.update({id:  $stateParams.id}, $scope.user, function(){
        $state.go('root.superAdmin.users')
      });
    }
    $scope.newPassword = {"newPassword":''};
    $scope.newPasswordTwo = {"newPassword":''};
    $scope.errore = '';
    $scope.editPsw = function (){
      if($scope.newPasswordTwo.newPassword !== $scope.newPassword.newPassword){
        $scope.errore = 'errore ! password differenti';
        return;
      }else{
        UserServiceSA.password.update({id: $stateParams.id}, $scope.newPassword, function(){
          console.log($stateParams.id);
          $state.go('root.superAdmin.users')
        });
      }
    }
  }]);
})();
