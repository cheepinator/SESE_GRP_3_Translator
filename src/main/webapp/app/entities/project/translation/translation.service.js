(function() {
    'use strict';
    angular
        .module('seseTranslatorApp')
        .factory('NextTranslation', NextTranslation);

    NextTranslation.$inject = ['$resource'];


    function NextTranslation ($resource) {
        var resourceUrl = 'api/release/next_translation';
        return $resource(resourceUrl, {}, {
            'query': { method: 'POST', isArray: true}
        });
    }


})();
