(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectController', ProjectController);

    ProjectController.$inject = ['$scope', '$state', 'Project', 'Language', 'Release'];

    function ProjectController ($scope, $state, Project, Language, Release) {
        var vm = this;

        vm.projects = [];
        vm.languages= [];
        vm.releases = [];

        loadAll();

        function loadAll() {
            Project.query(function(result) {
                vm.projects = result;
        });
            Language.query(function (result) {
                vm.languages = result
            });

            Release.query(function (result) {
                vm.releases = result
            });

        }

        function getCurrentReleaseByProjectID(id) {
            releases.forEach(function (entry) {
                if(entry.project_id == id && entry.is_current_release){
                    console.log(entry.id + "wurde geloggt") ;

                }
            })
        }

    }



})();
