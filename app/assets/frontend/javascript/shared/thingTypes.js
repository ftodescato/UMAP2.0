(function(){
  var umap = angular.module('umap.adminUser.thingTypes',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.admin.thingTypes', {
      url: '/thingTypes',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/thingTypes/index.html',
              controller:  'ThingTypesControllerAU'
            }
        }
    });
    $stateProvider.state('root.user.thingTypes', {
      url: '/thingTypes',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/thingTypes/index.html',
              controller:  'ThingTypesControllerAU'
            }
        }
    });
    $stateProvider.state('root.admin.thingTypesDetails', {
      url: '/thingTypes/:id',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/thingTypes/details.html',
              controller:  'ThingTypesControllerDetailsAU'
            }
        }
    });
    $stateProvider.state('root.user.thingTypesDetails', {
      url: '/thingTypes/:id',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/thingTypes/details.html',
              controller:  'ThingTypesControllerDetailsAU'
            }
        }
    });
  }]);
  umap.factory('ThingTypeServiceAU', function($resource){
    return{
        ThingType: $resource('/api/thingTypes/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          }
      }),
        Thing: $resource('/api/things/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          }
      })
    }
  });
  umap.controller('ThingTypesControllerAU', ['$scope','ThingTypeServiceAU',function($scope,ThingTypeServiceAU){
    ThingTypeServiceAU.ThingType.query().$promise.then(function(thingTypes){
      $scope.thingTypes = thingTypes;
    });
  }]);
  umap.controller('ThingTypesControllerDetailsAU', ['$scope','$stateParams','ThingTypeServiceAU',function($scope,$stateParams,ThingTypeServiceAU){
    ThingTypeServiceAU.ThingType.get({id: $stateParams.id}).$promise.then(function(thingType){
      $scope.thingType = thingType;
    });
  }]);
})();
