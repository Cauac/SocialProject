'use strict';

/**
 * FileUploadController
 * @constructor
 */
var FileUploadController = function ($scope, $http) {

    $scope.file;
    $scope.alertMessage = false;
    $scope.successMessage = false;

    $scope.onFileSelect = function ($files) {
        //$files: an array of files selected, each file has name, size, and type.
        for (var i = 0; i < $files.length; i++) {
            var file = $files[i];
            var type = $scope.getType(file);
            if (!type) {
                $scope.alertMessage = true;
                return;
            }
            $http.post('uploadPaymentFile.' + type, file).success(function () {
                $scope.successMessage = true;
            });
        }
    };

    $scope.getType = function (file) {
        if (file.type == 'text/xml') {
            return 'xml';
        }
        if (file.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
            return 'xlsx';
        }
        if (file.type == 'application/vnd.ms-excel') {
            return 'csv';
        }
    }
}
