(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDetailController', ProjectDetailController);


    ProjectDetailController.$inject = ['$state', '$scope', '$rootScope', '$stateParams', 'previousState', 'project',
        'projectReleases', 'Project', 'Release', 'User', 'ProjectRoles', 'Principal', 'UserName'];

    function ProjectDetailController($state, $scope, $rootScope, $stateParams, previousState, project, projectReleases,
                                     Project, Release, User, ProjectRoles, Principal, UserName) {

        var vm = this;

        vm.goToTranslate = goToTranslate;

        vm.project = project;
        vm.ownerDetails = UserName.get({id: vm.project.ownerId});
        vm.previousState = previousState.name;
        vm.releases = projectReleases;
        vm.currentRelease = null;
        vm.roles = "No role assigned";
      //  vm.progress = 0.0;
        vm.isOwner = isOwner;
        vm.isTranslator = isTranslator;
        vm.isReleaseManager = isReleaseManager;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function (event, result) {
            vm.project = result;
        });
        $scope.$on('$destroy', unsubscribe);

        getAccount();
        activate();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        function isTranslator() {
            return vm.roles && vm.roles.includes('TRANSLATOR');
        }

        function isReleaseManager() {
            return vm.roles && vm.roles.includes('RELEASE_MANAGER')
        }

        function isOwner() {
            return vm.project.ownerId == vm.account.id;
        }

        function activate() {

            ProjectRoles.query({projectId: vm.project.id}, onSuccess);

            function onSuccess(response) {
                vm.roles = response;
            }

            //ProjectProgress.query({id: vm.project.id}, onQuerySuccess);

          //  function onQuerySuccess(response) {
          //      vm.progress = response;
         //   }

        }

        function goToTranslate() {
            $state.go('project-detail.open-translation');
        }

    }
})();
