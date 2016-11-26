(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentController', ProjectassignmentController);

    ProjectassignmentController.$inject = ['$scope', '$state', 'Projectassignment'];

    function ProjectassignmentController ($scope, $state, Projectassignment) {
        var vm = this;

        vm.projectassignments = [];

        loadAll();

        function loadAll() {
            Projectassignment.query(function(result) {
                vm.projectassignments = result;
            });
        }
    }
})();
