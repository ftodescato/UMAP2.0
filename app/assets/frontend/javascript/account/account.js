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
      url: 'account/password',
      views: {
            'content@': {
              templateUrl: 'assets/html/shared/accountPsw.html',
              controller:  'AccountControllerPsw'
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
      Password: $resource('/api/account/psw',{},{
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
      newsecretString : ''
    }
    $scope.editPsw = function (){
      if($scope.newPasswordTwo !== $scope.infos.newPassword){
        $scope.errore = 'errore ! password differenti';
        return;
      }else{
        AccountService.Password.update({}, $scope.infos, function(){
          $state.go('root',{reload: true});
        });
      }
    }
  }])
})();
