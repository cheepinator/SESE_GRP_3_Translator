(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'project',
        'projectReleases', 'Project', 'Release', 'User', 'ProjectRoles', 'Principal'];

    function ProjectDetailController($scope, $rootScope, $stateParams, previousState, project, projectReleases,
                                     Project, Release, User, ProjectRoles, Principal) {
        var vm = this;

        vm.project = project;
        vm.previousState = previousState.name;
        vm.releases = projectReleases;
        vm.role = "Keine Rolle";
        vm.isOwner = isOwner;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function (event, result) {
            vm.project = result;
        });
        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        function isOwner(id) {
            return id == vm.account.id;
        }

        ProjectRoles.query({projectId: vm.project.id}, onSuccess);

        function onSuccess(response) {
            if (response.length > 0) {
                vm.role = response[0];
            }
        }

        $scope.$on('$destroy', unsubscribe);
    }
})();
