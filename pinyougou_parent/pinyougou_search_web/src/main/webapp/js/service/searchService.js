//服务层
app.service('searchService',function($http){
	    	
	//搜索
	this.search=function(searchMap){
		return $http.post('search/searchItem.do',searchMap);
	}
});
