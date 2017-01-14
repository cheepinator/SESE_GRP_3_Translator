(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProtocolController', ProtocolController);

    ProtocolController.$inject = ['$scope', '$state', 'Protocol'];

    function ProtocolController ($scope, $state, Protocol) {
        var vm = this;

        vm.protocols = [];

        loadAll();

        function loadAll() {
            Protocol.query(function(result) {
                vm.protocols = result;
            });
        }
    }
})();
