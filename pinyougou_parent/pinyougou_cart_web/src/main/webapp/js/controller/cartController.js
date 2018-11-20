//控制层
app.controller('cartController' ,function($scope,$controller   ,cartService){

    $controller('baseController',{$scope:$scope});//继承

    //查询购物车列表数据
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                sum();
            }
        );
    }

    //添加商品到购物车
    $scope.addItemToCartList=function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
            if(response.success){
                //重新加载购物车列表
                $scope.findCartList();
            }else {
                alert(response.message);
            }

        })
    }

    //统计购物车商品总数量和总金额
    sum=function () {
        $scope.totalNum=0;
        $scope.totalMoney=0.00;
        for(var i=0;i<$scope.cartList.length;i++){
            var cart = $scope.cartList[i];
            var orderItemList = cart.orderItemList;
            for(var j=0;j<orderItemList.length;j++){
                $scope.totalNum+=orderItemList[j].num;
                $scope.totalMoney+=orderItemList[j].totalFee;
            }
        }


    }

});
