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
    $scope.predicate = 'thingID';
    $scope.reverse = true;
    $scope.order = function(predicate) {
      $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
      $scope.predicate = predicate;
    };
    ThingTypeServiceAU.Thing.query().$promise.then(function(things){
      $scope.things = things;
    });
  }]);
  umap.controller('ThingsControllerDetailsAU', ['$scope','$stateParams','$state','$window' ,'ThingTypeServiceAU','GraphicService', function($scope, $stateParams, $state, $window, ThingTypeServiceAU, GraphicService ){
    $scope.hashMisure = [];
    $scope.predicate = 'dataTime';
    $scope.reverse = true;
    $scope.order = function(predicate) {
      $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
      $scope.predicate = predicate;
    };
    ThingTypeServiceAU.Thing.get({id: $stateParams.id}).$promise.then(function(thing){
      ThingTypeServiceAU.ThingType.get({id: thing.thingTypeID}).$promise.then(function(thingType){
        $scope.hashVisibility = {};
        for (var i = 0; i < thingType.doubleValue.infos.length; i++) {
          $scope.hashMisure[thingType.doubleValue.infos[i].name] = thingType.doubleValue.infos[i].visible;
        }
        $scope.thing = thing;
      });
    });
    $scope.loadings = {};
    GraphicService.Chart.get({id: $stateParams.id}).$promise.then(function(charts){
      $scope.charts = charts;
      for (var i = 0; i < $scope.charts.length; i++) {
        $scope.loadings[$scope.charts[i].chartID] = true;
      }
    });
    //query su charts per prendermi tutti i chart col mio thingID
    //ciclo questi chart e mi salvo la previsione in un array di previsioni
    $scope.deleteChart = function(id){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        GraphicService.Chart.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    };
    $scope.graphics = {};
    $scope.showGraphics = function(id){
      $scope.clicked = {};
      $scope.future = false;
      $scope.clicked[id] = ' Caricamento ... ';;
      var aux = {
        data: [],
        labels: [],
        isFuture: false,
        result: 0
      };
      GraphicService.Graphic.get({id: id}).$promise.then(function(graphic){
        aux.data.push(graphic.valuesY);
        aux.labels = graphic.valuesX;
        aux.result = graphic.resultFunction;
        if(!graphic.futureV){
          var lastItem = aux.labels.length - 1;
          aux.labels.splice(lastItem,1);
        }else{
          aux.data[0].push(graphic.resultFunction);
          $scope.future = true;
        }
        for (var i = 0; i < aux.labels.length; i++) {
          aux.labels[i] = new Date(aux.labels[i]);
          aux.labels[i] = aux.labels[i].toDateString();
        }
        aux.isFuture = graphic.futureV;
        console.log(aux);
        $scope.graphics[id] = aux;
        $scope.loadings[id] = false;
        $scope.clicked[id] = '';
      });
    }
  }]);
})();
