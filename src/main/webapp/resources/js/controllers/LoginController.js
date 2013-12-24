'use strict';

/**
 * LoginController
 * @constructor
 */
var LoginController = function ($rootScope, $scope, $http, AuthenticationService, $window) {

    $scope.credentials = { username: "", password: "", remember_me: false };

    var errorLogin = function (data, status, headers, config) {
        if (status == 401) {
            $scope.alertMessage = "Username does not exist";
        }
        if (status == 403) {
            $scope.alertMessage = "User was banned";
        }
    };

    $scope.login = function () {
        AuthenticationService.login($scope.credentials).error(errorLogin);
    };

    $scope.loginWithFacebook = function () {
        FB.api('/me', function (response) {
            AuthenticationService.facebookLogin(response).error(errorLogin);
        });
    };

    $scope.loginWithTwitter = function () {
        $http.get('/tw/getAuthUrl').success(function (url) {
            window.location.href = url;
        });
    }

    $scope.initFB = function () {
        $window.fbAsyncInit = function () {
            FB.init({
                appId: '234252656753529',
                status: true, // check login status
                cookie: true, // enable cookies to allow the server to access the session
                xfbml: true  // parse XFBML
            });

            FB.Event.subscribe('auth.authResponseChange', function (response) {
                if (response.status === 'connected') {
                    $scope.loginWithFacebook();
                } else if (response.status === 'not_authorized') {
                    FB.login();
                } else {
                    FB.login();
                }
            });
        };

        (function (d) {
            var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
            if (d.getElementById(id)) {
                return;
            }
            js = d.createElement('script');
            js.id = id;
            js.async = true;
            js.src = "//connect.facebook.net/en_US/all.js";
            ref.parentNode.insertBefore(js, ref);
        }(document));
    };

    $scope.initFB();
    AuthenticationService.tryLogin();
};
