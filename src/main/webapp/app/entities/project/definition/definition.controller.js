(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionController', ProjectDefinitionController);

    ProjectDefinitionController.$inject = ['$scope', '$state', '$location', 'project', 'projectReleases', 'DataUtils',
        'ProjectDefinition', 'ParseLinks', 'AlertService', 'ReleaseTooltips', 'ProjectTranslations', 'Principal', 'ProjectRoles'];

    function ProjectDefinitionController($scope, $state, $location, project, projectReleases, DataUtils, ProjectDefinition,
                                         ParseLinks, AlertService, ReleaseTooltips, ProjectTranslations, Principal, ProjectRoles) {
        var vm = this;

        vm.baseUrl = "http://" + $location.$$host + ":" + $location.$$port;

        vm.project = project;
        vm.releases = projectReleases;
        vm.selectedRelease = [];
        vm.filterBy = '';
        vm.definitions = [];
        vm.translations = [];
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

        vm.getReleaseTooltip = ReleaseTooltips.getReleaseTooltip;
        vm.getLanguageCode = getLanguageCode;
        vm.getTranslations = getTranslations;
        vm.isDeveloper = isDeveloper;
        vm.setSelectedRelease = setSelectedRelease;
        vm.getReleaseName = getReleaseName;
        loadAll();
        getAccount();
        setInitialFilteringRelease();

        function setInitialFilteringRelease() {
            vm.selectedRelease = "";
            setSelectedRelease();
        }

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        ProjectRoles.query({projectId: vm.project.id}, function (response) {
            vm.roles = response;
        });

        function isDeveloper() {
            return vm.roles && vm.roles.includes('DEVELOPER');
        }

        function setSelectedRelease() {
            if (vm.selectedRelease != null) {
                vm.filterBy = vm.selectedRelease.versionTag;
            } else {
                vm.filterBy = '';
            }

        }

        function loadAll() {
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


            ProjectTranslations.query({projectId: vm.project.id}, onTranslationsSuccess, onError);

            function onTranslationsSuccess(data, headers) {
                for (var i = 0; i < data.length; i++) {
                    vm.translations.push(data[i]);
                }
            }

        }

        function getLanguageCode(definition, languageId) {
            if (definition.release) {
                var language = definition.release.languages.find(function (language) {
                    return language.id == languageId;
                });
                if (language) {
                    return language.code;
                }
            }
            return "";
        }

        function getTranslations(definition) {
            return vm.translations.filter(function (translation) {
                return translation.definitionId == definition.id;
            });
        }

        function reset() {
            vm.page = 0;
            vm.definitions = [];
            vm.translations = [];
            loadAll();
        }

        function loadPage(page) {
            vm.page = page;
            loadAll();
        }

        function getReleaseName(releaseId) {
            var result = '';
            angular.forEach(vm.releases, function (value, key) {
                if (value.id == releaseId) {
                    result = value.versionTag;
                }
            });

            return result;
        }
    }
})();
