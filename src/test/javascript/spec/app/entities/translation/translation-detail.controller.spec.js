'use strict';

describe('Controller Tests', function() {

    describe('Translation Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockTranslation, MockUser, MockLanguage, MockDefinition;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockTranslation = jasmine.createSpy('MockTranslation');
            MockUser = jasmine.createSpy('MockUser');
            MockLanguage = jasmine.createSpy('MockLanguage');
            MockDefinition = jasmine.createSpy('MockDefinition');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Translation': MockTranslation,
                'User': MockUser,
                'Language': MockLanguage,
                'Definition': MockDefinition
            };
            createController = function() {
                $injector.get('$controller')("TranslationDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'seseTranslatorApp:translationUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
