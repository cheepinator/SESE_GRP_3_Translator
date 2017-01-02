(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('project-detail.release', {
                parent: 'project-detail',
                url: '/release',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Releases'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/project/release/releases.html',
                        controller: 'ReleaseController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    previousState: ["$state", function ($state) {
                        var currentStateData = {
                            name: $state.current.name || 'project-detail',
                            params: $state.params,
                            url: $state.href($state.current.name, $state.params)
                        };
                        // fix for issue of sub pages overriding the correct previous sate in modal dialogs via 'reload' current page
                        if (currentStateData.name.includes('release')) {
                            currentStateData.name = 'project-detail'
                        }
                        return currentStateData;
                    }]
                }
            })
            .state('project-detail.release.new', {
            parent: 'project-detail.release',
            url: '/new',
            params: {
                projetcId: null
            },
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/project/release/release-dialog.html',
                    controller: 'ReleaseDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        project: ['$stateParams', 'Project', function ($stateParams, Project) {
                            return Project.get({id: $stateParams.projectId}).$promise;
                        }],
                        entity: function () {
                            return {
                                description: null,
                                versionTag: null,
                                dueDate: null,
                            };
                        }

                    }
                }).result.then(function () {
                    $state.go('project-detail.release', null, {reload: 'project-detail.release' && 'project-detail'});
                }, function () {
                    $state.go('project-detail.release');
                });
            }]
        })
            .state('project-detail.release.delete', {
                parent: 'project-detail.release',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/release/release-delete-dialog.html',
                        controller: 'ReleaseDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['Release', function (Release) {
                                return Release.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('project-detail.release', null, {reload: 'project-detail.release' && 'project-detail'});
                    }, function () {
                        $state.go('^');
                    });
                }]
            })
            .state('project-detail.release-detail', {
                parent: 'project-detail.release',
                url: '/release/{id}',
                params: {
                    projetcId: null
                },
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Release'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/project/release/release-detail.html',
                        controller: 'ReleaseDetailController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'Release', function ($stateParams, Release) {
                        return Release.get({id: $stateParams.id}).$promise;
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
            .state('project-detail.release-detail.edit', {
                parent: 'project-detail.release-detail',
                url: '/detail/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/release/release-dialog.html',
                        controller: 'ReleaseDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Release', function (Release) {
                                return Release.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('project-detail.release', null, {reload: 'project-detail.release' && 'project-detail'});
                    }, function () {
                        $state.go('project-detail.release');
                    });
                }]
            })
            .state('project-detail.release.edit', {
                parent: 'project-detail.release',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/release/release-dialog.html',
                        controller: 'ReleaseDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['Release', function (Release) {
                                return Release.get({id: $stateParams.id}).$promise;
                            }]
                        }
                    }).result.then(function () {
                        $state.go('project-detail.release', null, {reload: 'project-detail.release' && 'project-detail'});
                    }, function () {
                        $state.go('project-detail.release');
                    });
                }]
            });
    }

})();
