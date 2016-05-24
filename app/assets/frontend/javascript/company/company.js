(function(){
  'use strict';
  var umap = angular.module('umap.company',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('companies', {
      url: '/superAdmin/addCompanies',
      views: {
            'content': {
              templateUrl: 'assets/html/superAdmin/companies.html',
              controller:  'CompanyController'
            }
        }
  });
  $urlRouterProvider.otherwise('/');


     //$locationProvider.html5Mode(true);
  }]);

    umap.factory('CompanyService', function($resource) {
      return $resource('/api/companies/:id'); // Note the full endpoint address
    });

  umap.controller('CompanyController',['$scope','CompanyService', function($scope, CompanyService) {
     $scope.company = {'name':''};
      $scope.addCompany = function (){
        CompanyService.save();
      };
  }]);
})();
