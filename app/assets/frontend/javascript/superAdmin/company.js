(function(){
  'use strict';
  var umap = angular.module('umap.superAdmin.company',['ui.router','ngResource']);
  umap.config(['$stateProvider','$urlRouterProvider','$locationProvider',function($stateProvider, $urlRouterProvider,$locationProvider){
    $stateProvider.state('root.superAdmin.addCompanies', {
      url: '/addCompany',
      views: {
            'content@': {
              templateUrl: 'assets/html/superAdmin/companies/addCompany.html',
              controller:  'CompanyController'
            }
        }
  });
  $stateProvider.state('root.superAdmin.companies', {
    url: '/companies',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/companies/index.html',
            controller:  'CompanyController'
          }
      }
  });
  $stateProvider.state('root.superAdmin.updateCompany', {
    url: '/companies/:id',
    views: {
          'content@': {
            templateUrl: 'assets/html/superAdmin/companies/updateCompany.html',
            controller:  'CompanyControllerDetails'
          }
      }
  });


     //$locationProvider.html5Mode(true);
  }]);

  umap.factory('CompanyService', function($resource) {
    return $resource('/api/companiesSA/:id',{id: "@id"},{
      update: {
        method: 'PUT' // this method issues a PUT request
      }
    });
  });


  umap.controller('CompanyController',['$scope','CompanyService','$stateParams','$state','$window', function($scope, CompanyService, $stateParams,$state,$window) {
    $scope.companies = CompanyService.query();
    $scope.company = {'companyName':''};
    $scope.addCompany = function(){
      CompanyService.save($scope.company, function(){
        $state.go('root.superAdmin.companies');
        //$scope.companies = CompanyService.query();
      });
    };
    $scope.deleteCompany = function(id){
      var deleteUser = $window.confirm('Sei sicuro ?');
      if(deleteUser){
        CompanyService.delete({id:  id}, function(){
          $state.go($state.current, {}, {reload: true});
        });
      }
    };
    $scope.predicate = 'companyID';
    $scope.reverse = true;
    $scope.order = function(predicate) {
      $scope.reverse = ($scope.predicate === predicate) ? !$scope.reverse : false;
      $scope.predicate = predicate;
    };
  }]);

  umap.controller('CompanyControllerDetails',['$scope','CompanyService','$stateParams','$state', function($scope, CompanyService,$stateParams,$state) {
    $scope.company = CompanyService.get({ id:  $stateParams.id });
    $scope.editCompany = function(){
      CompanyService.update({id:  $stateParams.id}, $scope.company, function(){
        $state.go('root.superAdmin.companies')
      });
    }
  }]);
})();
