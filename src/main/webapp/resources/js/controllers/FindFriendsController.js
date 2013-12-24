'use strict';

/**
 * FindFriendsController
 * @constructor
 */
var FindFriendsController = function($scope, $http) {

    $scope.fetchFriendsList = function() {
        $http.get('findFriends/getPossibleFriends.json').success(function(friends){
            $scope.friends = friends;
        });
    }

    $scope.fetchFriendsList();
}
