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
            vm.role = "No role assigned";
            vm.isOwner = isOwner;
            vm.isTranslator = isTranslator;

            var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function (event, result) {
                vm.project = result;
            });

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

            function isOwner() {
                return vm.project.ownerId == vm.account.id;
            }


            function activate() {
                var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function (event, result) {
                    vm.project = result;
                });

                $scope.$on('$destroy', unsubscribe);

                ProjectRoles.query({projectId: vm.project.id}, onSuccess);
                function onSuccess(response) {
                    if (response[0] != null) {
                        vm.role = response[0];
                    }
                }

                for (var x in vm.releases) {
                    if (vm.releases[x].isCurrentRelease) {
                        vm.currentRelease = vm.releases[x];
                    }
                }
            }


            function onSuccess(response) {
                if (response.length > 0) {
                    vm.role = response.join(', ');
                }

            }

            function goToTranslate() {
                $state.go('project-detail.translation', {curReleaseId: vm.currentRelease.id.toString()});//todo send language id
            }

        }
    })();
