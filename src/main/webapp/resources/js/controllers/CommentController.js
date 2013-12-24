'use strict';

/**
 * CommentController
 * @constructor
 */
var CommentController = function ($scope, $http, $cookieStore, $modal) {

    $scope.fetchCommentsList = function () {
        $http.get('comments/getCommentsList.json').success(function (commentsList) {
            $scope.comments = commentsList;
        });
    }
    $cookieStore.put('lastPage', '/comments');
    $scope.fetchCommentsList();

    $scope.openComments = function (photo) {
        $http.get('photos/getCommentsByPhoto?photoId=' + photo._id).success(function (result) {
            $scope.dates = result.dates;
            $scope.values = result.values;
            var modalInstance = $modal.open({
                templateUrl: 'resources/html/photoDetails.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    comments: function () {
                        return result.comments;
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
