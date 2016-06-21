(function(){
  'use strict';

  var umap = angular.module('umap', ['ngFlash','ui.router','ngCookies','umap.account','umap.superAdmin','umap.superAdmin.things','umap.superAdmin.company','umap.superAdmin.user','umap.login','umap.admin','umap.admin.user','umap.admin.analisi','umap.adminUser.thingTypes','umap.adminUser.things','umap.user']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider','$httpProvider',
  function($stateProvider, $urlRouterProvider,$locationProvider, $httpProvider){
  $urlRouterProvider.otherwise('/');

    $stateProvider.state('root', {
      url: '/',
      //abstract:true,
      views: {
            'header': {
              templateUrl: 'assets/html/shared/header.html'
              //controller: 'HeaderController'
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

  umap.run(['$rootScope','$state','$cookies','AccountService',function($rootScope,$state,$cookies, AccountService){
    $rootScope.isLoggedIn = function (){
      var token = $cookies.get('X-Auth-Token');
      if(token === undefined)
        return false;
      else
        return true;
    };
    $rootScope.logOut = function (){
      $cookies.remove('X-Auth-Token');
      $state.go('root.login');
    }
    $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams, options){
      var token = $cookies.get('X-Auth-Token');
      var user;
      if( token === undefined && (toState.name !== 'root.login' && toState.name !== 'root.resetPsw')){
        event.preventDefault();
        $state.go('root.login');
        return;
      }
      if(token != undefined){
        AccountService.Profile.get().$promise.then(
        function(account){
          user = account;
          if( user.mailConfirmed === false){
            event.preventDefault();
            $state.go('root.account.psw');
            return;
          }
          if(toState.name === 'root'){
            switch (user.role) {
              case 'superAdmin':
                event.preventDefault();
                $state.go('root.superAdmin');
                break;
              case 'admin':
                event.preventDefault();
                $state.go('root.admin');
                break;
              case 'user':
                event.preventDefault();
                $state.go('root.user');
                break;
              default:
            }
          }
        });
      }

    });
    $rootScope.$on('$stateChangeError', function(e, toState, toParams, fromState, fromParams, error){
    if(error === "Not Authorized"){
        $state.go("root");
      }
    });
  }]);

  umap.factory('InjectHeadersService',['$q','$cookies','$injector','$rootScope','Flash' ,function($q, $cookies,$injector,$rootScope, Flash){
    return{
      'request': function(request) {
        request.headers['Content-Type'] = 'application/json';
        request.headers['Csrf-Token'] = 'nocheck';
        var token = $cookies.get('X-Auth-Token');
        if( token  !== null)
          request.headers['X-Auth-Token'] = token;
        return request;
      },
      responseError: function(rejection){
        if(rejection.status === 401){
          if(rejection.data.message === 'Authentication required'){
            $rootScope.logOut();
            $injector.get('$state').go('root');
          }else{
            var message = '<h2 class="text-center">'+rejection.data.message+'</h2>';
            Flash.create('danger', message);
          }
          console.log(rejection);
        }
      }
    };
  }]);/*
  umap.factory('AuthService',['$cookies',function($cookies){

  }]);
  umap.controller('HeaderController',['$scope','$cookies',function($scope,$cookies) {

  }]);*/


})();
