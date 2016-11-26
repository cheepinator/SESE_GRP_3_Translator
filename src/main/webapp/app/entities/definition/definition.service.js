(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Definition', Definition);

    Definition.$inject = ['$resource'];

    function Definition ($resource) {
        var resourceUrl =  'api/definitions/:id';

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
