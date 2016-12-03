(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentController', ProjectassignmentController);

    ProjectassignmentController.$inject = ['$scope', '$state', 'Projectassignment', 'Project'];

    function ProjectassignmentController ($scope, $state, Projectassignment, Project) {
        var vm = this;

        vm.projectassignments = [];
        vm.projects = [];

        loadAll();

        function loadAll() {
            Projectassignment.query(function(result) {
                vm.projectassignments = result;
            });

            Project.query(function(result) {
                vm.projects = result;
            });
        }

        $scope.getAssignedProjectProjectID = getAssignedProjectProjectID;

        function getAssignedProjectProjectID(id) {
            $scope.assignedProject = null;
            vm.projects.forEach(function (entry) {
                if(entry.id == id){
                    $scope.assignedProject = entry;
                }
            })
        }
    }
})();
