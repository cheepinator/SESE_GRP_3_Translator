(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Translation', Translation);

    Translation.$inject = ['$resource'];

    function Translation ($resource) {
        var resourceUrl =  'api/translations/:id';

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
