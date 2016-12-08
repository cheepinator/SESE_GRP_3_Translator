(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'project',
        'projectReleases', 'Project', 'Release', 'User', 'ProjectRoles', 'Principal', 'UserName'];

    function ProjectDetailController($scope, $rootScope, $stateParams, previousState, project, projectReleases,
                                     Project, Release, User, ProjectRoles, Principal, UserName) {
        var vm = this;

        vm.project = project;
        vm.ownerDetails = UserName.get({id: vm.project.ownerId});
        vm.previousState = previousState.name;
        vm.releases = projectReleases;
        vm.role = "No role assigned";
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

        function isOwner() {
            return vm.project.ownerId == vm.account.id;
        }

        ProjectRoles.query({projectId: vm.project.id}, onSuccess);

        function onSuccess(response) {
            if (response.length > 0) {
                vm.role = response.join(', ');
            }
        }

        $scope.$on('$destroy', unsubscribe);
    }
})();
