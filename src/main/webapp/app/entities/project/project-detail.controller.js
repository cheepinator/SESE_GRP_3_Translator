(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$http', '$stateParams', 'previousState', 'project', 'Project', 'Release', 'User'];

    function ProjectDetailController($scope, $rootScope, $http, $stateParams, previousState, project, Project, Release, User) {
        var vm = this;

        vm.project = project;
        vm.previousState = previousState.name;
        vm.role = "Keine Rolle";
        var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function (event, result) {
            vm.project = result;
        });

        /*  $http.get("api/projectusers/" + vm.project.id).then(function (response) {
         },
         function (response) {
         });*/

        $http.get("api/projects/userRole/" + vm.project.id).then(function (response) {
            if (response.data.length == 1) {
                vm.role = response.data[0];
            }
        });

        $scope.$on('$destroy', unsubscribe);
    }
})();
