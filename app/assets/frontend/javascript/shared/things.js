(function(){
  var umap = angular.module('umap.adminUser.things',['ui.router','ngResource']);
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
/*
  umap.factory('GraphicService', function($resource){
    return{
        Graphic: $resource('/api/thingTypes/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          }
      })
    }
  });*/
  umap.controller('ThingsControllerAU', ['$scope','ThingTypeServiceAU',function($scope,ThingTypeServiceAU){
    ThingTypeServiceAU.Thing.query().$promise.then(function(things){
      $scope.things = things;
    });
  }]);
  umap.controller('ThingsControllerDetailsAU', ['$scope','$stateParams', 'ThingTypeServiceAU', function($scope, $stateParams, ThingTypeServiceAU ){
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
  }]);
})();
