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
	$scope.getObjectByName=function (list,key,value) {
		for(var i= 0;i<list.length;i++){
			if(list[i][key]==value){
				return list[i];
				break;
			}
		}
		return null;
    }
    
});	