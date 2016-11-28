(function() {
    'use strict';

    angular
        .module('seseTranslatorApp')
        .controller('ProjectController', ProjectController);

    ProjectController.$inject = ['$scope', '$state', 'Project', 'Language', 'Release', 'CountTranslations'];

    function ProjectController ($scope, $state, Project, Language, Release, CountTranslations) {
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

        $scope.getCurrentReleaseByProjectID = getCurrentReleaseByProjectID;

        function getCurrentReleaseByProjectID(id) {

            $scope.currentRelease = null;
            vm.releases.forEach(function (entry) {
                console.log("Aufgerufen mit id: "+id + " Entry PID "+entry.projectId+ " entryiscurrent: "+entry.isCurrentRelease +" Entry: "+entry)
                if(entry.projectId == id && entry.isCurrentRelease){
                    console.log(entry.description + " wurde geloggt") ;
                    $scope.currentRelease = entry;
                }

            })
        }

        $scope.getCountOfTranslationsByReleaseID = getCountOfTranslationsByReleaseID;

        function getCountOfTranslationsByReleaseID(id) {
            console.log("looking for id "+ id);
            $scope.countOfTranslations =  CountTranslations.get(0);
        }



    }



})();
