(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionController', ProjectDefinitionController);

    ProjectDefinitionController.$inject = ['$scope', '$state', '$location', 'project', 'projectReleases', 'DataUtils',
        'ProjectDefinition', 'ParseLinks', 'AlertService', 'ReleaseTooltips', 'ProjectTranslations', 'Principal', 'ProjectRoles',
        'ProjectProgress', 'CurrentRelease', 'FileUploadDefinition', 'Definition', 'Upload'];

    function ProjectDefinitionController($scope, $state, $location, project, projectReleases, DataUtils, ProjectDefinition,
                                         ParseLinks, AlertService, ReleaseTooltips, ProjectTranslations, Principal, ProjectRoles,
                                         ProjectProgress, CurrentRelease, FileUploadDefinition, Definition, Upload) {
        var vm = this;

        vm.baseUrl = "http://" + $location.$$host + ":" + $location.$$port;

        vm.project = project;
        vm.releases = projectReleases;
        vm.selectedRelease = [];
        vm.activeReleaseId = '';
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
        vm.proejctDetails = [];
        vm.getReleaseTooltip = ReleaseTooltips.getReleaseTooltip;
        vm.getTranslations = getTranslations;
        vm.isDeveloper = isDeveloper;
        vm.isReleaseManager = isReleaseManager;
        vm.uploadThis = uploadThis;
        vm.setSelectedRelease = setSelectedRelease;
        vm.getReleaseName = getReleaseName;
        vm.filterByVersionTagFunction = filterByVersionTagFunction;
        vm.saveDefinition = saveDefinition;
        vm.droppedFile = droppedFile;
        loadAll();
        getAccount();
        setInitialFilteringRelease();

        $scope.uploadFile = function(files) {
            var reader = new FileReader();

            reader.onload = function(e) {
                $scope.$apply(function() {
                    $scope.theFileToImport = reader.result;
                });
            };
            reader.readAsBinaryString(files[0]);
        };

        function uploadThis() {
            $scope.theFileToImport =  $scope.theFileToImport.replace(/\r\n/g, '');
            FileUploadDefinition.query({
                projectId: vm.project.id,
                fileUpL: $scope.theFileToImport
            });
        }

        function setInitialFilteringRelease() {
            vm.selectedRelease = "";
            setSelectedRelease();
        }

        function filterByVersionTagFunction() {
            return function (item) {
                var result = true;
                if (vm.filterBy != '') {
                    result = vm.getReleaseName(item.releaseId) === vm.filterBy;
                }
                return result;
            };
        }

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        ProjectRoles.query({projectId: vm.project.id}, function (response) {
            vm.roles = response;
        });

        vm.progress = ProjectProgress.query({projectId: vm.project.id});

        function isDeveloper() {
            return vm.roles && vm.roles.includes('DEVELOPER');
        }

        function isReleaseManager() {
            return vm.roles && vm.roles.includes('RELEASE_MANAGER');
        }

        function setSelectedRelease() {
            if (vm.selectedRelease != null && vm.selectedRelease.versionTag != null) {
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
                var result = [];
                if (vm.predicate !== 'release.dueDate') {
                    result.push('release.dueDate');
                }
                result.push(vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'));
                result.push('id');
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

            CurrentRelease.get({projectId: vm.project.id}, function (result) {
                vm.activeReleaseId = result.id;
            });

            ProjectTranslations.query({projectId: vm.project.id}, onTranslationsSuccess, onError);

            function onTranslationsSuccess(data, headers) {
                vm.translations = [];
                for (var i = 0; i < data.length; i++) {
                    vm.translations.push(data[i]);
                }
            }

        }

        function getTranslations(definition) {
            return vm.translations.filter(function (translation) {
                return translation.definitionId == definition.id;
            });
        }

        vm.getProgressValue = getProgressValue;
        function getProgressValue(language) {
            var progress = vm.progress.find(function (progress) {
                return progress.language.id == language.id;
            });
            if (progress) {
                return progress.current;
            }
            return 0;
        }

        vm.getMaxProgressValue = getMaxProgressValue;
        function getMaxProgressValue() {
            if (vm.progress.length > 0) {
                return vm.progress[0].max;
            }
            return 0;
        }

        vm.hasUpdateNeeded = hasUpdateNeeded;
        function hasUpdateNeeded(language) {
            var progress = vm.progress.find(function (progress) {
                return progress.language.id == language.id;
            });
            if (progress) {
                return progress.hasUpdateNeeded;
            }
            return false;
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
            var release = vm.releases.find(function (release) {
                return release.id == releaseId;
            });
            if (release) {
                return release.versionTag;
            } else {
                return '';
            }
        }

        function saveDefinition(definition) {
            Definition.update(definition).$promise.then(function (result) {
                console.log(result);
            });
        }

        function droppedFile(file) {
            if (!isDeveloper()) {
                return;
            }
            console.log(file);
            if (file) {
                Upload.upload({
                    url: 'api/projects/' + vm.project.id + '/fileUpload',
                    data: {file: file}
                }).then(function (resp) {
                    console.log('Success ' + resp.config.data.file.name + ' uploaded. Response: ' + resp.data);
                }, function (resp) {
                    console.log('Error status: ' + resp.status);
                }, function (evt) {
                    var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                    console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
                });
            }
        }
    }
})();
