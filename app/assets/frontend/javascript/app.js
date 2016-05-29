(function(){
  'use strict';

  var umap = angular.module('umap', ['ui.router','ngCookies','umap.superAdminHome','umap.company','umap.user','umap.login']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider','$httpProvider',
  function($stateProvider, $urlRouterProvider,$locationProvider, $httpProvider){
  //$urlRouterProvider.otherwise('/');

    $stateProvider.state('root', {
      url: '/',
      //abstract:true,
      views: {
            'header': {
              templateUrl: 'assets/html/shared/header.html',
              controller: 'HeaderController'
            },
            'content': {

            },
            'footer': {
              templateUrl: 'assets/html/shared/footer.html'
            //  controller: 'footer/FooterCtrl'
            }
    }
  });
  $httpProvider.interceptors.push('InjectHeadersService');
  }]);

  umap.run(['$rootScope','$state','$cookies',function($rootScope,$state,$cookies){
    $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams, options){
      var token = $cookies.get('X-Auth-Token');
      var role = $cookies.get('Role');

      if( token === undefined && toState.name !== 'root.login'){
        event.preventDefault();
        $state.go('root.login');
        return;
      }
      if( role === undefined && toState.name !== 'root.login' ){
        event.preventDefault();
        $cookies.remove('X-Auth-Token');
        $cookies.remove('Role');
        $state.go('root.login');
        return;
      }
      if(toState.name === 'root'){
        switch (role) {
          case 'superAdmin':
            event.preventDefault();
            $state.go('root.superAdmin');
            break;
          default:

        }
      }
    })
  }]);

  umap.factory('InjectHeadersService',['$q','$cookies','$injector' ,function($q, $cookies,$injector){
    return{
      'request': function(request) {
        request.headers['Content-Type'] = 'application/json';
        request.headers['Csrf-Token'] = 'nocheck';
        var token = $cookies.get('X-Auth-Token');
        if( token  !== null)
          request.headers['X-Auth-Token'] = token;
        return request;
      },/*
      responseError: function(rejection){
        if(rejection.status === '401'){
          $injector.get('$state').go('root.unauthorized');
        }
      }*/
    };
  }]);
  umap.controller('HeaderController',['$scope','$cookies',function($scope,$cookies) {
    //if()
  }]);


})();
