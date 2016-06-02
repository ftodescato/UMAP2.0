(function(){
  var umap = angular.module('umap.superAdmin.things',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.superAdmin.things', {
      url: '/things',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/things/index.html',
              controller:  'ThingsTypeController'
            }
        }
    });
    $stateProvider.state('root.superAdmin.addThingsType', {
      url: '/things/addThingType',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/things/addThingType.html',
              controller:  'ThingsTypeController'
            }
        }
    });
  }]);

  umap.factory('ThingTypeService', function($resource){
    return{
        ThingType: $resource('/api/thingTypesSA/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          }
        })
    }
  })

  umap.controller('ThingsTypeController',['$scope','$state','CompanyService','ThingTypeService',function($scope,$state,CompanyService,ThingTypeService){
    CompanyService.query().$promise.then(function(companies){
      $scope.companies = companies;
    });
    $scope.newThingType = {
      "company": [],
      "thingTypeName":'',
      "listQty":[0,0,0],
      "listDoubleValue":[]
    };

    $scope.addItem = function (stringa){
      switch (stringa) {
        case 'Double':
          $scope.newThingType.listQty[0]++;
          $scope.newThingType.listDoubleValue.push('');
          break;
        case 'Company':
          $scope.newThingType.company.push('');
          break;
        default:
      }
    };
    $scope.removeItem = function (stringa, index){
      switch (stringa) {
        case 'Double':
          $scope.newThingType.listQty[0];
          $scope.newThingType.listDoubleValue.splice(index,1);
          break;
        case 'Company':
          $scope.newThingType.company.splice(index,1);
          break;
        default:
      }
    };

    $scope.addThingType = function (){
      ThingTypeService.ThingType.save($scope.newThingType, function(){
        $state.go('root.superAdmin.things');
      })
      //console.log($scope.newThingType);
    }
  }]);
})();
