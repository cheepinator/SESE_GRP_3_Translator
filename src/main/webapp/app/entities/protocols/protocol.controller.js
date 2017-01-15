(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProtocolController', ProtocolController);

    ProtocolController.$inject = ['$scope', '$state', '$stateParams', 'Protocol', 'project', 'previousState'];

    function ProtocolController ($scope, $state, $stateParams, Protocol, project, previousState) {
        var vm = this;

        vm.protocols = [];
        vm.project = project;
        vm.previousState = previousState.name;

        loadAll();

        function loadAll() {
            Protocol.get({id:$stateParams.projectId},function(result) {
                vm.protocols = result;
            });
        }
    }
})();
