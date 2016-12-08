(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectController', ProjectController);

    ProjectController.$inject = ['$q', '$scope', '$state', 'Project', 'Language', 'Release', 'CountTranslations', 'dateFilter', 'Principal',
        'ReleaseTooltips', '$location', 'ProjectRoles'];

    function ProjectController($q, $scope, $state, Project, Language, Release, CountTranslations, dateFilter, Principal, ReleaseTooltips, $location, ProjectRoles) {
        var vm = this;

        vm.baseUrl = "http://" + $location.$$host + ":" + $location.$$port;

        vm.projects = [];
        vm.languages = [];
        vm.releases = [];
        vm.roles = [];
        vm.isOwner = isOwner;
        vm.isDeveloper = isDeveloper;
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

        $scope.getCurrentReleaseByProjectID = getCurrentReleaseByProjectID;
        function getCurrentReleaseByProjectID(id) {
            $scope.currentRelease = null;
            vm.releases.forEach(function (entry) {
                if (entry.projectId == id && entry.isCurrentRelease) {
                    $scope.currentRelease = entry;
                }
            })
        }

        $scope.getCountOfTranslationsByReleaseID = getCountOfTranslationsByReleaseID;
        function getCountOfTranslationsByReleaseID(id) {
            console.log("looking for id " + id);
            $scope.countOfTranslations = CountTranslations.get(0);
        }
    }


})();
