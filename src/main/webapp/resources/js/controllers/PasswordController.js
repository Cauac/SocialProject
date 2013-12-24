'use strict';

/**
 * PasswordController
 * @constructor
 */
var PasswordController = function ($scope, $http) {

    $scope.mail;

    $scope.restore = function () {
        $http.post('/sendCredentials?mail=' + $scope.mail)
            .success(function () {
                $scope.alertMessage=false;
            }).error(function () {
                $scope.alertMessage=true;
            });
        console.log('message sending');
    };

};
