'use strict';

/**
 * PhotoController
 * @constructor
 */

var PhotoController = function ($scope, $http, $modal, $cookieStore, AuthenticationService) {

    $scope.totalSize = 0;
    $scope.newPhotoCount = 0;
    $scope.currentPage = 1;
    $scope.itemPerPage = 4;
    $scope.maxSize = 10;
    $scope.login = AuthenticationService.getLogin();

    $scope.fetchPhotosList = function () {
        if (localStorage.getItem($scope.login + '_photoPage' + $scope.currentPage)) {
            $scope.photos = $.parseJSON(localStorage.getItem($scope.login + '_photoPage' + $scope.currentPage));
        } else {
            $http.get('photos/getPhotosList.json?pageSize=' + $scope.itemPerPage + '&pageNum=' + $scope.currentPage).success(function (photosList) {
                $scope.photos = photosList;
                localStorage.setItem($scope.login + '_photoPage' + $scope.currentPage, JSON.stringify(photosList));
            });
        }
    }

    $scope.getPhotoCount = function () {
        $http.get('photos/getPhotoCount').success(function (count) {
            if ($scope.totalSize != 0 && $scope.totalSize != count) {
                $scope.newPhotoCount = count - $scope.totalSize;
            }
            if ($scope.newPhotoCount > 0) {
                $("#head").css("background-color", "red");
            }
            $scope.totalSize = count
        });
    }

    $scope.pageChanged = function (page) {
        $scope.currentPage = page;
        $scope.fetchPhotosList();
        $scope.getPhotoCount();
    };

    $scope.uploadNewPhoto = function () {
        for (var i = 1; i < $scope.totalSize; i++) {
            localStorage.removeItem('photoPage' + i);
        }
        $scope.totalSize = 0;
        $scope.newPhotoCount = 0;
        $("#head").css("background-color", "rgb(66, 139, 202)");
    }

    $scope.getPhotoCount();
    $scope.fetchPhotosList();
    $cookieStore.put('lastPage', '/photos');

    $scope.openComments = function (photo) {
        $http.get('photos/getCommentsByPhoto?photoId=' + photo._id).success(function (result) {
            $scope.comments = result.comments;
            $scope.dates = result.dates;
            $scope.values = result.values;
            var modalInstance = $modal.open({
                templateUrl: 'resources/html/photoDetails.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    comments: function () {
                        return $scope.comments;
                    },
                    dates: function () {
                        return $scope.dates;
                    },
                    values: function () {
                        return $scope.values;
                    }
                }
            });
        });
    };
};

var ModalInstanceCtrl = function ($scope, $modalInstance, comments, values, dates) {

    $scope.comments = comments;

    $scope.initChart = function () {
        $('#chartContainer').highcharts({
            chart: {
                type: 'area'
            },
            title: {
                text: 'Comments statistic'
            },
            xAxis: {
                categories: dates,
                tickmarkPlacement: 'on',
                title: {
                    enabled: false
                }
            },
            tooltip: {
                shared: true
            },
            plotOptions: {
                area: {
                    stacking: 'normal',
                    lineColor: '#666666',
                    lineWidth: 1,
                    marker: {
                        lineWidth: 1,
                        lineColor: '#666666'
                    }
                }
            },
            series: [
                {
                    name: 'Comment',
                    data: values
                }
            ]
        });
    };

    $scope.ok = function () {
        $modalInstance.close();
    };
};
