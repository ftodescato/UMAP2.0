(function(){
  'use strict';

  var umap = angular.module('umap', ['ui.router','umap.company','umap.user']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider','$httpProvider',
  function($stateProvider, $urlRouterProvider,$locationProvider, $httpProvider){
  //  $urlRouterProvider.otherwise('/');

    $stateProvider.state('root', {
    //  url: '/',
      abstract:true,
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
  $httpProvider.interceptors.push(function($q, $injector) {
    return {
      request: function(request) {
        // Add auth token for Silhouette if user is authenticated

          request.headers['Content-Type'] = 'application/json';
          request.headers['Csrf-Token'] = 'nocheck';


        return request;
      },

      responseError: function(rejection) {
        /*
        if (rejection.status === 401) {
          $injector.get('$state').go('signIn');
        }
        return $q.reject(rejection);*/
      }
    };
  });
  }]);
umap.controller('HeaderController',['$scope',function($scope) {

}]);


})();
