(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ReleaseController', ReleaseController);

    ReleaseController.$inject = ['$scope', '$state', '$stateParams', 'Release', 'previousState', 'ReleaseProject'];

    function ReleaseController ($scope, $state, $stateParams, Release, previousState, ReleaseProject) {
        var vm = this;

        vm.releases = [];
        vm.release = [];
        vm.previousState = previousState.name;
        vm.isCurrentRelease = isCurrentRelease;
        vm.projectId = $stateParams.projectId;
        loadAll();

        function loadAll() {

            ReleaseProject.query({projectId: vm.projectId},function(result) {
                vm.releases = result;
            });
        }

        function isCurrentRelease (isActive) {
            if(isActive) {
                return 'glyphicon glyphicon-ok'
            } else {
                return 'glyphicon glyphicon-remove'
            }
        }

        function getProject(projectId) {
            for(var i = 0; i < vm.releases.length; i++){
                if(vm.releases[i].id == projectId){
                    vm.selectedRelease = vm.releases[i];
                }
            }
        }
    }
})();
