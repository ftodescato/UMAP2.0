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
    $stateProvider.state('root.superAdmin.engine.parameters', {
      url: '/parameters',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/engine/parameters.html',
              controller:  'EngineParametersController'
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
      }),
      Admin: $resource('/api/engineA/functions/:id', {id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      }),
      Parameters: $resource('/api/thingTypeVisibility', {id: "@id"},{
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
    $scope.infoC = [];
    $scope.selected ;
    CompanyService.query().$promise.then(function(companies){
      $scope.hash = {}
      for (var i = 0; i < companies.length; i++) {
        $scope.infoC.push({companyID: companies[i].companyID, functions:[]});
        $scope.hash[companies[i].companyID] = companies[i];
      }
      $scope.companies = companies;
    });
    FunctionsService.Functions.query().$promise.then(function(functions){
      $scope.functions = functions
      for (var i = 0; i < $scope.infoC.length; i++) {// per ogni company
        for (var j = 0; j < functions.length; j++) {
          if($scope.hash[$scope.infoC[i].companyID].functionAlgList.indexOf(functions[j].name) != -1)
            $scope.infoC[i].functions.push({name:functions[j].name, inUse: true});
          else
            $scope.infoC[i].functions.push({name:functions[j].name, inUse: false});
        }
      }
    });
    $scope.send = function ( ){
      $scope.info = {
        companyID : $scope.infoC[$scope.selected].companyID,
        listFunction : []
      };
      for (var i = 0; i < $scope.infoC[$scope.selected].functions.length; i++) {
        if($scope.infoC[$scope.selected].functions[i].inUse)
          $scope.info.listFunction.push($scope.infoC[$scope.selected].functions[i].name);
      }
      FunctionsService.Functions.save($scope.info, function(){
        $state.go('root.superAdmin.engine')
      });
    };
  }]);


  umap.controller('EngineParametersController',['$scope','$state', 'FunctionsService','ThingTypeService', function($scope, $state, FunctionsService, ThingTypeService){
    $scope.selected = '';
    ThingTypeService.ThingType.query().$promise.then(function(thingTypes){
      $scope.thingTypesHash = {}
      for (var i = 0; i < thingTypes.length; i++) {
        $scope.thingTypesHash[thingTypes[i].thingTypeID] = thingTypes[i];
      }
      $scope.thingTypes = thingTypes;
    });
    $scope.log = function(){
      $scope.info = {
        thingTypeID: '',
        listData: []
      }
      $scope.info.thingTypeID = $scope.selected;
      for (var i = 0; i < $scope.thingTypesHash[$scope.selected].doubleValue.infos.length; i++) {
        if($scope.thingTypesHash[$scope.selected].doubleValue.infos[i].visible)
          $scope.info.listData.push($scope.thingTypesHash[$scope.selected].doubleValue.infos[i].name)
      }
      console.log($scope.info);
      FunctionsService.Parameters.save($scope.info, function(){
        $state.go('root.superAdmin.engine')
      });
    }
  }]);

})();