(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('DefinitionDetailController', DefinitionDetailController);

    DefinitionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Definition', 'Translation', 'Release'];

    function DefinitionDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Definition, Translation, Release) {
        var vm = this;

        vm.definition = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:definitionUpdate', function(event, result) {
            vm.definition = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
