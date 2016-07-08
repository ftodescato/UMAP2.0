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
    $stateProvider.state('root.resetPsw',{
      url: 'resetPsw',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/recuperoPsw.html',
              controller:  'ResetPswController'
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
      Role: $resource('/api/getrole'),
      Reset: $resource('/api/account/resetPasswords', {},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      })
    }
  }]);
  umap.controller('LoginController',['LoginService','$scope','$cookies','$state',function(LoginService,$scope,$cookies,$state){
    $scope.credentials = {'email':'','password':'','rememberMe':false};
    $scope.login = function (){
      LoginService.Login.save({},$scope.credentials).$promise.then(
        function(success){
          $cookies.put('X-Auth-Token', success.token);
          LoginService.Role.get({}, function(success){
            if(success !== null)
              $cookies.put('Role', success.role);
              $state.go('root');
          });
          //$state.go('root.home');
        }, function(err){
          //console.log(err.message);
        }
      );
    };
  }]);
  umap.controller('ResetPswController',['LoginService','$scope','$state', function(LoginService, $scope, $state){
    $scope.credentials = {
      'email':'',
      'secretString': '',
      'newPassword': ''
    };
    $scope.newPasswordTwo = '';
    $scope.errore = '';
    $scope.editPsw = function (){
      if($scope.newPasswordTwo !== $scope.credentials.newPassword){
        $scope.errore = 'errore ! password differenti';
        return;
      }else{
        LoginService.Reset.save($scope.credentials, function(){
          $state.go('root')
        });
      }
    }
  }]);
})();
