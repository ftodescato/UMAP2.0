(function(){
  var umap = angular.module('umap.account',['ui.router','ngResource']);

  umap.factory('AccountService', function($resource) {
    return $resource('/api/account', {}, {
      query: {method:'GET', isArray:false}
    })
  });
})();
