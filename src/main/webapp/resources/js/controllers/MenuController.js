'use strict';

/**
 * MenuController
 * @constructor
 */
var MenuController = function ($scope, $location, AuthenticationService, SessionService) {

    $scope.username;
    $scope.role;

    $scope.initMenu = function () {
        $scope.username = AuthenticationService.getLogin();
        $scope.role = AuthenticationService.getRole();
        $("div.btn-group >a").removeClass('active');
        var path = $location.path();
        path = path.replace('/', '');
        $("#" + path).addClass('active');
    }

    $scope.logout = function () {
        AuthenticationService.logout().success(function () {
            $location.path('/login')
        });
    };

    $scope.initMenu();
};

