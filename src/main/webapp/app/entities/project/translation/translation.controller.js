(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('TranslatorController', TranslatorController);

    TranslatorController.$inject = ['$scope', '$state', '$stateParams', 'Project', 'Language', 'Release', 'CountTranslations', 'dateFilter',
        'ReleaseTooltips', 'Definition', 'project', 'currentRelease', 'nextTranslations', 'language', 'previousState', 'Translation', 'NextTranslation'];

    function TranslatorController($scope, $state, $stateParams, Project, Language, Release,
                                  CountTranslations, dateFilter, ReleaseTooltips, Definition,
                                  project, currentRelease, nextTranslations, language, previousState, Translation, NextTranslation) {
        var vm = this;

        vm.saveTranslation = saveTranslation;

        vm.project = project;
        vm.currentRelease = currentRelease;
        vm.nextTranslations = nextTranslations;
        vm.language = language;
        vm.previousState = previousState.name;
        vm.releases = [];

        activate();

        function saveTranslation(translation) {
            if(translation.translatedText != null){
                Translation.update(translation).$promise.then(function (result) {
                    var oldTranslation= vm.nextTranslations.find(function (translation) {
                        return translation.id == result.id;
                    });
                    const oldIndex = vm.nextTranslations.indexOf(oldTranslation);
                    if (oldIndex > -1) {
                        vm.nextTranslations[oldIndex] = result;
                    }
                    console.log(result);
                });
            }
        }

        function activate() {
        }


    }


})();
