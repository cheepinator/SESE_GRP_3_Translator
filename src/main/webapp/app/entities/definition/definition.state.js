(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('definition', {
            parent: 'entity',
            url: '/definition',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Definitions'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/definition/definitions.html',
                    controller: 'DefinitionController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('definition-detail', {
            parent: 'entity',
            url: '/definition/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Definition'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/definition/definition-detail.html',
                    controller: 'DefinitionDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Definition', function($stateParams, Definition) {
                    return Definition.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'definition',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('definition-detail.edit', {
            parent: 'definition-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/definition/definition-dialog.html',
                    controller: 'DefinitionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Definition', function(Definition) {
                            return Definition.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('definition.new', {
            parent: 'definition',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/definition/definition-dialog.html',
                    controller: 'DefinitionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                code: null,
                                originalText: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('definition', null, { reload: 'definition' });
                }, function() {
                    $state.go('definition');
                });
            }]
        })
        .state('definition.edit', {
            parent: 'definition',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/definition/definition-dialog.html',
                    controller: 'DefinitionDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Definition', function(Definition) {
                            return Definition.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('definition', null, { reload: 'definition' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('definition.delete', {
            parent: 'definition',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/definition/definition-delete-dialog.html',
                    controller: 'DefinitionDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Definition', function(Definition) {
                            return Definition.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('definition', null, { reload: 'definition' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
