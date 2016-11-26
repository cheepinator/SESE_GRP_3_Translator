(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('TranslationDetailController', TranslationDetailController);

    TranslationDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'Translation', 'User', 'Language', 'Definition'];

    function TranslationDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, Translation, User, Language, Definition) {
        var vm = this;

        vm.translation = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:translationUpdate', function(event, result) {
            vm.translation = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
