(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectDefinitionDialogController', ProjectDefinitionDialogController);

    ProjectDefinitionDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils',
        'definition', 'projectReleases', 'Definition', 'Translation', 'Release', 'ProjectRoles', 'project'];

    function ProjectDefinitionDialogController($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, definition,
                                               projectReleases, Definition, Translation, Release, ProjectRoles, project) {
        var vm = this;

        vm.definition = definition;
        vm.project = project;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;
        vm.translations = Translation.query();
        vm.releases = projectReleases;
        vm.selectedRelease = [];
        vm.releaseId = $stateParams.releaseId;
        vm.isEdit = vm.definition.id != null;
        vm.isDeveloper = isDeveloper;
        vm.isReleaseManager = isReleaseManager;

        if (vm.isEdit) {
            for (var i = 0; i < vm.releases.length; i++) {
                if (vm.releases[i].id == vm.definition.releaseId) {
                    vm.selectedRelease = vm.releases[i];
                }
            }
        } else {
            for (var i = 0; i < vm.releases.length; i++) {
                if (vm.releases[i].id == vm.releaseId) {
                    vm.selectedRelease = vm.releases[i];
                }
            }
        }

        ProjectRoles.query({projectId: vm.project.id}, function (response) {
            vm.roles = response;
        });

        $timeout(function () {
            if (vm.isEdit) {
                angular.element('.form-group:eq(1)>textarea').focus();
            } else {
                angular.element('.form-group:eq(0)>input').focus();
            }
        });

        function clear() {
            $uibModalInstance.dismiss('cancel');
        }

        function isDeveloper() {
            return vm.roles && vm.roles.includes('DEVELOPER');
        }

        function isReleaseManager() {
            return vm.roles && vm.roles.includes('RELEASE_MANAGER');
        }

        function save() {
            vm.isSaving = true;
            vm.definition.releaseId = vm.selectedRelease.id;
            if (vm.definition.id !== null) {
                Definition.update(vm.definition, onSaveSuccess, onSaveError);
            } else {
                Definition.save(vm.definition, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess(result) {
            $scope.$emit('seseTranslatorApp:definitionUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError() {
            vm.isSaving = false;
        }
    }
})();
