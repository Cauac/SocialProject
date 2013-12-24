'use strict';

/**
 * ErrorController
 * @constructor
 */
var ErrorController = function ($scope, $http) {

    $scope.fetchErrorsList = function () {
        $http.get('errors/getErrors.json').success(function (errorsList) {
            $scope.errors = errorsList;
        });
    }
    $scope.fetchErrorsList();
};
