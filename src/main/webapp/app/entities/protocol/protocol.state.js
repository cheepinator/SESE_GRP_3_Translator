(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('protocol', {
            parent: 'entity',
            url: '/protocol',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Protocols'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/protocol/protocols.html',
                    controller: 'ProtocolController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })


    }

})();
