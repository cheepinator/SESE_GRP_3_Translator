(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectTranslationDialogController', ProjectTranslationDialogController);

    ProjectTranslationDialogController.$inject = ['$state', '$timeout', '$uibModalInstance','Language', 'releases','languages'];

    function ProjectTranslationDialogController ($state, $timeout, $uibModalInstance, Language, releases,languages) {
        var vm = this;

        vm.languages = languages;
        vm.releases = releases;
        vm.selectedRelease = "";
        vm.selectedLanguage = "";
        vm.show = show;
        vm.clear = clear;

        function clear () {
            $uibModalInstance.dismiss("cancel");
        }

        function show () {
            $state.go('project-detail.translation', {curReleaseId: vm.selectedRelease.id, languageId: vm.selectedLanguage.id});
            $uibModalInstance.close();
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
