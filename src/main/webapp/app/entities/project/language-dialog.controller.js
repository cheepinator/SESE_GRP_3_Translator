(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectLanguageDialogController', ProjectLanguageDialogController);

    ProjectLanguageDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'project', 'NewProjectLanguage', 'Release'];

    function ProjectLanguageDialogController($timeout, $scope, $stateParams, $uibModalInstance, project, NewProjectLanguage, Release) {
        var vm = this;

        vm.project = project;
        vm.language = {code: null, id: null};
        vm.clear = clear;
        vm.save = save;
        vm.releases = Release.query();

        $timeout(function () {
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function save() {
            vm.isSaving = true;
            NewProjectLanguage.save({projectId: vm.project.id}, vm.language, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess(result) {
            $scope.$emit('seseTranslatorApp:languageUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }


    }
})();
