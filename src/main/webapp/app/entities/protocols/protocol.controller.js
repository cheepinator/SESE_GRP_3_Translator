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
            var str= window.location.toString();
            Protocol.get({id:str.match(/project\/\d+/)[0].match(/\d+/)[0]},function(result) {
                vm.protocols = result;
            });
        }
    }
})();
