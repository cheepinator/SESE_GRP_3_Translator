(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('project-detail.definition-detail', {
                parent: 'project-detail',
                url: '/definition/{definitionId}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Definition'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/project/definition/definition-detail.html',
                        controller: 'ProjectDefinitionDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    definition: ['$stateParams', 'Definition', function($stateParams, Definition) {
                        return Definition.get({id : $stateParams.definitionId}).$promise;
                    }],
                    release: ['$stateParams', 'definition', 'Release', function($stateParams, definition, Release) {
                        return Release.get({id : definition.releaseId}).$promise;
                    }],
                    translations: ['$stateParams', 'DefinitionTranslation', function($stateParams, DefinitionTranslation) {
                        return DefinitionTranslation.query({definitionId : $stateParams.definitionId}).$promise;
                    }],
                    previousState: ["$state", function ($state) {
                        var currentStateData = {
                            name: $state.current.name || 'project-detail',
                            params: $state.params,
                            url: $state.href($state.current.name, $state.params)
                        };
                        return currentStateData;
                    }]
                }
            })
            .state('project-detail.definition-detail.edit', {
                parent: 'project-detail.definition-detail',
                url: '/detail/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'definition', 'projectReleases',
                    function ($stateParams, $state, $uibModal, definition, projectReleases) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/definition/definition-dialog.html',
                        controller: 'ProjectDefinitionDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            definition: function () {
                                return definition;
                            },
                            defaultRelease: function () {
                                return {};
                            },
                            projectReleases: function () {
                                return projectReleases;
                            }
                        }
                    }).result.then(function() {
                        $state.go('^', {}, { reload: true });
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('project-detail.new-definition', {
                parent: 'project-detail',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'defaultRelease', 'projectReleases',
                    function ($stateParams, $state, $uibModal, defaultRelease, projectReleases) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/definition/definition-dialog.html',
                        controller: 'ProjectDefinitionDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            definition: function () {
                                return {
                                    code: null,
                                    originalText: null,
                                    id: null
                                };
                            },
                            defaultRelease: function () {
                                return defaultRelease;
                            },
                            projectReleases: function () {
                                return projectReleases;
                            }
                        }
                    }).result.then(function() {
                        $state.go('project-detail', null, { reload: 'project-detail' });
                    }, function() {
                        $state.go('project-detail');
                    });
                }]
            })
            .state('project-detail.edit-definition', {
                parent: 'project-detail',
                url: '/definition/{definitionId}/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'projectReleases', function($stateParams, $state, $uibModal, projectReleases) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/definition/definition-dialog.html',
                        controller: 'ProjectDefinitionDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            definition: ['Definition', function(Definition) {
                                return Definition.get({id : $stateParams.definitionId}).$promise;
                            }],
                            defaultRelease: function () {
                                return {};
                            },
                            projectReleases: function () {
                                return projectReleases;
                            }
                        }
                    }).result.then(function() {
                        $state.go('project-detail', null, { reload: 'project-detail' });
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('project-detail.delete-definition', {
                parent: 'project-detail',
                url: '/definition/{definitionId}/delete',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/definition/definition-delete-dialog.html',
                        controller: 'ProjectDefinitionDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            definition: ['Definition', function(Definition) {
                                return Definition.get({id : $stateParams.definitionId}).$promise;
                            }]
                        }
                    }).result.then(function() {
                        $state.go('project-detail', null, { reload: 'project-detail' });
                    }, function() {
                        $state.go('^');
                    });
                }]
            });
    }

})();
