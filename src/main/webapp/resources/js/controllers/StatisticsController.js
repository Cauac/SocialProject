'use strict';

/**
 * StatisticsController
 * @constructor
 */
var StatisticsController = function ($scope, $http, $cookieStore) {

    $scope.fetchStatistic = function () {
        $http.get('statistic/getStatistic.json').success(function (data) {
            $scope.data = data;
            $('#container').highcharts({
                chart: {
                    type: 'bar'
                },
                title: {
                    text: 'Fave and comment your photos statistic'
                },
                xAxis: {
                    categories: data.names,
                    title: {
                        text: null
                    }
                },
                yAxis: {
                    min: 0,
                    labels: {
                        overflow: 'justify'
                    }
                },
                plotOptions: {
                    bar: {
                        dataLabels: {
                            enabled: true
                        }
                    }
                },
                legend: {
                    layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'top',
                    x: -40,
                    y: 100,
                    floating: true,
                    borderWidth: 1,
                    backgroundColor: '#FFFFFF',
                    shadow: true
                },
                credits: {
                    enabled: false
                },
                series: [
                    {
                        name: 'Fave',
                        data: data.faves
                    },
                    {
                        name: 'Comments',
                        data: data.comments
                    }
                ]
            });
            $('#containerPhoto').highcharts({
                chart: {
                    type: 'bar'
                },
                title: {
                    text: 'Fave and comment your photos statistic'
                },
                xAxis: {
                    categories: data.photos,
                    title: {
                        text: null
                    }
                },
                yAxis: {
                    min: 0,
                    labels: {
                        overflow: 'justify'
                    }
                },
                plotOptions: {
                    bar: {
                        dataLabels: {
                            enabled: true
                        }
                    }
                },
                legend: {
                    layout: 'vertical',
                    align: 'right',
                    verticalAlign: 'top',
                    x: -40,
                    y: 100,
                    floating: true,
                    borderWidth: 1,
                    backgroundColor: '#FFFFFF',
                    shadow: true
                },
                credits: {
                    enabled: false
                },
                series: [
                    {
                        name: 'Fave',
                        data: data.photoFaves
                    },
                    {
                        name: 'Comments',
                        data: data.photoComments
                    }
                ]
            });

        });
    }
    $cookieStore.put('lastPage', '/statistic');
    $scope.fetchStatistic();
}