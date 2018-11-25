 //控制层 
app.controller('seckillGoodsController' ,function($scope,$controller ,$location ,$interval,seckillGoodsService,seckillOrderService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		seckillGoodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		seckillGoodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		seckillGoodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=seckillGoodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=seckillGoodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		seckillGoodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		seckillGoodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//查询秒杀商品列表（从redis中）
	$scope.findSeckillGoodsList=function () {
		seckillGoodsService.findSeckillGoodsList().success(function (response) {
            $scope.list=response;
        })
    }
    //查询商品详情信息
    $scope.findSeckillGoods=function () {
		//从秒杀商品列表页传递的秒杀商品id
		$scope.seckillGoodsId = $location.search()["seckillGoodsId"];
        seckillGoodsService.findSeckillGoods($scope.seckillGoodsId).success(function (response) {
            $scope.seckillGoods=response;

           /* //距离秒杀结束时间，倒计时效果实现
            $interval(function () {
                //获取当前时间和秒杀结束时间差（秒）
				var endTime= new Date($scope.seckillGoods.endTime).getTime();
				var nowTime = new Date().getTime();
				var seconds = Math.floor((endTime-nowTime)/1000);

				var day=Math.floor(seconds/3600/24);
				var hours= Math.floor((seconds-day*24*3600)/3600);
				var minutes=Math.floor((seconds-day*24*3600-hours*3600)/60);
				var sec=seconds-day*24*3600-hours*3600-minutes*60;

            }, 1000);*/

            //计算出剩余时间
            var endTime = new Date($scope.seckillGoods.endTime).getTime();
            var nowTime = new Date().getTime();

            //剩余时间
            $scope.secondes =Math.floor( (endTime-nowTime)/1000 );

            var time =$interval(function () {
                if($scope.secondes>0){
                    //时间递减
                    $scope.secondes--;
                    //时间格式化
                    $scope.timeString=$scope.convertTimeString($scope.secondes);
                }else{
                    //结束时间递减
                    $interval.cancel(time);
                }
            },1000);
        })
    }

    $scope.convertTimeString=function (allseconds) {
        //计算天数
        var days = Math.floor(allseconds/(60*60*24));

        //小时
        var hours =Math.floor( (allseconds-(days*60*60*24))/(60*60) );

        //分钟
        var minutes = Math.floor( (allseconds-(days*60*60*24)-(hours*60*60))/60 );

        //秒
        var seconds = allseconds-(days*60*60*24)-(hours*60*60)-(minutes*60);

        //拼接时间
        var timString="";
        if(days>0){
            timString=days+"天:";
        }

        if(hours<10){
            hours="0"+hours;
        }
        if(minutes<10){
            minutes="0"+minutes;
        }
        if(seconds<10){
            seconds="0"+seconds;
        }
        return timString+=hours+":"+minutes+":"+seconds;
    }

    // $interval(执行的函数,间隔的毫秒数,运行次数); 第三个参数往往都不设置
/*	$scope.time=10;
    $interval(function () {
        $scope.time--;
    }, 1000 ,10);*/

	//秒杀下单
	$scope.saveSeckillOrder=function () {
		seckillOrderService.saveSeckillOrder($scope.seckillGoodsId).success(function (rsponse) {
			alert(rsponse.message);

			/*if(rsponse.success){
				location.href="pay.html";
			}else {
                alert(rsponse.message);
			}*/

        })
    }
});	
