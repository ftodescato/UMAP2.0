(function(){
  'use strict';

  var umap = angular.module('umap', ['ui.router','umap.company']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider','$httpProvider',
  function($stateProvider, $urlRouterProvider,$locationProvider, $httpProvider){
  //  $urlRouterProvider.otherwise('/');

    $stateProvider.state('home', {
      url: '/',
      //abstract:true,
      views: {
            'header': {
              templateUrl: 'assets/html/shared/header.html'
              //controller: 'header/HeaderCtrl'
            },
            'content': {
              templateUrl: 'assets/html/shared/index.html',
              controller: 'AppController'
            },
            'footer': {
              templateUrl: 'assets/html/shared/footer.html'
            //  controller: 'footer/FooterCtrl'
            }
    }
  });/*
  $httpProvider.interceptors.push(function($q, $injector) {
     return {
       request: function(request) {
         // Add auth token for Silhouette if user is authenticated
         var $auth = $injector.get('$auth');
         if ($auth.isAuthenticated()) {
           request.headers['X-Auth-Token'] = $auth.getToken();
         }

         // Add CSRF token for the Play CSRF filter
         var cookies = $injector.get('$cookies');
         var token = cookies.get('PLAY_CSRF_TOKEN');
         if (token) {
           // Play looks for a token with the name Csrf-Token
           // https://www.playframework.com/documentation/2.4.x/ScalaCsrf
           request.headers['Csrf-Token'] = token;
         }

         return request;
       },

       responseError: function(rejection) {
         if (rejection.status === 401) {
           $injector.get('$state').go('signIn');
         }
         return $q.reject(rejection);
       }
     }});
     //$locationProvider.html5Mode(true);
     // Auth config
    $authProvider.httpInterceptor = true; // Add Authorization header to HTTP request
    $authProvider.loginOnSignup = true;
    $authProvider.loginRedirect = '/home';
    $authProvider.logoutRedirect = '/';
    $authProvider.signupRedirect = '/home';
    $authProvider.loginUrl = '/signIn';
    $authProvider.signupUrl = '/signUp';
    $authProvider.loginRoute = '/signIn';
    $authProvider.signupRoute = '/signUp';
    $authProvider.tokenName = 'token';
    $authProvider.tokenPrefix = 'satellizer'; // Local Storage name prefix
    $authProvider.authHeader = 'X-Auth-Token';
    $authProvider.platform = 'browser';
    $authProvider.storage = 'localStorage'; */
  }]);
umap.controller('AppController',['$scope',function($scope) {
  // body...
}]);


})();
