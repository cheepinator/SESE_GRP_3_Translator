(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('projectassignment', {
            parent: 'entity',
            url: '/projectassignment',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Projectassignments'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/projectassignment/projectassignments.html',
                    controller: 'ProjectassignmentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('projectassignment-detail', {
            parent: 'entity',
            url: '/projectassignment/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Projectassignment'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/projectassignment/projectassignment-detail.html',
                    controller: 'ProjectassignmentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Projectassignment', function($stateParams, Projectassignment) {
                    return Projectassignment.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'projectassignment',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('projectassignment-detail.edit', {
            parent: 'projectassignment-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/projectassignment/projectassignment-dialog.html',
                    controller: 'ProjectassignmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Projectassignment', function(Projectassignment) {
                            return Projectassignment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('projectassignment.new', {
            parent: 'projectassignment',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/projectassignment/projectassignment-dialog.html',
                    controller: 'ProjectassignmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                role: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('projectassignment', null, { reload: 'projectassignment' });
                }, function() {
                    $state.go('projectassignment');
                });
            }]
        })
        .state('projectassignment.edit', {
            parent: 'projectassignment',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/projectassignment/projectassignment-dialog.html',
                    controller: 'ProjectassignmentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Projectassignment', function(Projectassignment) {
                            return Projectassignment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('projectassignment', null, { reload: 'projectassignment' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('projectassignment.delete', {
            parent: 'projectassignment',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/projectassignment/projectassignment-delete-dialog.html',
                    controller: 'ProjectassignmentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Projectassignment', function(Projectassignment) {
                            return Projectassignment.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('projectassignment', null, { reload: 'projectassignment' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
