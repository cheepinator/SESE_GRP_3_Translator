(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionDetailController', ProjectDefinitionDetailController);

    ProjectDefinitionDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils',
        'project', 'definition', 'release', 'translations', 'Definition', 'Translation', 'Release', 'ReleaseTooltips',
        'ProjectRoles'];

    function ProjectDefinitionDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, project,
                                               definition, release, translations, Definition, Translation, Release,
                                               ReleaseTooltips, ProjectRoles) {
        var vm = this;

        vm.project = project;
        vm.definition = definition;
        vm.release = release;
        vm.translations = translations;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;

        vm.openFile = DataUtils.openFile;

        vm.getTranslation = getTranslation;
        vm.isDeveloper = isDeveloper;

        function getTranslation(languageId) {
            // save the translation in the current scope i.e. in our case inside a ng-repeat for just the current element
            $scope.translation = vm.translations.find(function (translation) {
                return translation.languageId == languageId;
            });
        }

        vm.getReleaseTooltip = ReleaseTooltips.getReleaseTooltip;

        ProjectRoles.query({projectId: vm.project.id}, function (response) {
            vm.roles = response;
        });

        function isDeveloper() {
            return vm.roles && vm.roles.includes('DEVELOPER');
        }

        var unsubscribe = $rootScope.$on('seseTranslatorApp:definitionUpdate', function (event, result) {
            vm.definition = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
