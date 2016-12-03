(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionDetailController', ProjectDefinitionDetailController);

    ProjectDefinitionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils',
        'project', 'definition', 'release', 'Definition', 'Translation', 'Release'];

    function ProjectDefinitionDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, project,
                                               definition, release, Definition, Translation, Release) {
        var vm = this;

        vm.project = project;
        vm.definition = definition;
        vm.release = release;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;

        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('seseTranslatorApp:definitionUpdate', function (event, result) {
            vm.definition = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
