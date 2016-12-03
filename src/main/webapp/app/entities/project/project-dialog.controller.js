(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDialogController', ProjectDialogController);

    ProjectDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'project', 'Principal', 'Project', 'Release', 'User'];

    function ProjectDialogController ($timeout, $scope, $stateParams, $uibModalInstance, project,Principal, Project, Release, User) {
        var vm = this;

        vm.project = project;
        vm.clear = clear;
        vm.save = save;
        vm.releases = Release.query();
        vm.users = User.query();

        getAccount();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.project.id !== null) {
                Project.update(vm.project, onSaveSuccess, onSaveError);
            } else {
                vm.project.owner = vm.account.id;
                Project.save(vm.project, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('seseTranslatorApp:projectUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
