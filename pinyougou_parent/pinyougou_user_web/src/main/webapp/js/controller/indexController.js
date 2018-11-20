app.controller("indexController",function ($scope,$controller,loginService) {

    //angularjs继承代码实现  参数一：父对象名称  参数二：共享$scope配置
    $controller("baseController",{$scope:$scope});

    //获取登录名
    $scope.getName=function () {
        loginService.getName().success(function (response) {
            //{loginName:"张三"}
            $scope.loginName=response.loginName;
        })
    }

});