(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Release', Release)
        .factory('ReleaseTooltips', ReleaseTooltips)
        .factory('ReleaseProject', ReleaseProject)
        .factory('CurrentRelease', CurrentRelease);

    Release.$inject = ['$resource', 'DateUtils'];
    ReleaseTooltips.$inject = ['dateFilter'];
    ReleaseProject.$inject = ['$resource'];
    CurrentRelease.$inject = ['$resource'];

    function ReleaseProject ($resource) {
        var resourceUrl =  'api//projects/:projectId/releases';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dueDate = DateUtils.convertDateTimeFromServer(data.dueDate);
                    }
                    return data;
                }
            }
        });
    }

    function CurrentRelease ($resource) {
        var resourceUrl =  'api/releases/project/:projectId';

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


    function Release ($resource, DateUtils) {
        var resourceUrl =  'api/releases/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dueDate = DateUtils.convertDateTimeFromServer(data.dueDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }

    function ReleaseTooltips(dateFilter) {
        this.getReleaseTooltip = getReleaseTooltip;

        function getReleaseTooltip(release) {
            var result = "";
            if (release && release.dueDate && release.versionTag !== 'no release') {
                var formattedDate = dateFilter(release.dueDate, 'mediumDate');
                result += "Due: " + formattedDate;
            }
            return result;
        }

        return this;
    }
})();
