 //品牌控制层 
app.controller('baseController' ,function($scope){	
	
    //重新加载列表 数据
    $scope.reloadList=function(){
    	//切换页码  
    	$scope.search( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);	   	
    }
    
	//分页控件配置 
	$scope.paginationConf = {
         currentPage: 1,
         totalItems: 10,
         itemsPerPage: 10,
         perPageOptions: [10, 20, 30, 40, 50],
         onChange: function(){
        	 $scope.reloadList();//重新加载
     	 }
	}; 
	
	$scope.selectIds=[];//选中的ID集合 

	//更新复选
	$scope.updateSelection = function($event, id) {		
		if($event.target.checked){//如果是被选中,则增加到数组
			$scope.selectIds.push( id);			
		}else{
			var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx, 1);//删除 
		}
	}
	
	//去数组中对象的属性值，拼接为字符串
	$scope.selectValueByKey=function (jsonString,key) {
		//alert(jsonString);
		//[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		var value="";
		var objList =JSON.parse(jsonString);
		for(var i=0;i<objList.length;i++){
			//从json对象中，根据属性名取属性值的方式，有两种方法
			//1、如果属性名是确定值，对象.属性名
			//2、如果属性名是变量，取值方式为 对象[属性名]

			if(i==0){
                value+= objList[i][key];
			}else {
                value+=","+ objList[i][key];
			}

		}
		return value;
    }

    //根据对象的属性名判断该对象是否存在于json格式的数组对象中例如：
       /* [{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},
        {"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]*/
       //参数一：json格式的数组对象   参数二：数组中对象的属性名  参数二：数组中对象的属性值  []
   $scope.getObjectByName=function (list,key,value) {
   	  // console.log(list);
	   for(var i=0;i<list.length;i++){
	   		//基于对象属性值，判断该对象是否存在
	   		if(list[i][key]==value){
				return list[i];
				break;
			}
	   }
	   return null;
   }    
       
       
       
       
       
       
       
       
       
	
});	