(function () {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Project', Project)
        .factory('CountTranslations', CountTranslations)
        .factory('DefaultRelease', DefaultRelease)
        .factory('ProjectReleases', ProjectReleases)
        .factory('ProjectTranslations', ProjectTranslations)
        .factory('ProjectRoles', ProjectRoles)
        .factory('NewProjectLanguage', NewProjectLanguage)
        .factory('ProjectDetails', ProjectDetails)
        .factory('ProjectProgress', ProjectProgress);

    Project.$inject = ['$resource'];
    Language.$inject = ['$resource'];
    Release.$inject = ['$resource'];
    ProjectDetails.$inject = ['$resource'];
    CountTranslations.$inject = ['$resource'];
    DefaultRelease.$inject = ['$resource'];
    ProjectReleases.$inject = ['$resource'];
    ProjectTranslations.$inject = ['$resource'];
    ProjectRoles.$inject = ['$resource'];
    NewProjectLanguage.$inject = ['$resource'];
    ProjectProgress.$inject = ['$resource'];


    function Project($resource) {
        var resourceUrl = 'api/projects/:id';

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

    function Language($resource) {
        var resourceUrl = 'api/language/:id';

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

    function Release($resource) {
        var resourceUrl = 'api/releases/:id';

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

    function ProjectDetails($resource) {
        var resourceUrl = 'api/releases/projects';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true}
        });
    }


    function CountTranslations($resource) {
        var resourceUrl = 'api/releases/counttranslations/:id';

        return $resource(resourceUrl, {}, {
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


    function DefaultRelease($resource) {
        var resourceUrl = 'api/projects/:projectId/releases/default';

        return $resource(resourceUrl, {}, {
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

    function ProjectReleases($resource) {
        var resourceUrl = 'api/projects/:projectId/releases/';

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
            }
        });
    }

    function ProjectTranslations($resource) {
        var resourceUrl = 'api/projects/:projectId/translations/';

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
            }
        });
    }

    function ProjectRoles($resource) {
        var resourceUrl = 'api/projects/:projectId/userRoles/';
        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true}
        });
    }

    function NewProjectLanguage($resource) {
        var resourceUrl = 'api/projects/:projectId/languages/:languageId';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true}
        });
    }

    function ProjectProgress($resource) {
        var resourceUrl = 'api/projects/:projectId/languages-progress';

        return $resource(resourceUrl, {}, {});
    }


})();
