(function(){
  'use strict';
  var umap = angular.module('umap.superAdmin.engine',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.superAdmin.engine', {
      url: '/engine',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/engine/index.html',
              controller:  'EngineController'
            }
        }
    });
    $stateProvider.state('root.superAdmin.engine.functions', {
      url: '/functions',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/engine/functions.html',
              controller:  'EngineFunctionsController'
            }
        }
    });
}]);
  umap.factory('FunctionsService', function($resource) {
    return {
      Functions: $resource('/api/engine/functions/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      })
    }
  });
  umap.controller('EngineController',['$scope', function($scope){

  }]);
  umap.controller('EngineFunctionsController',['$scope','$state','CompanyService','FunctionsService', function($scope,$state, CompanyService, FunctionsService){
    $scope.info = {
      companyID: '',
      listFunction: []
    }
    $scope.funSelected = [];
    CompanyService.query().$promise.then(function(companies){
      $scope.hash = {}
      for (var i = 0; i < companies.length; i++) {
        $scope.hash[companies[i].companyID] = companies[i];
      }
      $scope.companies = companies;
      $scope.companyInUse = $scope.hash[$scope.info.companyID];

    });
    FunctionsService.Functions.query().$promise.then(function(functions){
      $scope.functions = functions;
    });
    $scope.send = function ( ){
      $scope.info.listFunction = [];
      for (var i = 0; i < $scope.funSelected.length; i++) {
        if($scope.funSelected[i])
          $scope.info.listFunction.push($scope.functions[i].name);
      }
      FunctionsService.Functions.save($scope.info, function(){
        $state.go('root.superAdmin.engine')
      });
      console.log($scope.info);
    };
  }]);
})();
