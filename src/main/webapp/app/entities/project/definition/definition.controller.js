(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionController', ProjectDefinitionController);

    ProjectDefinitionController.$inject = ['$scope', '$state', 'project', 'projectReleases', 'DataUtils',
        'ProjectDefinition', 'ParseLinks', 'AlertService'];

    function ProjectDefinitionController ($scope, $state, project, projectReleases, DataUtils, ProjectDefinition,
                                          ParseLinks, AlertService) {
        var vm = this;

        vm.project = project;
        vm.releases = projectReleases;
        vm.definitions = [];
        vm.loadPage = loadPage;
        vm.page = 0;
        vm.links = {
            last: 0
        };
        vm.predicate = 'id';
        vm.reset = reset;
        vm.reverse = true;
        vm.openFile = DataUtils.openFile;
        vm.byteSize = DataUtils.byteSize;

        loadAll();

        function loadAll () {
            ProjectDefinition.query({
                projectId: vm.project.id,
                page: vm.page,
                size: 20,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                for (var i = 0; i < data.length; i++) {
                    // map releases to the definitions
                    var definition = data[i];
                    definition.release = vm.releases.find(function (release) {
                        return definition.releaseId == release.id;
                    });
                    vm.definitions.push(definition);
                }
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function reset () {
            vm.page = 0;
            vm.definitions = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }
    }
})();
