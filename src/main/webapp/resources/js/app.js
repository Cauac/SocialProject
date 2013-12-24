'use strict';

var AngularSpringApp = {};

var App = angular.module('AngularSpringApp', ['AngularSpringApp.filters', 'AngularSpringApp.services', 'AngularSpringApp.directives', 'ui.bootstrap.modal', 'ui.bootstrap.pagination', 'ngCookies','angularFileUpload']);

App.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/photos', {
        templateUrl: 'resources/html/secure/photos.html',
        controller: PhotoController
    });

    $routeProvider.when('/comments', {
        templateUrl: 'resources/html/secure/comments.html',
        controller: CommentController
    });

    $routeProvider.when('/404', {
        templateUrl: 'resources/html/404.html'
    });

    $routeProvider.when('/tokens', {
        templateUrl: 'resources/html/secure/tokens.html',
        controller: TokenController
    });

    $routeProvider.when('/friends', {
        templateUrl: 'resources/html/secure/friends.html',
        controller: FriendController
    });

    $routeProvider.when('/possibleFriends', {
        templateUrl: 'resources/html/secure/possibleFriends.html',
        controller: FindFriendsController
    });

    $routeProvider.when('/statistic', {
        templateUrl: 'resources/html/secure/statistic.html',
        controller: StatisticsController
    });

    $routeProvider.when('/forgetPassword', {
        templateUrl: 'resources/html/restore.html',
        controller: PasswordController
    });

    $routeProvider.when('/errors', {
        templateUrl: 'resources/html/secure/errors.html',
        controller: ErrorController
    });

    $routeProvider.when('/admin', {
        templateUrl: 'resources/html/secure/admin.html',
        controller: AdminController
    });

    $routeProvider.when('/login', {
        templateUrl: 'resources/html/login.html',
        controller: LoginController
    });

    $routeProvider.otherwise({redirectTo: '/login'});
}]);

App.config(function ($httpProvider) {

    var logsOutUserOn401 = function ($location, $q, SessionService) {
        var success = function (response) {
            return response;
        };

        var error = function (response) {
            if (response.status === 401) {
                SessionService.unset('authenticated');
                $location.path('/login');
            }
            return $q.reject(response);
        };

        return function (promise) {
            return promise.then(success, error);
        };
    };

    $httpProvider.responseInterceptors.push(logsOutUserOn401);

});

App.run(function ($rootScope, $location, AuthenticationService, $cookieStore) {
    $rootScope.$on('$routeChangeStart', function (event, next, current) {

        if (!AuthenticationService.isLoggedIn()) {
            if ('/login' == $location.path()) {
                return;
            }
            if ('/forgetPassword' == $location.path()) {
                return;
            }
            $location.path('/login');
            return;
        }

        if ('/login' == $location.path()) {
            if (AuthenticationService.isAdmin()) {
                $location.path('/admin');
                return;
            }
            if ($cookieStore.get('lastPage')) {
                $location.path($cookieStore.get('lastPage'));
            }
        }
    });
});

App.factory("SessionService", function () {
    return {
        get: function (key) {
            return sessionStorage.getItem(key);
        },
        set: function (key, val) {
            return sessionStorage.setItem(key, val);
        },
        unset: function (key) {
            return sessionStorage.removeItem(key);
        }
    }
});

App.factory("AuthenticationService", function ($rootScope, $http, SessionService, $cookieStore, $location) {

    var cacheSession = function (userInfo) {
        $cookieStore.put('REMEMBER_ME', userInfo.remember_me);
        SessionService.set('username', userInfo.username);
        SessionService.set('role', userInfo.role);
    };

    var uncacheSession = function () {
        $cookieStore.remove('REMEMBER_ME');
        SessionService.unset('username');
        SessionService.unset('role');
    };

    var refreshUserInfo = function () {
        $http.get("getUserInfo").success(function (userInfo) {
            if (userInfo.role == 'ROLE_ANONYMOUS') {
                return;
            }

            cacheSession(userInfo);

            if (userInfo.role == 'ROLE_ADMIN') {
                $location.path('/admin');
                return;
            }
            if ($cookieStore.get('lastPage')) {
                $location.path($cookieStore.get('lastPage'));
            } else {
                $location.path('/photos');
            }
        });
    }

    return {
        login: function (credentials) {
            var payload = $.param({j_username: credentials.username, j_password: credentials.password, _spring_security_remember_me: credentials.remember_me});
            var config = {
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            }
            var login = $http.post("j_spring_security_check", payload, config);
            login.success(refreshUserInfo);
            return login;
        },

        facebookLogin: function (response) {
            var payload = $.param({profile_id: response.id, service_name: 'facebook'});
            var config = {
                headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
            }
            var login = $http.post("j_spring_service_security_check", payload, config);
            login.success(refreshUserInfo);
            return login;
        },

        logout: function () {
            var logout = $http.get("j_spring_security_logout");
            logout.success(uncacheSession);
            return logout;
        },
        isLoggedIn: function () {
            return SessionService.get('username');
        },
        isAdmin: function () {
            return SessionService.get('role') == 'ROLE_ADMIN';
        },
        getLogin: function () {
            return SessionService.get('username');
        },
        getRole: function () {
            return SessionService.get('role');
        },
        tryLogin: function () {
            refreshUserInfo();
        }
    }
});

App.directive("watchAutofill", [
    '$timeout',
    function ($timeout) {
        var INTERVAL_MS = 500;

        return {
            require: 'ngModel',
            link: function (scope, element, attrs, ngModel) {

                var timer;

                function startTimer() {
                    timer = $timeout(function () {
                        var value = element.val();
                        if (value && ngModel.$viewValue !== value) {
                            ngModel.$setViewValue(value);
                        }
                        startTimer();
                    }, INTERVAL_MS);
                }

                scope.$on('$destroy', function () {
                    $timeout.cancel(timer);
                });

                startTimer();
            }
        };
    }
]);

