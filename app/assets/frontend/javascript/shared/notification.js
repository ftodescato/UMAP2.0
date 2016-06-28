(function(){
  "use strict";
  var umap = angular.module('umap.adminUser.notifications',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.admin.notifications', {
      url: '/notifications',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/notifications/index.html',
              controller:  'NotificationController'
            }
        }
    });
    $stateProvider.state('root.user.notifications', {
      url: '/notifications',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/notifications/index.html',
              controller:  'NotificationController'
            }
        }
    });
    $stateProvider.state('root.admin.notifications.addNotification', {
      url: '/addNotification',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/notifications/addNotification.html',
              controller:  'AddNotificationController'
            }
        }
    });
    $stateProvider.state('root.user.notifications.addNotification', {
      url: '/addNotification',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/notifications/addNotification.html',
              controller:  'AddNotificationController'
            }
        }
    });
    $stateProvider.state('root.admin.notifications.updateNotification', {
      url: '/notification/:id',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/notifications/updateNotification.html',
              controller:  'UpdateNotificationController'
            }
        }
    });
    $stateProvider.state('root.user.notifications.updateNotification', {
      url: '/notification/:id',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/notifications/updateNotification.html',
              controller:  'UpdateNotificationController'
            }
        }
    });
  }]);

  umap.factory('NotificationService', function($resource){
    return{
        Notification: $resource('/api/notifications/:id',{id: "@id"},{
          update:{
            method: 'PUT'
          }
      })
    }
  });
  umap.controller('NotificationController',['$state', '$scope','$window','NotificationService'  ,function($state, $scope,$window, NotificationService){
    $scope.notificationThingType = [];
    $scope.notificationThing = [];
    NotificationService.Notification.query().$promise.then(function(notifications){
      for (var i = 0; i < notifications.length; i++) {
        if(notifications[i].thingTypeID)
          $scope.notificationThingType.push(notifications[i]);
        else
          $scope.notificationThing.push(notifications[i]);
      }
    });
    $scope.predicate = 'notificationID';
    $scope.reverse = true;
    $scope.order = function(predicate) {
      $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
      $scope.predicate = predicate;
    };
    $scope.deleteNotification = function(id){
      var deleteNot = $window.confirm('Sei sicuro ?');
      if(deleteNot){
        NotificationService.Notification.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    }
  }]);
  umap.controller('AddNotificationController',['$state', '$scope','$stateParams','NotificationService','ThingTypeServiceAU', function($state, $scope, $stateParams, NotificationService, ThingTypeServiceAU){
    $scope.which = "Modello";
    $scope.info = {};
    $scope.parameterSelected = '';
    //$scope.parameterSelected2 = '';
    $scope.idModelloselected = '';
    $scope.idOggettoSelected = '';
    ThingTypeServiceAU.ThingType.query().$promise.then(function(thingTypes){
      $scope.thingTypesHash = {};
      $scope.availableParametersHash = {};
      for (var i = 0; i < thingTypes.length; i++) {
        $scope.thingTypesHash[thingTypes[i].thingTypeID] = thingTypes[i];
        $scope.availableParametersHash[thingTypes[i].thingTypeID] = [];
        for (var j = 0; j < thingTypes[i].doubleValue.infos.length; j++) {
          if(thingTypes[i].doubleValue.infos[j].visible)
            $scope.availableParametersHash[thingTypes[i].thingTypeID].push( thingTypes[i].doubleValue.infos[j].name);
        }
      }
    });
    ThingTypeServiceAU.Thing.query().$promise.then(function(things){
      $scope.thingsHash = {};
      for (var i = 0; i < things.length; i++) {
        $scope.thingsHash[things[i].thingID] = things[i];
      }
    });
    $scope.send = function(user){
      var infos = {
        description: $scope.info.description,
        objectID: '',
        modelOrThing: $scope.which,
        parameter: $scope.parameterSelected,
        minValue: $scope.info.minValue,
        maxValue: $scope.info.maxValue,
        isThing: true
      }
      if($scope.which === 'Oggetto'){
        infos.objectID = $scope.idOggettoSelected;
        infos.isThing = true;
      }else {
        infos.objectID = $scope.idModelloselected;
        infos.isThing = false;
      }

      NotificationService.Notification.save(infos).$promise.then(function(d){
        if(user)
          $state.go('root.user.notifications');
        else
          $state.go('root.admin.notifications');
      });
    }
  }]);
  umap.controller('UpdateNotificationController',['$scope', '$state','$stateParams', 'NotificationService', function($scope, $state, $stateParams,NotificationService){
    NotificationService.Notification.get({id: $stateParams.id}).$promise.then(function(result){
      $scope.notification = result;
    })
    $scope.editNotification = function(admin){
      NotificationService.Notification.update({id: $stateParams.id}, $scope.notification).$promise.then(function(result){
        if(admin)
          $state.go("root.admin.notifications");
        else
          $state.go("root.user.notifications");
      });
    }
  }]);
})();
