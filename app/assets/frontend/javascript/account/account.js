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
}]);
  umap.factory('AccountService', function($resource) {
    return $resource('/api/account', {}, {
      update: {method:'PUT'}
    })
  });
  umap.controller('AccountController',['AccountService','CompanyService','$scope','$state',function(AccountService,CompanyService,$scope,$state){
    AccountService.get().$promise.then(function(account){
      CompanyService.get({id: account.company}).$promise.then(function(company){
        $scope.account = account;
        $scope.company = company.companyName;
      });
    });
    $scope.editUser = function(){
      AccountService.update({}, $scope.account, function(){
        $state.go('root')
      });
    }
  }]);
})();
