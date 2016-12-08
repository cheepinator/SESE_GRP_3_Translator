(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentController', ProjectassignmentController);

    ProjectassignmentController.$inject = ['$scope', '$state', 'project', 'Projectassignment', 'Project', 'ProjectassignmentProject', 'User', 'Principal'];

    function ProjectassignmentController ($scope, $state, project, Projectassignment, Project, ProjectassignmentProject, User, Principal) {
        var vm = this;

        vm.projectassignments = [];
        vm.projects = [];
        vm.users = [];
        vm.isOwner = isOwner;
        vm.project = project;

        getAccount();
        loadAll();
        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        function isOwner() {
            return vm.project.ownerId == vm.account.id;
        }

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
