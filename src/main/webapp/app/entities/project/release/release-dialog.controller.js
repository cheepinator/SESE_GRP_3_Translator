(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ReleaseDialogController', ReleaseDialogController);

    ReleaseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Release', 'Definition', 'Language', 'Project'];

    function ReleaseDialogController($timeout, $scope, $stateParams, $uibModalInstance, entity, Release, Definition, Language, Project) {
        var vm = this;

        vm.release = entity;

        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.definitions = Definition.query();
        vm.languages = Language.query();
        vm.projects = Project.query();
        vm.projectId = $stateParams.projectId;

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            vm.release.projectId = vm.projectId;
            if (vm.release.id !== null) {
                Release.update(vm.release, onSaveSuccess, onSaveError);
            } else {
                Release.save(vm.release, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess(result) {
            $scope.$emit('seseTranslatorApp:releaseUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dueDate = false;

        function openCalendar(date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
