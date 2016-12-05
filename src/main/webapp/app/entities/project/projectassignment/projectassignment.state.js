(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-detail.projectassignment', {
            parent: 'project-detail',
            url: '/projectassignment',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Projectassignments'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/project/projectassignment/projectassignments.html',
                    controller: 'ProjectassignmentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('project-detail.projectassignment.new', {
            parent: 'project-detail.projectassignment',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/project/projectassignment/projectassignment-dialog.html',
                    controller: 'ProjectassignmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        project: ['$stateParams', 'Project', function($stateParams, Project) {
                            return Project.get({id : $stateParams.projectId}).$promise;
                        }],
                        entity: function () {
                            return {
                                role: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('project-detail.projectassignment', null, { reload: 'project-detail.projectassignment' });
                }, function() {
                    $state.go('project-detail.projectassignment');
                });
            }]
        })
        .state('project-detail.projectassignment.delete', {
            parent: 'project-detail.projectassignment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/project/projectassignment/projectassignment-delete-dialog.html',
                    controller: 'ProjectassignmentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Projectassignment', function(Projectassignment) {
                            return Projectassignment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-detail.projectassignment', null, { reload: 'project-detail.projectassignment' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
