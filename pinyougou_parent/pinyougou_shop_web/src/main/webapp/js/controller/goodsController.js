 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
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

    //新增
    $scope.add=function(){
		//获取商品介绍编辑器中的html内容，赋予goodsDesc.introduction
		$scope.entity.goodsDesc.introduction=editor.html();

        goodsService.add( $scope.entity).success(
            function(response){
                if(response.success){
                    // 清除录入商品信息时输入的内容
                    $scope.entity={};
                    editor.html("");//清除商品介绍编辑框中的内容
                }else{
                    alert(response.message);
                }
            }
        );
    }
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
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
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	//查询商品一级分类方法
	$scope.selectItemCat1List=function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.itemCat1List=response;
        })
    }

    //基于一级分类查询对应的二级分类
	//参数一：监控的值  参数二：监控内容发生变化后，做的事情
    $scope.$watch("entity.goods.category1Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List=response;
        })
    })

    //基于二级分类查询对应的三级分类
    //参数一：监控的值  参数二：监控内容发生变化后，做的事情
    $scope.$watch("entity.goods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List=response;
        })
    })

    //基于三级分类查询对应的模板
    //参数一：监控的值  参数二：监控内容发生变化后，做的事情
    $scope.$watch("entity.goods.category3Id",function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
           $scope.entity.goods.typeTemplateId = response.typeId;
        })
    })

    //基于模板变化查询对应的品牌和规格、扩展属性等数据
    //参数一：监控的值  参数二：监控内容发生变化后，做的事情
    $scope.$watch("entity.goods.typeTemplateId",function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            //模板对象
			//处理模板关联的品牌数据
           $scope.brandList= JSON.parse(response.brandIds);
           //理模板关联的扩展属性数据"[{"text":"内存大小"},{"text":"颜色"}]"
			//[{"text":"内存大小"},{"text":"颜色"}]
            $scope.entity.goodsDesc.customAttributeItems =JSON.parse(response.customAttributeItems);
        })
    })

    $scope.image_entity={};
	//文件上传
	$scope.uploadFile=function () {
		uploadService.uploadFile().success(function (response) {
			if(response.success){
				//接收图片地址
				$scope.image_entity.url=response.message;
			}else {
				alert(response.message);
			}

        })
    }

    //初始化保存商品的组合实体类对象
	$scope.entity={goods:{},goodsDesc:{itemImages:[]},itemList:[]};

    //图片保存时，将图片对象添加到图片列表中
	$scope.addImageEntity=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //删除图片列表中的对象
    $scope.deleImageEntity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }
});	
