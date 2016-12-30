(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectLanguageDeleteController',ProjectLanguageDeleteController);

    ProjectLanguageDeleteController.$inject = ['$uibModalInstance', 'language', 'project', 'NewProjectLanguage'];

    function ProjectLanguageDeleteController($uibModalInstance, language, project, NewProjectLanguage) {
        var vm = this;

        vm.language = language;
        vm.project = project;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            NewProjectLanguage.delete({projectId: vm.project.id, languageId: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
