(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectController', ProjectController);

    ProjectController.$inject = ['$q', '$scope', '$state', 'Project', 'Language', 'Release', 'CountTranslations', 'dateFilter', 'Principal',
        'ReleaseTooltips', '$location', 'ProjectRoles', 'ProjectDetails'];

    function ProjectController($q, $scope, $state, Project, Language, Release, CountTranslations, dateFilter, Principal, ReleaseTooltips, $location, ProjectRoles, ProjectDetails) {
        var vm = this;

        vm.baseUrl = "http://" + $location.$$host + ":" + $location.$$port;

        vm.projects = [];
        vm.languages = [];
        vm.releases = [];
        vm.roles = [];
        vm.proejctDetails = [];

        vm.getCurrentReleaseProgress = getCurrentReleaseProgress;
        vm.isOwner = isOwner;
        vm.isDeveloper = isDeveloper;
        vm.getCurrentReleaseByProjectId = getCurrentReleaseByProjectId;
        vm.getReleaseTooltip = ReleaseTooltips.getReleaseTooltip;

        getAccount();
        loadAll();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        function isOwner(id) {
            return id == vm.account.id;
        }

        function isDeveloper(project) {
            return project.roles && project.roles.includes('DEVELOPER');
        }

        function loadAll() {
            Project.query(function (result) {
                vm.projects = result;
                var promises = [];
                for (var x = 0; x < vm.projects.length; x++) {
                    promises.push(ProjectRoles.query({projectId: vm.projects[x].id}));
                }
                $q.all(promises).then(function (response) {
                    for (var i = 0, len = response.length; i < len; ++i) {
                        vm.projects[i].roles = response[i];
                    }
                });
            });
            Language.query(function (result) {
                vm.languages = result
            });

            Release.query(function (result) {
                vm.releases = result
            });

            ProjectDetails.query(function (result) {
                vm.proejctDetails = result;
            });
        }

        function getCurrentReleaseByProjectId(projectId) {
            for (var i = 0; i < vm.proejctDetails.length; i++) {
                if (vm.proejctDetails[i].projectId == projectId) {
                    return vm.proejctDetails[i].currentRelease;
                }
            }
        }

        function getCurrentReleaseProgress(projectId) {
            for (var i = 0; i < vm.proejctDetails.length; i++) {
                if (vm.proejctDetails[i].projectId == projectId) {
                    return vm.proejctDetails[i].projectProgress;
                }
            }
        }

        $scope.getReleaseLabel = getReleaseLabel;
        function getReleaseLabel(release) {
            var result = "";
            if (release) {
                if (release.versionTag) {
                    result += release.versionTag;
                }
                if (release.dueDate) {
                    var formattedDate = dateFilter(release.dueDate, 'mediumDate');
                    result += ": " + formattedDate;
                }
            }
            return result;
        }

        $scope.getCountOfTranslationsByReleaseID = getCountOfTranslationsByReleaseID;
        function getCountOfTranslationsByReleaseID(id) {
            console.log("looking for id " + id);
            $scope.countOfTranslations = CountTranslations.get(0);
        }
    }


})();
