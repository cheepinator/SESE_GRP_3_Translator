(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Protocol', Protocol);

    Protocol.$inject = ['$resource', 'DateUtils'];

    function Protocol ($resource, DateUtils) {
        var resourceUrl =  'api/protocollist/1'  // /:id';

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
