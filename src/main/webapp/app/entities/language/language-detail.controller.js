(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('LanguageDetailController', LanguageDetailController);

    LanguageDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Language', 'Release'];

    function LanguageDetailController($scope, $rootScope, $stateParams, previousState, entity, Language, Release) {
        var vm = this;

        vm.language = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:languageUpdate', function(event, result) {
            vm.language = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
