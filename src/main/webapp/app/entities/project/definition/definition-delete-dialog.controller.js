(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionDeleteController',ProjectDefinitionDeleteController);

    ProjectDefinitionDeleteController.$inject = ['$uibModalInstance', 'definition', 'Definition'];

    function ProjectDefinitionDeleteController($uibModalInstance, definition, Definition) {
        var vm = this;

        vm.definition = definition;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Definition.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
