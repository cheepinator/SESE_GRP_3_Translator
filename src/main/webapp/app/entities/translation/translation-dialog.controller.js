(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('TranslationDialogController', TranslationDialogController);

    TranslationDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Translation', 'User', 'Language', 'Definition'];

    function TranslationDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Translation, User, Language, Definition) {
        var vm = this;

        vm.translation = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.users = User.query();
        vm.languages = Language.query();
        vm.definitions = Definition.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.translation.id !== null) {
                Translation.update(vm.translation, onSaveSuccess, onSaveError);
            } else {
                Translation.save(vm.translation, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('seseTranslatorApp:translationUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
