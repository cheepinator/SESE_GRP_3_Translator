(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectassignmentDetailController', ProjectassignmentDetailController);

    ProjectassignmentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Projectassignment', 'User', 'Project'];

    function ProjectassignmentDetailController($scope, $rootScope, $stateParams, previousState, entity, Projectassignment, User, Project) {
        var vm = this;

        vm.projectassignment = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:projectassignmentUpdate', function(event, result) {
            vm.projectassignment = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
