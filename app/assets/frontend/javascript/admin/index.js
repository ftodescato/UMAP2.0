(function(){
  'use strict';

  var umap = angular.module('umap.admin',['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider',function($stateProvider, $urlRouterProvider){
    var $cookies;
    angular.injector(['ngCookies']).invoke(['$cookies', function(_$cookies_) {
      $cookies = _$cookies_;
    }]);
    $stateProvider.state('root.admin',{
      url: 'admin',
      views: {
            'content@': {
              templateUrl: 'assets/html/admin/home.html',
              controller:  'AdminController'
            },
            'header@':{
              templateUrl: 'assets/html/admin/header.html'
            }
        },
        resolve: {
          security: ['$q', function($q){
              var role = $cookies.get('Role');
              if(role != 'admin'){
                 return $q.reject("Not Authorized");
              }
          }]
       }
    });
  }]);

  umap.controller('AdminController',['$scope',function($scope){

  }]);
})();
