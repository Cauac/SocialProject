'use strict';

/**
 * FriendController
 * @constructor
 */
var FriendController = function ($scope, $http, $cookieStore) {

    $scope.totalSize;
    $scope.currentPage = 1;
    $scope.itemPerPage = 10;
    $scope.maxSize = 10;
    $scope.usernameSearch = '';

    $scope.fetchFriendsList = function () {
        $http.get('friends/getFriends.json?pageSize=' + $scope.itemPerPage + '&pageNum=' + $scope.currentPage + '&search=' + $scope.usernameSearch)
            .success(function (response) {
                $scope.friends = response.data;
                $scope.totalSize = response.count;
            });
    }

    $scope.pageChanged = function (page) {
        $scope.currentPage = page;
        $scope.fetchFriendsList();
    };

    $scope.search = function () {
        $scope.currentPage = 1;
        $scope.fetchFriendsList();
    };
    $cookieStore.put('lastPage', '/friends');
    $scope.fetchFriendsList();
}
