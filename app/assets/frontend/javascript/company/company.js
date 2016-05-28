(function(){
  'use strict';
  var umap = angular.module('umap.company',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.addCompanies', {
      url: '/superAdmin/addCompany',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/companies/addCompany.html',
              controller:  'CompanyController'
            }
        }
  });
  $stateProvider.state('root.companies', {
    url: '/superAdmin/companies',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/companies/index.html',
            controller:  'CompanyController'
          }
      }
  });
  $stateProvider.state('root.updateCompany', {
    url: '/superAdmin/companies/:id',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/companies/updateCompany.html',
            controller:  'CompanyControllerDetails'
          }
      }
  });
  $urlRouterProvider.otherwise('/');


     //$locationProvider.html5Mode(true);
  }]);

  umap.factory('CompanyService', function($resource) {
    return $resource('/api/companiesSA/:id',{id: "@id"},{
      update: {
        method: 'PUT' // this method issues a PUT request
      }
    });
  });


  umap.controller('CompanyController',['$scope','CompanyService','$stateParams','$state', function($scope, CompanyService, $stateParams,$state) {
    $scope.companies = CompanyService.query();
     $scope.company = {'companyName':''};
      $scope.addCompany = function(){
        CompanyService.save($scope.company, function(){
          $state.go('root.companies');
          //$scope.companies = CompanyService.query();
        });
      };
    $scope.deleteCompany = function(id){
      CompanyService.delete({id:  id}, function(){
        $state.go('root.companies')
      });
    };
  }]);

  umap.controller('CompanyControllerDetails',['$scope','CompanyService','$stateParams', function($scope, CompanyService,$stateParams) {
    $scope.company = CompanyService.get({ id:  $stateParams.id });
    $scope.editCompany = function(){
      CompanyService.update({id:  $stateParams.id}, $scope.company, function(){
        $state.go('root.companies')
      });
    }
  }]);
})();
