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
      {functionId:'1', functionName:'func uno', selected: false},
      {functionId:'2', functionName:'func due', selected: false},
      {functionId:'3', functionName:'func tre', selected: false}
    ];
    $scope.charts = [
      {chartId:'0', chartName:'chart uno', selected: false},
      {chartId:'1', chartName:'chart due', selected: false},
      {chartId:'2', chartName:'chart tre', selected: false}
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
      chart: [],
      thingTypes: {}
    }
    $scope.test = function(index){
      console.log(index);
      $scope.charts[index].selected = true;
      console.log($scope.final);
    }
  }]);
})();
