(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Projectassignment', Projectassignment)
        .factory('ProjectassignmentProject', ProjectassignmentProject);

    Projectassignment.$inject = ['$resource'];
    ProjectassignmentProject.$inject = ['$resource'];

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

    function ProjectassignmentProject ($resource) {
        var resourceUrl =  '/api/project/:projectId/projectassignments/';

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
