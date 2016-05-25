(function(){
  'use strict';
  var umap = angular.module('umap.company',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('addCompanies', {
      url: '/superAdmin/addCompany',
      views: {
            'content': {
              templateUrl: 'assets/html/superAdmin/companies/addCompany.html',
              controller:  'CompanyController'
            }
        }
  });
  $stateProvider.state('companies', {
    url: '/superAdmin/companies',
    views: {
          'content': {
            templateUrl: 'assets/html/superAdmin/companies/index.html',
            controller:  'CompanyController'
          }
      }
});
$stateProvider.state('updateCompany', {
  url: '/superAdmin/companies/:id',
  views: {
        'content': {
          templateUrl: 'assets/html/superAdmin/companies/updateCompany.html',
          controller:  'CompanyControllerDetails'
        }
    }
});
  $urlRouterProvider.otherwise('/');


     //$locationProvider.html5Mode(true);
  }]);

    umap.factory('CompanyService', function($resource) {
      return $resource('/api/companies/:id',{id: "@id"},{
        update: {
          method: 'PUT' // this method issues a PUT request
        }
      });
    });


  umap.controller('CompanyController',['$scope','CompanyService','$http','$stateParams','$location', function($scope, CompanyService,$http, $stateParams,$location) {
     $scope.company = {'companyName':''};
      $scope.addCompany = function(){
        CompanyService.save($scope.company, function(){
          $location.path('/superAdmin/companies')
        });
      };
    $scope.companies = CompanyService.query();
    $scope.deleteCompany = function(param){
      CompanyService.delete({id:  param}, function(){
        $location.path('/superAdmin/companies')
      });
    };
  }]);

  umap.controller('CompanyControllerDetails',['$scope','CompanyService','$http','$stateParams', function($scope, CompanyService,$http,$stateParams) {
    $scope.company = CompanyService.get({ id:  $stateParams.id });
    $scope.editCompany = function(){
      CompanyService.update({id:  $stateParams.id}, $scope.company, function(){
        $location.path('/superAdmin/companies')
      });
    }
  }]);
})();
