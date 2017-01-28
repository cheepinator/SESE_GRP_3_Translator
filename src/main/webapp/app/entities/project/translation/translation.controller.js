(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('TranslatorController', TranslatorController);

    TranslatorController.$inject = ['$scope', '$state', '$stateParams', 'Project', 'Language', 'Release', 'CountTranslations', 'dateFilter',
        'ReleaseTooltips', 'Definition', 'project', 'currentRelease', 'nextTranslations', 'language', 'previousState', 'Translation', 'NextTranslation',
        '$timeout'];

    function TranslatorController($scope, $state, $stateParams, Project, Language, Release,
                                  CountTranslations, dateFilter, ReleaseTooltips, Definition,
                                  project, currentRelease, nextTranslations, language, previousState, Translation, NextTranslation,
                                  $timeout) {
        var vm = this;

        vm.saveTranslation = saveTranslation;

        vm.project = project;
        vm.currentRelease = currentRelease;
        vm.nextTranslations = nextTranslations;
        vm.language = language;
        vm.previousState = previousState.name;
        vm.releases = [];
        vm.selectNextTranslation = selectNextTranslation;

        activate();

        function saveTranslation(translation) {
            if(translation.translatedText){
                Translation.update(translation).$promise.then(function (result) {
                    var oldIndex = getOldIndex(translation);
                    if (oldIndex > -1) {
                        vm.nextTranslations[oldIndex] = result;
                    }
                    console.log(result);
                });
            }
        }

        function getOldIndex(translation) {
            var oldTranslation = vm.nextTranslations.find(function (tr) {
                return tr.id == translation.id;
            });
            return vm.nextTranslations.indexOf(oldTranslation);
        }

        function selectNextTranslation(translation, $event) {
            selectNextTranslationByIndex(getOldIndex(translation))
        }

        function selectNextTranslationByIndex(oldIndex) {
            if (oldIndex < vm.nextTranslations.length - 1) {
                var nextTranslation = vm.nextTranslations[oldIndex + 1];
                $timeout(function () {
                    angular.element('#translation-' + nextTranslation.id + '>a').click();
                });
            } else {
                // for the last translation, there is no next one. deselect the input to save changes on enter
                var currentTranslation = vm.nextTranslations[oldIndex];
                $timeout(function () {
                    angular.element('#translation-' + currentTranslation.id).click();
                });
            }
        }

        function activate() {
        }


    }


})();
