(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('release', {
            parent: 'entity',
            url: '/release',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Releases'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/release/releases.html',
                    controller: 'ReleaseController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('release-detail', {
            parent: 'entity',
            url: '/release/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Release'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/release/release-detail.html',
                    controller: 'ReleaseDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Release', function($stateParams, Release) {
                    return Release.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'release',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('release-detail.edit', {
            parent: 'release-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-dialog.html',
                    controller: 'ReleaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Release', function(Release) {
                            return Release.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('release.new', {
            parent: 'release',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-dialog.html',
                    controller: 'ReleaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                description: null,
                                versionTag: null,
                                isCurrentRelease: false,
                                dueDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('release', null, { reload: 'release' });
                }, function() {
                    $state.go('release');
                });
            }]
        })
        .state('release.edit', {
            parent: 'release',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-dialog.html',
                    controller: 'ReleaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Release', function(Release) {
                            return Release.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('release', null, { reload: 'release' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('release.delete', {
            parent: 'release',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/release/release-delete-dialog.html',
                    controller: 'ReleaseDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Release', function(Release) {
                            return Release.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('release', null, { reload: 'release' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
