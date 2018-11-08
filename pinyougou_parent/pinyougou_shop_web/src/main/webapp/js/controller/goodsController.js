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
            $scope.itemCat3List={};
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
        });
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList=response;
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
	$scope.entity={goods:{isEnableSpec:"1"},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]};

    //图片保存时，将图片对象添加到图片列表中
	$scope.addImageEntity=function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    //删除图片列表中的对象
    $scope.deleImageEntity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    $scope.updateSpecAttribute=function ($event, specName, specOption) {
        var specObject = $scope.getObjectByName($scope.entity.goodsDesc.specificationItems,"attributeName",specName);
        if(specObject!=null){
            if($event.target.checked){
                specObject.attributeValue.push(specOption);
            }else {
                var index = specObject.attributeValue.indexOf(specOption);
                specObject.attributeValue.splice(index,1);
                if(specObject.attributeValue.length==0){
                    var index1 = $scope.entity.goodsDesc.specificationItems.indexOf(specObject);
                    $scope.entity.goodsDesc.specificationItems.splice(index1,1);
                }
            }
        }else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[specOption]});
        }
    }
    //组装sku列表数据
    $scope.createItemList=function () {

        //初始化sku列表对象
        $scope.entity.itemList=[{spec:{},price:0,num:99999,status:"1",isDefault:"0"}];
        //考虑组装spec：{"网络":"联通3G"}
        //组装spec对象的之和勾选的规格结果集有关
        //[{"attributeName":"网络","attributeValue":["移动3G"]}]
        var specList = $scope.entity.goodsDesc.specificationItems;

        //
        if(specList.length==0){
            $scope.entity.itemList=[];
        }

        for(var i=0;i<specList.length;i++){
            //基于深克隆，构建itemList中对象的spec属性
            $scope.entity.itemList = addColumn($scope.entity.itemList,specList[i].attributeName,specList[i].attributeValue);
        }
    }

    //构建sku列表行与列数据
    addColumn=function (list,specName,specOptions) {
        //声明新的sku列表
        var newList=[];

        //list=[{spec:{},price:0,num:99999,status:"1",isDefault:"0"}]
        for(var i=0;i<list.length;i++){
            //{spec:{},price:0,num:99999,status:"1",isDefault:"0"}
            var oldItem = list[i];
            //遍历勾选的规格选项集合
            for(var j=0;j<specOptions.length;j++){
                //基于深克隆创建新的sku对象
                var newItem=JSON.parse(JSON.stringify(oldItem));
                //spec：{"网络":"联通3G"}
                newItem.spec[specName]=specOptions[j];
                newList.push(newItem);
            }
        }

        return newList;
    }
    $scope.status=['未审核','已审核','审核未通过','关闭'];
	$scope.updateIsMarketable=function (isMarketable) {
        goodsService.updateIsMarketable($scope.selectIds,isMarketable).success(function (response) {
            if(response.success()){
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        })
    }
    $scope.isMarketable=['下架','上架'];

    $scope.itemCatList=[];
	$scope.findAllItemCatList=function () {
        itemCatService.findAll().success(function (response) {
            for(var i=0;i<response.length;i++){
                $scope.itemCatList[response[i].id]=response[i].name;
            }
        })
    }
});	
