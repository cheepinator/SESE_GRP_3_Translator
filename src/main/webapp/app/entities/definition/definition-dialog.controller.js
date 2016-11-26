(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('DefinitionDialogController', DefinitionDialogController);

    DefinitionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Definition', 'Translation', 'Release'];

    function DefinitionDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Definition, Translation, Release) {
        var vm = this;

        vm.definition = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.translations = Translation.query();
        vm.releases = Release.query();

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
