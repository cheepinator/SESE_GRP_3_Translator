(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentDialogController', ProjectassignmentDialogController);

    ProjectassignmentDialogController.$inject = ['$timeout', '$scope', '$stateParams', 'project', '$uibModalInstance', 'entity', 'Projectassignment', 'User', 'Project'];

    function ProjectassignmentDialogController ($timeout, $scope, $stateParams, project, $uibModalInstance, entity, Projectassignment, User, Project) {
        var vm = this;

        vm.projectassignment = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();
        vm.projects = Project.query();
        vm.project = project;
        vm.projectassignment.assignedProjectId = vm.project.id;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.projectassignment.id !== null) {
                Projectassignment.update(vm.projectassignment, onSaveSuccess, onSaveError);
            } else {
                Projectassignment.save(vm.projectassignment, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('seseTranslatorApp:projectassignmentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
