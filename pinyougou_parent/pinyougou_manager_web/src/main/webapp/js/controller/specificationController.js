//定义控制器 参数一：控制器名称  参数二：控制器要做的事情
//$controller实现angularjs继承机制
app.controller("specificationController",function ($scope,$controller,specificationService) {

    //angularjs继承代码实现  参数一：父对象名称  参数二：共享$scope配置
    $controller("baseController",{$scope:$scope});


    //$scope 是angularjs内置对象，$scope可以理解为angularjs的js代码与html代码数据交互的桥梁
    //[]在json格式数据中代表数组，{}在json格式数据中代表对象

    $scope.searchEntity={};
    //条件分页查询
    $scope.search=function (pageNum,pageSize) {
        specificationService.search(pageNum,pageSize,$scope.searchEntity).success(function (response) {
            $scope.paginationConf.totalItems=response.total;//总记录数
            $scope.list=response.rows;//当前页查询结果集
        })
    }

    //保存品牌
    $scope.save=function () {

        var method=null;
        if($scope.entity.specification.id !=null){
            //当id存在时，执行修改
            method=specificationService.update($scope.entity);
        }else {
            //当id不存在时，执行新增
            method=specificationService.add($scope.entity);
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
        specificationService.findOne(id).success(function (response) {
            $scope.entity=response;
        })
    }

    //批量删除
    $scope.dele=function () {
        if(confirm("您确定要删除吗？")){
            specificationService.dele($scope.selectIds).success(function (response) {
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

    //初始化规格和规格选项组合实体对象 {}
    $scope.entity={specification:{},specificationOptions:[]};

    //添加规格选项行
    $scope.addRow=function () {
        $scope.entity.specificationOptions.push({});
    }

    //删除规格选项行
    $scope.deleRow=function (index) {
        $scope.entity.specificationOptions.splice(index,1);
    }

});