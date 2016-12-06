(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentController', ProjectassignmentController);

    ProjectassignmentController.$inject = ['$scope', '$state', 'project', 'Projectassignment', 'Project', 'ProjectassignmentProject', 'User'];

    function ProjectassignmentController ($scope, $state, project, Projectassignment, Project, ProjectassignmentProject, User) {
        var vm = this;

        vm.projectassignments = [];
        vm.projects = [];
        vm.users = [];
        vm.project = project;

        loadAll();

        function loadAll() {
            ProjectassignmentProject.query({projectId: vm.project.id},function(result) {
                vm.projectassignments = result;
            });

            Project.query(function(result) {
                vm.projects = result;
            });

            User.query(function(result) {
                vm.users = result;
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

        $scope.getAssignedUserByID = getAssignedUserByID;

        function getAssignedUserByID(id) {
            $scope.assignedUser = null;
            vm.users.forEach(function (entry) {
                if(entry.id == id){
                    $scope.assignedUser = entry;
                }
            })
        }
    }
})();
