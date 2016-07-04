(function(){
  var umap = angular.module('umap.account',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.account', {
      url: 'account',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/account.html',
              controller:  'AccountController'
            }
        }
  });
    $stateProvider.state('root.account.psw', {
      url: 'account/passwordNew',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/accountPsw.html',
              controller:  'AccountControllerPsw'
            }
        }
  });
    $stateProvider.state('root.account.pswUpdate', {
      url: 'account/passwordUpdate',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/accountPswUpdate.html',
              controller:  'AccountControllerPswUpdate'
            }
        }
  });
}]);
  umap.factory('AccountService', function($resource) {
    return{
      Profile: $resource('/api/account',{},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      }),
      PasswordNew: $resource('/api/account/pswNew ',{},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      }),
      PasswordEdit: $resource('/api/account/pswUpdate',{},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      })
    }
  });
  umap.controller('AccountController',['AccountService','CompanyService','$scope','$state',function(AccountService,CompanyService,$scope,$state){
    AccountService.Profile.get().$promise.then(function(account){
      $scope.account = account;
    });
    $scope.editUser = function(){
      AccountService.Profile.update({}, $scope.account, function(){
        $state.go('root')
      });
    }
  }]);
  umap.controller('AccountControllerPsw',['AccountService','$scope','$state',function(AccountService,$scope,$state){
    $scope.newPasswordTwo = '';
    $scope.errore = '';
    $scope.infos = {
      newPassword : '',
      newSecretString : ''
    }
    $scope.editPsw = function (){
      if($scope.newPasswordTwo !== $scope.infos.newPassword){
        $scope.errore = 'errore ! password differenti';
        return;
      }else{
        AccountService.PasswordNew.save($scope.infos).$promise.then(function(u){
          $state.go('root');
        });
      }
    }
  }]);
  umap.controller('AccountControllerPswUpdate',['AccountService','$scope','$state',function(AccountService,$scope,$state){
    $scope.newPasswordTwo = '';
    $scope.errore = '';
    $scope.infos = {
      newPassword : ''
    }
    $scope.editPsw = function (){
      if($scope.newPasswordTwo !== $scope.infos.newPassword){
        $scope.errore = 'errore ! password differenti';
        return;
      }else{
        AccountService.PasswordEdit.save($scope.infos).$promise.then(function(u){
          $state.go('root');
        });
      }
    }
  }])
})();
