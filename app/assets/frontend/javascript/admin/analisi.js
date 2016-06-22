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
      Analisi: $resource('/api/usersA/:id',{id: "@id"},{// da rifare
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      })
    }
  });

  umap.controller('AnalisiController',['$scope','$state','AnalisiService', function($scope, $state, AnalisiService){
    //$scope.item = { testo:'stocazzo' } ;
    $scope.drop;
    $scope.functions = [
      {functionId:'1', functionName:'func uno'},
      {functionId:'2', functionName:'func due'},
      {functionId:'3', functionName:'func tre'}
    ];
    $scope.charts = [
      {chartId:'1', chartName:'chart uno'},
      {chartId:'2', chartName:'chart due'},
      {chartId:'3', chartName:'chart tre'}
    ];
    //['func uno','func due', 'func tre'];
  //  $scope.charts = ['chart uno','chart due', 'chart tre'];
    $scope.thingTypes = ['type uno','type due', 'type tre'];
  //  $scope.functionsHash = {}
  //  for (var i = 0; i < $scope.functions.length; i++) {
  //    $scope.functionsHash[$scope.functions[i].functionId] = $scope.functions[i].functionName;
  //  }
    $scope.final = {
      fun: {},
      chart: {},
      thingTypes: {}
    }
    $scope.test = function(stuff, stuff2, obj){
      console.log('$scope.drop = '+$scope.drop);
    }
  }]);
})();
