(function(){
  'use strict';

  var umap = angular.module('umap', ['ui.router']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('home', {
      url: '/',
      //abstract:true,
      views: {
            'header': {
              //templateUrl: 'assets/html/shared/header.html'
              controller: 'header/HeaderCtrl'
            },
            'content': {
              templateUrl: 'assets/html/index.html'
            },
            'footer': {
              templateUrl: 'assets/html/shared/footer.html'
            //  controller: 'footer/FooterCtrl'
            }
    }
  })
    $urlRouterProvider.otherwise('/');
     //$locationProvider.html5Mode(true);
  }]);
})();
