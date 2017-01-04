(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionDialogController', ProjectDefinitionDialogController);

    ProjectDefinitionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils',
        'definition', 'projectReleases', 'Definition', 'Translation', 'Release'];

    function ProjectDefinitionDialogController($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, definition,
                                               projectReleases, Definition, Translation, Release) {
        var vm = this;

        vm.definition = definition;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.translations = Translation.query();
        vm.releases = projectReleases;
        vm.selectedRelease = [];
        vm.isEdit = vm.definition.id != null;

        if (vm.isEdit) {
            for (var i = 0; i < vm.releases.length; i++) {
                if (vm.releases[i].id == vm.definition.releaseId) {
                    vm.selectedRelease = vm.releases[i];
                }
            }
        } else {
            for (var i = 0; i < vm.releases.length; i++) {
                if (vm.releases[i].isCurrentRelease == true) {
                    vm.selectedRelease = vm.releases[i];
                }
            }
        }
        $timeout(function () {
            if (vm.isEdit) {
                angular.element('.form-group:eq(1)>textarea').focus();
            } else {
                angular.element('.form-group:eq(0)>input').focus();
            }
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            vm.definition.releaseId = vm.selectedRelease.id;
            if (vm.definition.id !== null) {
                Definition.update(vm.definition, onSaveSuccess, onSaveError);
            } else {
                Definition.save(vm.definition, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess(result) {
            $scope.$emit('seseTranslatorApp:definitionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }
    }
})();
