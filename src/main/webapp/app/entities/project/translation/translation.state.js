(function () {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('project-detail.translation', {
                parent: 'project-detail',
                url: '/translation',
                params: {
                    curReleaseId: null,
                    languageId: null
                },
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'Translation'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/project/translation/translation.html',
                        controller: 'TranslatorController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    project: ['$stateParams', 'Project', function($stateParams, Project) {
                        return Project.get({id : $stateParams.projectId}).$promise;
                    }],
                    currentRelease: ['$stateParams', 'Release', function ($stateParams, Release) {
                        if($stateParams.curReleaseId){
                            return Release.get({id: $stateParams.curReleaseId}).$promise;
                        }
                        else{
                            return "";
                        }
                    }],
                    language:['$stateParams', 'Language', function ($stateParams, Language) {
                        return Language.get({id: $stateParams.languageId}).$promise;
                    }],

                    nextTranslations:['$stateParams','NextTranslation', function ($stateParams,NextTranslation) {
                        return NextTranslation.query(
                            {
                                releaseId:$stateParams.curReleaseId,
                                languageId:$stateParams.languageId
                            }
                        ).$promise;
                    }],
                    previousState: ["$state", function ($state) {
                        var currentStateData = {
                            name: $state.current.name || 'project-detail',
                            params: $state.params,
                            url: $state.href($state.current.name, $state.params)
                        };
                        // fix for issue of sub pages overriding the correct previous sate in modal dialogs via 'reload' current page
                        if (currentStateData.name.includes('translation')) {
                            currentStateData.name = 'project-detail'
                        }
                        return currentStateData;
                    }]


                }
            });

    }

})();
