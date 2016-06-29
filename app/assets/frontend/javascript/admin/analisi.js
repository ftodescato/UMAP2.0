(function(){
  'use strict';
  var umap = angular.module('umap.admin.analisi',['ui.router','ngResource','ngDragDrop']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.admin.analisi', {
      url: '/analisi',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/analisi/index.html',
              controller:  'AnalisiController'
            }
        }
      });
  }]);
  umap.factory('AnalisiService', function($resource) {
    return{
      Analisi: $resource('/api/charts/:id',{id: "@id"},{// da rifare
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      }),
      Things: $resource('/api/things/:id',{id: "@id"},{// da rifare
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      })
    }
  });

  umap.controller('AnalisiController',['$scope','$state','AnalisiService','MyCompanyService','ThingTypeServiceAU','Flash', function($scope, $state, AnalisiService, MyCompanyService, ThingTypeServiceAU, Flash){
    //$scope.item = { testo:'stocazzo' } ;
    $scope.drop;
    $scope.errore = '';
    MyCompanyService.query().$promise.then(function(company){
      $scope.functions = company.functionAlgList;
    });
    AnalisiService.Things.query().$promise.then(function(things){
      $scope.thingsHash = {};
      for (var i = 0; i < things.length; i++) {
        $scope.thingsHash[things[i].thingID] = things[i];
      }
    });
ThingTypeServiceAU.ThingType.query().$promise.then(function(thingTypes){
  $scope.thingTypeHash = {};
    for (var i = 0; i < thingTypes.length; i++) {
      $scope.thingTypeHash[thingTypes[i].thingTypeID] = thingTypes[i];
    }
})
    $scope.final = {
      fun: '',
      thingID: '',
      par: ''
    }
    $scope.dropped = function(){
      $scope.final.par = '';
    }
    $scope.test = function(){
      var aux = {
        functionName: $scope.final.fun,
        objectID: $scope.final.thingID.thingID,
        parameter: $scope.final.par.name
      }
      if((!aux.functionName || !aux.objectID || !aux.parameter) )
        //$scope.errore = 'completa tutti i campi !'
        Flash.create('danger', '<h2 class="text-center"> completa tutti i campi</h2>');
      else{
        AnalisiService.Analisi.save(aux, function(result){
          $state.go('root.admin');
        })
      }
    }
  }]);
})();
