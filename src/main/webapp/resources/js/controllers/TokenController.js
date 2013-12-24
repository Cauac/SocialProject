'use strict';

/**
 * TokenController
 * @constructor
 */
var TokenController = function ($scope, $http, $cookieStore) {

    $scope.getAccess = function () {
        $http.get('/getFlickrAuthUrl').success(function (url) {
            window.location.href = url;
        });
    };

    $scope.getTokens = function () {
        $http.get('getFlickrTokenForCurrentUser').success(function (result) {
            $scope.token = result;
        });
    }
    $cookieStore.put('lastPage', '/tokens');
    $scope.getTokens();
}
