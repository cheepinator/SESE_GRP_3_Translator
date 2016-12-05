(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Project', Project)
        .factory('CountTranslations',CountTranslations)
        .factory('DefaultRelease', DefaultRelease)
        .factory('ProjectReleases', ProjectReleases)
        .factory('ProjectTranslations', ProjectTranslations);

    Project.$inject = ['$resource'];
    Language.$inject = ['$resource'];
    Release.$inject = ['$resource'];
    //CurrentRelease.$inject = ['$resource'];
    CountTranslations.$inject = ['$resource'];
    DefaultRelease.$inject = ['$resource'];
    ProjectReleases.$inject = ['$resource'];
    ProjectTranslations.$inject = ['$resource'];

    function Project ($resource) {
        var resourceUrl =  'api/projects/:id';

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

    function Language ($resource) {
        var resourceUrl =  'api/language/:id';

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

    function Release ($resource) {
        var resourceUrl =  'api/releases/:id';

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

    // function CurrentRelease ($resource) {
    //     var resourceUrl =  'api/releases/project/:id';
    //
    //     return $resource(resourceUrl, {}, {
    //         'get': {
    //             method: 'GET',
    //             transformResponse: function (data) {
    //                 if (data) {
    //                     data = angular.fromJson(data);
    //                 }
    //                 return data;
    //             }
    //         }
    //     });
    // }



    function CountTranslations ($resource) {
        var resourceUrl =  'api/releases/counttranslations/:id';

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
        var resourceUrl =  'api/projects/:projectId/releases/default';

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

    function ProjectReleases ($resource) {
        var resourceUrl =  'api/projects/:projectId/releases/';

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

    function ProjectTranslations ($resource) {
        var resourceUrl =  'api/projects/:projectId/translations/';

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
