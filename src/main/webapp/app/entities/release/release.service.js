(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('Release', Release)
        .factory('ReleaseTooltips', ReleaseTooltips);

    Release.$inject = ['$resource', 'DateUtils'];
    ReleaseTooltips.$inject = ['dateFilter'];

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
            if (release && release.dueDate) {
                var formattedDate = dateFilter(release.dueDate, 'mediumDate');
                result += "Due: " + formattedDate;
            }
            return result;
        }

        return this;
    }
})();
