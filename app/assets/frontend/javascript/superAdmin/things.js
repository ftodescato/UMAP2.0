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
    $stateProvider.state('root.superAdmin.addThings', {
      url: '/things/addThing',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/things/addThing.html',
              controller:  'ThingsController'
            }
        }
    });
    $stateProvider.state('root.superAdmin.updateThingType', {
      url: '/things/ThingType/:id',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/things/updateThingType.html',
              controller:  'ThingTypeDetailsController'
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
        }),
        Thing: $resource('/api/thingsSA/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          }
        })
    }
  })

  umap.controller('ThingsTypeController',['$scope','$state','$window','CompanyService','ThingTypeService',function($scope,$state,$window,CompanyService,ThingTypeService){
    CompanyService.query().$promise.then(function(companies){
      $scope.companies = companies;
    });
    ThingTypeService.ThingType.query().$promise.then(function(thingTypes){
      $scope.thingTypes = thingTypes;
      console.log($scope.thingTypes);
    })

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
    };
    $scope.deleteThingType = function(id){
      var deleteThingType = $window.confirm('Sei sicuro ?');
      if(deleteThingType){
        ThingTypeService.ThingType.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    }
  }]);
  umap.controller('ThingTypeDetailsController',['$scope','$state','$stateParams','CompanyService','ThingTypeService', function($scope,$state,$stateParams,CompanyService,ThingTypeService){
    ThingTypeService.ThingType.get({id: $stateParams.id}).$promise.then(function(thingType){
      $scope.thingType = {
        "company": [] ,
        "thingTypeName":''
      }
      $scope.thingType.company = thingType.companyID;
      $scope.thingType.thingTypeName = thingType.thingTypeName;
    });
    CompanyService.query().$promise.then(function(companies){
      $scope.companies = companies;
    });
    $scope.addItem = function (){
      $scope.thingType.company.push('');
    }
    $scope.removeItem = function (index){
      $scope.thingType.company.splice(index,1);
    }
    $scope.updateThingType = function (){
      ThingTypeService.ThingType.update({id: $stateParams.id}, $scope.thingType, function(){
        $state.go('root.superAdmin.things');
      })
    }
  }]);

  umap.controller('ThingsController',['$scope','$state','ThingTypeService','CompanyService',function($scope,$state,ThingTypeService,CompanyService){
    $scope.newThing = {
      'thingName':'',
      'serialNumber':'',
      'description':'',
      'thingTypeID':'',
      'company':''
    };
    CompanyService.query().$promise.then(function(companies){
      $scope.companies = companies;
    });
    ThingTypeService.ThingType.query().$promise.then(function(thingTypes){
      $scope.thingTypes = thingTypes;
    });
    $scope.addThing = function(){
      ThingTypeService.Thing.save($scope.newThing, function(result){
        $state.go('root.superAdmin.things');
      })
    }
  }]);
})();
