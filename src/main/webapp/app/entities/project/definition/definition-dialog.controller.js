(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionDialogController', ProjectDefinitionDialogController);

    ProjectDefinitionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils',
        'definition', 'defaultRelease', 'Definition', 'Translation', 'Release'];

    function ProjectDefinitionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, definition,
                                                defaultRelease, Definition, Translation, Release) {
        var vm = this;

        vm.definition = definition;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.translations = Translation.query();
        vm.releases = Release.query();
        vm.defaultRelease = defaultRelease;
        if (vm.definition.id == null) {
            vm.definition.releaseId = vm.defaultRelease.id;
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.definition.id !== null) {
                Definition.update(vm.definition, onSaveSuccess, onSaveError);
            } else {
                Definition.save(vm.definition, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('seseTranslatorApp:definitionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
