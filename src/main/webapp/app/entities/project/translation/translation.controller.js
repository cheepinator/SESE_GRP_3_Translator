(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('TranslatorController', TranslatorController);

    TranslatorController.$inject = ['$scope', '$state', 'Project', 'Language', 'Release', 'CountTranslations', 'dateFilter',
        'ReleaseTooltips', 'Definition', 'project', 'currentRelease', 'nextTranslation', 'language', 'previousState', 'Translation', 'NextTranslation'];

    function TranslatorController($scope, $state, Project, Language, Release,
                                  CountTranslations, dateFilter, ReleaseTooltips, Definition,
                                  project, currentRelease, nextTranslation, language, previousState, Translation, NextTranslation) {
        var vm = this;

        vm.saveAndNext = saveAndNext;

        vm.project = project;
        vm.currentRelease = currentRelease;
        vm.nextTranslation = nextTranslation;
        vm.language = language;
        vm.previousState = previousState.name;
        vm.definition = {};
        vm.releases = [];

        activate();

        function saveAndNext() {
            if(vm.nextTranslation.translatedText != null){
                Translation.update(vm.nextTranslation).$promise.then(function (result) {
                    console.log(result);
                    NextTranslation.query(
                        {
                            releaseId:vm.currentRelease.id,
                            languageId:1//todo change for languages!
                        }
                    ).$promise.then(function (result) {
                        console.log(result);
                        vm.nextTranslation = result;
                        activate();

                    });
                });
                //todo update and load new one
            }

        }

        function activate() {
            if (vm.nextTranslation.definitionId != null) {
                Definition.get({id: vm.nextTranslation.definitionId}).$promise.then(function (result) {
                    vm.definition = result;

                });
            }
        }


    }


})();
