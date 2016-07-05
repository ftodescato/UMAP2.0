(function(){
  var umap = angular.module('umap.adminUser.things',['ui.router','ngResource', 'chart.js']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.admin.things', {
      url: '/things',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/things/index.html',
              controller:  'ThingsControllerAU'
            }
        }
    });
    $stateProvider.state('root.user.things', {
      url: '/things',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/things/index.html',
              controller:  'ThingsControllerAU'
            }
        }
    });
    $stateProvider.state('root.admin.thingDetails', {
      url: '/things/:id',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/things/details.html',
              controller:  'ThingsControllerDetailsAU'
            }
        }
    });
    $stateProvider.state('root.user.thingDetails', {
      url: '/things/:id',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/things/details.html',
              controller:  'ThingsControllerDetailsAU'
            }
        }
    });
  }]);
  umap.factory('GraphicService', function($resource){
    return{
        Graphic: $resource('/api/graphics/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          }
        }),
        Chart: $resource('/api/charts/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          },
          get:{
            method: 'GET',
            isArray: true
          }
        })
    }
  });
  umap.controller('ThingsControllerAU', ['$scope','ThingTypeServiceAU',function($scope,ThingTypeServiceAU){
    ThingTypeServiceAU.Thing.query().$promise.then(function(things){
      $scope.things = things;
    });
  }]);
  umap.controller('ThingsControllerDetailsAU', ['$scope','$stateParams', 'ThingTypeServiceAU','GraphicService', function($scope, $stateParams, ThingTypeServiceAU, GraphicService ){
    $scope.hashMisure = [];
    ThingTypeServiceAU.Thing.get({id: $stateParams.id}).$promise.then(function(thing){
      ThingTypeServiceAU.ThingType.get({id: thing.thingTypeID}).$promise.then(function(thingType){
        $scope.hashVisibility = {};
        for (var i = 0; i < thingType.doubleValue.infos.length; i++) {
          $scope.hashMisure[thingType.doubleValue.infos[i].name] = thingType.doubleValue.infos[i].visible;
        }
        $scope.thing = thing;
      });
    });
    GraphicService.Chart.get({id: $stateParams.id}).$promise.then(function(charts){
      $scope.charts = charts;
    });
    //query su charts per prendermi tutti i chart col mio thingID
    //ciclo questi chart e mi salvo la previsione in un array di previsioni
    $scope.showGraphics = function(){
      $scope.loading = true;
      $scope.graphics = [];
      for (var i = 0; i < $scope.charts.length; i++) {
        var aux = {
          data: [],
          labels: [],
          function: $scope.charts[i].functionName,
          param: $scope.charts[i].infoDataName
        };
        GraphicService.Graphic.get({id: $scope.charts[i].chartID}).$promise.then(function(graphic){
          aux.data.push(graphic.valuesY);
          aux.labels = graphic.valuesX;
          for (var i = 0; i < aux.labels.length; i++) {
            console.log(aux.labels[i]);
            aux.labels[i] = Date.parse(aux.labels[i]);
            console.log(aux.labels[i]);
          }
          $scope.graphics.push(aux);
          if(i === $scope.charts.length)
            $scope.loading = false;
        });
      }
    }
  }]);
})();
