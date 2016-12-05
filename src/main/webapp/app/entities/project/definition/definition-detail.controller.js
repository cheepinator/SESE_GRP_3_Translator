(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionDetailController', ProjectDefinitionDetailController);

    ProjectDefinitionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils',
        'project', 'definition', 'release', 'translations', 'Definition', 'Translation', 'Release'];

    function ProjectDefinitionDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, project,
                                               definition, release, translations, Definition, Translation, Release) {
        var vm = this;

        vm.project = project;
        vm.definition = definition;
        vm.release = release;
        vm.translations = translations;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;

        vm.openFile = DataUtils.openFile;

        vm.getTranslation = getTranslation;

        function getTranslation(languageId) {
            // save the translation in the current scope i.e. in our case inside a ng-repeat for just the current element
            $scope.translation = vm.translations.find(function (translation) {
                return translation.languageId == languageId;
            });
        }

        var unsubscribe = $rootScope.$on('seseTranslatorApp:definitionUpdate', function (event, result) {
            vm.definition = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
