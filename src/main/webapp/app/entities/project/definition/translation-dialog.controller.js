(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('DefinitionTranslationDialogController', DefinitionTranslationDialogController);

    DefinitionTranslationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Translation', 'User', 'Language', 'Definition', 'definition'];

    function DefinitionTranslationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Translation, User, Language, Definition, definition) {
        var vm = this;

        vm.translation = entity;
        vm.definition = definition;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.users = User.query();
        vm.language = Language.get({id:vm.translation.languageId});

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            vm.translation.updateNeeded = false;
            if (vm.translation.id !== null) {
                Translation.update(vm.translation, onSaveSuccess, onSaveError);
            } else {
                Translation.save(vm.translation, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
