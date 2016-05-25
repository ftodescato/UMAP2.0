(function(){
  'use strict';
  var umap = angular.module('umap.company',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('addCompanies', {
      url: '/superAdmin/addCompanies',
      views: {
            'content': {
              templateUrl: 'assets/html/superAdmin/companies.html',
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


  umap.controller('CompanyController',['$scope','CompanyService','$http', function($scope, CompanyService,$http) {
     $scope.company = {'companyName':''};
      $scope.addCompany = function(){
        CompanyService.save($scope.company, function(){

        });
      };
    $scope.addTest = function(){
      $http.post('/api/companies', $scope.company).success(function(){
        console.log('ok');
      });
    }
    $scope.companies = CompanyService.query();
  }]);

  umap.controller('CompanyControllerDetails',['$scope','CompanyService','$http','$stateParams', function($scope, CompanyService,$http,$stateParams) {
    $scope.company = CompanyService.get({ id:  $stateParams.id });
    $scope.editCompany = function(){
      CompanyService.update({id:  $stateParams.id}, $scope.company, function(){

      });
    }
  }]);
})();
