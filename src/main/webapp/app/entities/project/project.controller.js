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

        $scope.getCurrentReleaseByProjectID = getCurrentReleaseByProjectID;

        function getCurrentReleaseByProjectID(id) {
            var currentReleases = this;
            $scope.currentRelease = null;
            vm.releases.forEach(function (entry) {
                console.log("Aufgerufen mit id: "+id + " Entry PID "+entry.projectId+ " entryiscurrent: "+entry.isCurrentRelease +" Entry: "+entry)
                if(entry.projectId == id && entry.isCurrentRelease){
                    console.log(entry.description + " wurde geloggt") ;
                    $scope.currentRelease = entry;
                }

            })
        }

    }



})();
