'use strict';

/**
 * FileUploadController
 * @constructor
 */
var FileUploadController = function ($scope, $upload, $modal) {

    $scope.percent = 0;
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
            $scope.upload = $upload.upload({
                url: 'uploadPaymentFile.' + type,
                data: {myObj: $scope.myModelObj},
                file: file
            }).progress(function (evt) {
                    $scope.percent = parseInt(100.0 * evt.loaded / evt.total)
                }).success(function () {
                    $scope.successMessage = true;
                });
        }
    };

    $scope.getType = function (file) {
        if (file.type == 'text/xml') {
            return 'xlm';
        }
        if (file.type == 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
            return 'xlsx';
        }
    }
}
