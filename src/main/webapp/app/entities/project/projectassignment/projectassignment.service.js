(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Projectassignment', Projectassignment);

    Projectassignment.$inject = ['$resource'];

    function Projectassignment ($resource) {
        var resourceUrl =  'api/projectassignments/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
