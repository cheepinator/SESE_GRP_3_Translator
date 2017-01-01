(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ReleaseDetailController', ReleaseDetailController);

    ReleaseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Release', 'Definition', 'Language', 'Project'];

    function ReleaseDetailController($scope, $rootScope, $stateParams, previousState, entity, Release, Definition, Language, Project) {
        var vm = this;

        vm.release = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:releaseUpdate', function(event, result) {
            vm.release = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
