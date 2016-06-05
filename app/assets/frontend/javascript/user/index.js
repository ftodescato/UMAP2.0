(function(){
  'use strict';

  var umap = angular.module('umap.user',['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider',function($stateProvider, $urlRouterProvider){
    var $cookies;
    angular.injector(['ngCookies']).invoke(['$cookies', function(_$cookies_) {
      $cookies = _$cookies_;
    }]);
    $stateProvider.state('root.user',{
      url: 'user',
      views: {
            'content@': {
              templateUrl: 'assets/html/user/home.html',
              controller:  'UserController'
            },
            'header@':{
              templateUrl: 'assets/html/user/header.html'
            }
        },
        resolve: {
          security: ['$q', function($q){
              var role = $cookies.get('Role');
              if(role != 'user'){
                 return $q.reject("Not Authorized");
              }
          }]
       }
    });
  }]);

  umap.controller('UserController',['$scope',function($scope){

  }]);
})();
