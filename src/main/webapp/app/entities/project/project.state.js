(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project', {
            parent: 'entity',
            url: '/project',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Projects'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/project/projects.html',
                    controller: 'ProjectController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('project-detail', {
            parent: 'entity',
            url: '/project/{projectId}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Project'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/project/project-detail.html',
                    controller: 'ProjectDetailController',
                    controllerAs: 'vm'
                },
                'definitions@project-detail': {
                    templateUrl: 'app/entities/project/definition/definitions.html',
                    controller: 'ProjectDefinitionController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                project: ['$stateParams', 'Project', function($stateParams, Project) {
                    return Project.get({id : $stateParams.projectId}).$promise;
                }],
                projectReleases: ['$stateParams', 'ProjectReleases', function ($stateParams, ProjectReleases) {
                    return ProjectReleases.query({projectId: $stateParams.projectId}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    // fix for issue of sub pages overriding the correct previous sate in modal dialogs via 'reload' current page
                    if (currentStateData.name.startsWith('project-detail')) {
                        currentStateData.name = 'project'
                    }
                    return currentStateData;
                }]
            }
        })
        .state('project-detail.edit', {
            parent: 'project-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/project/project-dialog.html',
                    controller: 'ProjectDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        project: ['Project', function(Project) {
                            return Project.get({id : $stateParams.projectId}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project.new', {
            parent: 'project',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/project/project-dialog.html',
                    controller: 'ProjectDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        project: function () {
                            return {
                                name: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('project', null, { reload: 'project' });
                }, function() {
                    $state.go('project');
                });
            }]
        })
        .state('project.edit', {
            parent: 'project',
            url: '/{projectId}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/project/project-dialog.html',
                    controller: 'ProjectDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        project: ['Project', function(Project) {
                            return Project.get({id : $stateParams.projectId}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project', null, { reload: 'project' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project.delete', {
            parent: 'project',
            url: '/{projectId}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/project/project-delete-dialog.html',
                    controller: 'ProjectDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        project: ['Project', function(Project) {
                            return Project.get({id : $stateParams.projectId}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project', null, { reload: 'project' });
                }, function() {
                    $state.go('^');
                });
            }]
        }).state('project-detail.open-translation', {
                parent: 'project-detail',
                url: '/start-translating',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/project/project-translation-dialog.html',
                        controller: 'ProjectTranslationDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'sm',
                        windowClass: 'app-modal-window',
                        resolve: {
                            releases: ['Release', function(Release) {
                                return Release.query({projectId : $stateParams.projectId}).$promise;
                            }],
                            languages: ['Language', function(Language) {
                                return Language.query().$promise;
                            }]
                        }
                    }).result.then(function() {
                    }, function() {
                        $state.go('^');
                    });
                }]
            });
    }

})();
