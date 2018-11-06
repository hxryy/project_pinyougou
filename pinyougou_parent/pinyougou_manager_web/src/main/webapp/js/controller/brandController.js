//定义控制器 参数一：控制器名称  参数二：控制器要做的事情
//$controller实现angularjs继承机制
app.controller("brandController",function ($scope,$controller,brandService) {

    //angularjs继承代码实现  参数一：父对象名称  参数二：共享$scope配置
    $controller("baseController",{$scope:$scope});


    //$scope 是angularjs内置对象，$scope可以理解为angularjs的js代码与html代码数据交互的桥梁
    //[]在json格式数据中代表数组，{}在json格式数据中代表对象

    //查询所有品牌数据
    $scope.findAll=function () {
        brandService.findAll().success(function (response) {
            //基于$http内置对象，发起http请求后端，获取数据
            //声明变量接收响应结果
            $scope.list=response;
        })
    }

    $scope.searchEntity={};
    //分页查询
    $scope.search=function (pageNum,pageSize) {
        brandService.findPage(pageNum,pageSize,$scope.searchEntity).success(function (response) {
            $scope.paginationConf.totalItems=response.total;//总记录数
            $scope.list=response.rows;//当前页查询结果集
        })
    }

    $scope.entity={};

    //保存品牌
    $scope.save=function () {

        var method=null;
        if($scope.entity.id !=null){
            //当id存在时，执行修改
            method=brandService.update($scope.entity);
        }else {
            //当id不存在时，执行新增
            method=brandService.add($scope.entity);
        }

        //{name:'',firstChar:''}
        method.success(function (response) {
            //response={success:false,message:"保存失败"}
            if(response.success){
                //保存成功,重新加载分页列表数据
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        })
    }

    //基于id查询品牌数据  异步
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        })
    }

    //批量删除
    $scope.dele=function () {
        if(confirm("您确定要删除吗？")){
            brandService.dele($scope.selectIds).success(function (response) {
                //response={success:false,message:"保存失败"}
                if(response.success){
                    //保存成功,重新加载分页列表数据
                    $scope.reloadList();
                }else {
                    alert(response.message);
                }
            })
        }
    }

});