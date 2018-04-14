


var app = angular.module("loggedInPage",["chart.js"]);

app.controller("controller1",function ($scope,$http) {
    $scope.getGraph1 = function () {
        $http.get('/graph1').success(function (stats) {
            $scope.labels1 =[];
            $scope.data1=[];
            for(var i in stats){
                $scope.labels1.push(i);
                $scope.data1.push(stats[i]);
            }
        });
    };

        $scope.getGraph2 = function () {
            $http.get('/graph2').success(function (stats) {
                $scope.labels2 =[];
                $scope.data2=[];
                for(var i in stats){
                    $scope.labels2.push(i);
                    $scope.data2.push(stats[i]);
                }
            });
        };

                $scope.getGraph3 = function () {
                    $http.get('/graph3').success(function (stats) {
                        $scope.labels3 =[];
                        $scope.data3=[];
                        for(var i in stats){
                            $scope.labels3.push(i);
                            $scope.data3.push(stats[i]);
                        }
                    });
                };
        $scope.getGraph4 = function () {
        $http.get('/graph4').success(function (stats) {
        $scope.labels4 =[];
        $scope.data4=[];
        for(var i in stats){
        $scope.labels4.push(i);
        $scope.data4.push(stats[i]);
                }
            });
         }

});


