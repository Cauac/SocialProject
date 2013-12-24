'use strict';

/**
 * AdminController
 * @constructor
 */
var AdminController = function ($scope, $http, $modal) {

    $scope.fetchUserList = function () {
        $http.get('admin/getUsersList.json').success(function (users) {
            $scope.users = users;
        });
    }
    $scope.fetchUserList();

    $scope.ban = function (user) {
        $http.post('admin/banUser?username=' + user.username).success(function () {
            user.banned = true;
            $( "#"+user.username ).removeClass("active");
            $( "#"+user.username ).addClass("danger");
        });
    };

    $scope.unban = function (user) {
        $http.post('admin/unbanUser?username=' + user.username).success(function () {
            user.banned = false;
            $( "#"+user.username ).addClass("active");
            $( "#"+user.username ).removeClass("danger");
        });
    };

    $scope.openAuditList = function (user) {
        $http.get('admin/getAuditByUser.json?username=' + user.username).success(function (auditList) {
            $scope.audits = auditList;
            var modalInstance = $modal.open({
                templateUrl: 'resources/html/auditList.html',
                controller: ModalInstanceCtrl2,
                resolve: {
                    audits: function () {
                        return $scope.audits;
                    }
                }
            });
        });
    };
};

var ModalInstanceCtrl2 = function ($scope, $modalInstance, audits) {

    $scope.audits = audits;

    $scope.ok = function () {
        $modalInstance.close();
    };
};
