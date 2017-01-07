(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ReleaseController', ReleaseController);

    ReleaseController.$inject = ['$scope', '$state', '$stateParams', 'Release', 'previousState', 'ReleaseProject', 'Project', 'CurrentRelease'];

    function ReleaseController($scope, $state, $stateParams, Release, previousState, ReleaseProject, Project, CurrentRelease) {
        var vm = this;

        vm.releases = [];
        vm.release = [];
        vm.previousState = previousState.name;
        vm.projectId = $stateParams.projectId;
        vm.project = [];
        vm.currentRelease = [];

        vm.isCurrentRelease = isCurrentRelease;
        loadAll();

        function loadAll() {

            ReleaseProject.query({projectId: vm.projectId}, function (result) {
                vm.releases = result;
            });

            CurrentRelease.get({projectId: vm.projectId}, function (result) {
                vm.currentRelease = result;
            });
        }

        function isCurrentRelease(release) {
            if (vm.currentRelease.id == release.id) {
                return true;
            }

            return false;
        }

        Project.get({id: vm.projectId}, function (result) {
            vm.project = result;
        });

        function getProject(projectId) {
            for (var i = 0; i < vm.releases.length; i++) {
                if (vm.releases[i].id == projectId) {
                    vm.selectedRelease = vm.releases[i];
                }
            }
        }
    }
})();
