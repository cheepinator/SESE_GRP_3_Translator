(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'project',
        'projectReleases', 'Project', 'Release', 'User', 'ProjectRoles'];

    function ProjectDetailController($scope, $rootScope, $stateParams, previousState, project, projectReleases,
                                     Project, Release, User, ProjectRoles) {
        var vm = this;

        vm.project = project;
        vm.previousState = previousState.name;
        vm.releases = projectReleases;
        vm.role = "Keine Rolle";
        var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function (event, result) {
            vm.project = result;
        });

        ProjectRoles.query({projectId: vm.project.id}, onSuccess);

        function onSuccess(response) {
            vm.role = response[0];
        }

        $scope.$on('$destroy', unsubscribe);
    }
})();
