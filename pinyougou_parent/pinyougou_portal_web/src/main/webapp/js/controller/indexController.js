 //控制层 
app.controller('indexController' ,function($scope,$controller   ,contentService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //根据广告分类查询广告列表数据
	$scope.findByCategoryId=function (categoryId) {
		contentService.findByCategoryId(categoryId).success(function (response) {
			$scope.contentList=response;
        })
    }
    //关键字搜索
	$scope.search=function () {
		//注意：angularjs路由地址传参时，需要在拼接参数的问号前加上#号
		location.href="http://search.pinyougou.com/search.html#?keywords="+$scope.keywords;
    }
});	
