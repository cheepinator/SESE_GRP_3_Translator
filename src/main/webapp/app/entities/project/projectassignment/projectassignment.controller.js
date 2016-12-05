(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentController', ProjectassignmentController);

    ProjectassignmentController.$inject = ['$scope', '$state', 'project', 'Projectassignment', 'Project', 'ProjectassignmentProject'];

    function ProjectassignmentController ($scope, $state, project, Projectassignment, Project, ProjectassignmentProject) {
        var vm = this;

        vm.projectassignments = [];
        vm.projects = [];
        vm.project = project;

        loadAll();

        function loadAll() {
            ProjectassignmentProject.query({projectId: vm.project.id},function(result) {
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
