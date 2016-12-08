(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$state','$scope', '$rootScope', '$stateParams', 'previousState', 'project',
        'projectReleases', 'Project', 'Release', 'User', 'ProjectRoles'];

    function ProjectDetailController($state,$scope, $rootScope, $stateParams, previousState, project, projectReleases,
                                     Project, Release, User, ProjectRoles) {
        var vm = this;

        vm.goToTranslate = goToTranslate;

        vm.project = project;
        vm.previousState = previousState.name;
        vm.releases = projectReleases;
        vm.currentRelease = null;
        vm.role = "Keine Rolle";

        activate();

        function activate() {
            var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function (event, result) {
                vm.project = result;
            });

            $scope.$on('$destroy', unsubscribe);

            ProjectRoles.query({projectId: vm.project.id}, onSuccess);
            function onSuccess(response) {
                if(response[0] != null){
                    vm.role = response[0];
                }
            }

            for(var x in vm.releases){
                if(vm.releases[x].isCurrentRelease){
                    vm.currentRelease = vm.releases[x];
                }
            }

        }

        function goToTranslate() {
            $state.go('project-detail.translation', { curReleaseId:vm.currentRelease.id.toString() });//todo send language id
        }
    }
})();
