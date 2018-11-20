//控制层
app.controller('orderController' ,function($scope,$controller   ,addressService,cartService,orderService){

    $controller('baseController',{$scope:$scope});//继承
    //展示收件人地址列表
    $scope.findAddressByUserId=function () {
        addressService.findAddressByUserId().success(function (response) {
            $scope.addressList=response;
            //设置默认选中的地址对象
            for(var i=0;i<$scope.addressList.length;i++){
                if($scope.addressList[i].isDefault=='1'){
                    //默认选中的地址对象
                    $scope.address=$scope.addressList[i];
                    break;
                }
            }
            //如果没有默认地址
            if($scope.address==null){
                $scope.address=$scope.addressList[0];
            }
        })
    }

    //定义寄送的收件人地址对象
    $scope.address=null;
    //默认收件人地址勾选效果实现
    $scope.isSelected=function (addr) {
        if($scope.address==addr){
            return true;
        }else {
            return false;
        }
    }
    //切换收件人地址列表
    $scope.updateAddress=function (addr) {
        $scope.address=addr;
    }

    //定义订单对象 默认使用微信支付
    $scope.entity={paymentType:'1'};
    //页面支付方式选择的方法
    $scope.updatePaymentType=function (type) {
        $scope.entity.paymentType=type;
    }

    //查询购物车列表数据
    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                sum();
            }
        );
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

    //保存订单的方法
    $scope.save=function () {
        //绑定收件人相关信息
        $scope.entity.receiver=$scope.address.contact;
        $scope.entity.receiverMobile=$scope.address.mobile;
        $scope.entity.receiverAreaName=$scope.address.address;
        orderService.add($scope.entity).success(function (response) {
            if(response.success){
                location.href="pay.html";
            }else{
                alert(response.message);
            }
        })
    }

});
