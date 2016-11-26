'use strict';

describe('Controller Tests', function() {

    describe('Definition Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockDefinition, MockTranslation, MockRelease;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockDefinition = jasmine.createSpy('MockDefinition');
            MockTranslation = jasmine.createSpy('MockTranslation');
            MockRelease = jasmine.createSpy('MockRelease');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Definition': MockDefinition,
                'Translation': MockTranslation,
                'Release': MockRelease
            };
            createController = function() {
                $injector.get('$controller')("DefinitionDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'seseTranslatorApp:definitionUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
