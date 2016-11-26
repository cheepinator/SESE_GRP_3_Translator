(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('language', {
            parent: 'entity',
            url: '/language',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Languages'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/language/languages.html',
                    controller: 'LanguageController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('language-detail', {
            parent: 'entity',
            url: '/language/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Language'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/language/language-detail.html',
                    controller: 'LanguageDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Language', function($stateParams, Language) {
                    return Language.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'language',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('language-detail.edit', {
            parent: 'language-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/language/language-dialog.html',
                    controller: 'LanguageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Language', function(Language) {
                            return Language.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('language.new', {
            parent: 'language',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/language/language-dialog.html',
                    controller: 'LanguageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                code: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('language', null, { reload: 'language' });
                }, function() {
                    $state.go('language');
                });
            }]
        })
        .state('language.edit', {
            parent: 'language',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/language/language-dialog.html',
                    controller: 'LanguageDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Language', function(Language) {
                            return Language.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('language', null, { reload: 'language' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('language.delete', {
            parent: 'language',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/language/language-delete-dialog.html',
                    controller: 'LanguageDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Language', function(Language) {
                            return Language.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('language', null, { reload: 'language' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
