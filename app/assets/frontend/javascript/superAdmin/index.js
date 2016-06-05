(function(){
  'use strict';

  var umap = angular.module('umap.superAdmin',['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider',function($stateProvider, $urlRouterProvider){
    var $cookies;
    angular.injector(['ngCookies']).invoke(['$cookies', function(_$cookies_) {
      $cookies = _$cookies_;
    }]);
    $stateProvider.state('root.superAdmin',{
      url: 'superAdmin',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/home.html',
              controller:  'SuperAdminController'
            },
            'header@':{
              templateUrl: 'assets/html/superAdmin/header.html'
            }
        },
        resolve: {
          security: ['$q', function($q){
              var role = $cookies.get('Role');
              if(role != 'superAdmin'){
                 return $q.reject("Not Authorized");
              }
          }]
       }
    });
  }]);

  umap.controller('SuperAdminController',['$scope',function($scope){

  }]);
})();
