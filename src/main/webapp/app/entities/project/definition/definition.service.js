(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('ProjectDefinition', ProjectDefinition);

    ProjectDefinition.$inject = ['$resource'];

    function ProjectDefinition ($resource) {
        var resourceUrl =  'api/project/:projectId/definitions/';

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
