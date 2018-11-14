//控制层
app.controller('searchController' ,function($scope,$controller,$location,searchService){

    $controller('baseController',{$scope:$scope});//继承

    //定义封装搜索条件的对象
    $scope.searchMap={
        keywords:"",//关键字
        category:"",//分类
        brand:"",
        spec:{},//规格对象 规格名称:规格选项值
        price:"",
        sortField:"", //排序字段
        sort:"ASC", //排序方式  ASC升序  DESC降序
        pageNo:1,
        pageSize:60
    };

    //获取门户网站传过来的关键字
    var keywords = $location.search()["keywords"];

    if(keywords!="undefined"){
        //门户网站输入了关键字
        $scope.searchMap.keywords=keywords;
    }else{
        $scope.searchMap.keywords="手机";
    }
    /**
     * 商品搜索功能
     */
    $scope.search=function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
            buildPageLabel();//每次执行查询后，都需要重新构建分页工具条
        })
    }

    //封装条件查询
    $scope.addFilterCondition=function (key,value) {
        if(key=="category" || key=="brand" || key=="price"){
            $scope.searchMap[key]=value;
        }else {
            $scope.searchMap.spec[key]=value;
        }

        //调用search方法，将组装好的searchMap数据，提交给后端
        $scope.search();

    }

    //移除搜索条件方法
    $scope.removeSearchItem=function (key) {
        if(key=="category" || key=="brand" || key=="price"){
            $scope.searchMap[key]="";
        }else {
            delete $scope.searchMap.spec[key];
        }

        //调用search方法，将组装好的searchMap数据，提交给后端
        $scope.search();
    }

    //排序条件过滤查询
    $scope.sortSearch=function (sortField,sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;

        //调用search方法，将组装好的searchMap数据，提交给后端
        $scope.search();
    }

    //构建分页工具条代码
    buildPageLabel=function(){
        $scope.pageLabel = [];// 新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;// 得到最后页码

        // 定义属性,显示省略号
        $scope.firstDot = true;
        $scope.lastDot = true;

        var firstPage = 1;// 开始页码
        var lastPage = maxPageNo;// 截止页码

        if ($scope.resultMap.totalPages > 5) { // 如果总页数大于5页,显示部分页码
            if ($scope.resultMap.pageNo <= 3) {// 如果当前页小于等于3
                lastPage = 5; // 前5页
                // 前面没有省略号
                $scope.firstDot = false;

            } else if ($scope.searchMap.pageNo >= lastPage - 2) {// 如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4; // 后5页
                // 后面没有省略号
                $scope.lastDot = false;
            } else {// 显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            // 页码数小于5页  前后都没有省略号
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        // 循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }


    //分页查询
    $scope.queryForPage=function(pageNo){
        $scope.searchMap.pageNo=pageNo;

        //执行查询操作
        $scope.search();

    }

    //分页页码显示逻辑分析：
    // 1,如果页面数不足5页,展示所有页号
    // 2,如果页码数大于5页
    // 1) 如果展示最前面的5页,后面必须有省略号.....
    // 2) 如果展示是后5页,前面必须有省略号
    // 3) 如果展示是中间5页,前后都有省略号

    // 定义函数,判断是否是第一页
    $scope.isTopPage = function() {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }
    // 定义函数,判断是否最后一页
    $scope.isLastPage = function() {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }
});	
