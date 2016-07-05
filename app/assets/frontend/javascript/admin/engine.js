(function(){
  'use strict';
  var umap = angular.module('umap.admin.engine',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.admin.engine', {
      url: '/engine',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/engine/functions.html',
              controller:  'EngineFunctionsAController'
            }
        }
    });
}]);
umap.factory('MyCompanyService', function($resource) {
  return  $resource('/api/getMyCompany/:id',{id: "@id", isArray: false},{
    query: {
          method: 'GET',
          isArray: false
        }
    })
});

  umap.controller('EngineFunctionsAController',['$scope','$state','FunctionsService','MyCompanyService', function($scope,$state, FunctionsService, MyCompanyService){
    $scope.info = {
      listFunction: []
    };
    $scope.ok = false;
    $scope.infoC = [];
    MyCompanyService.query().$promise.then(function(company){
      $scope.infoC = company.functionAlgList;
    });

    FunctionsService.Functions.query().$promise.then(function(functions){
      for (var j = 0; j < functions.length; j++) {
        if($scope.infoC.indexOf(functions[j].name) != -1)
          $scope.info.listFunction.push({name:functions[j].name, inUse: true});
        else
          $scope.info.listFunction.push({name:functions[j].name, inUse: false});
      }
      $scope.ok = true;
    });
    $scope.send = function ( ){
      $scope.payload = {
        listFunction : []
      };
      for (var i = 0; i < $scope.info.listFunction.length; i++) {
        if($scope.info.listFunction[i].inUse)
          $scope.payload.listFunction.push($scope.info.listFunction[i].name);
      }
      FunctionsService.Admin.save($scope.payload, function(){
        $state.go('root.admin')
      });
    };
  }]);
})();
