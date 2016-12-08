(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('ProjectDefinition', ProjectDefinition)
        .factory('DefinitionTranslation', DefinitionTranslation)
        .factory('Definition', Definition);

    ProjectDefinition.$inject = ['$resource'];
    DefinitionTranslation.$inject = ['$resource'];
    Definition.$inject = ['$resource'];

    function ProjectDefinition ($resource) {
        var resourceUrl =  'api/projects/:projectId/definitions/';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': {method: 'PUT'}
        });
    }

    function DefinitionTranslation($resource) {
        var resourceUrl = 'api/definitions/:definitionId/translations';

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
            }
        });
    }

    function Definition($resource) {
        var resourceUrl = 'api/definitions/:id';

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
            }
        });
    }
})();
