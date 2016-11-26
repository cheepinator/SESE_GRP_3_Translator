(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentDeleteController',ProjectassignmentDeleteController);

    ProjectassignmentDeleteController.$inject = ['$uibModalInstance', 'entity', 'Projectassignment'];

    function ProjectassignmentDeleteController($uibModalInstance, entity, Projectassignment) {
        var vm = this;

        vm.projectassignment = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Projectassignment.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
