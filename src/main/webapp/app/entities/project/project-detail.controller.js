(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDetailController', ProjectDetailController);

    ProjectDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'project', 'Project', 'Release', 'User'];

    function ProjectDetailController($scope, $rootScope, $stateParams, previousState, project, Project, Release, User) {
        var vm = this;

        vm.project = project;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:projectUpdate', function(event, result) {
            vm.project = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
