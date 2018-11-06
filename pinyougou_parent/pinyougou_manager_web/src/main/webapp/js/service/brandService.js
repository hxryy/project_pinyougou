//定义服务层  与后端进行请求交互。
app.service("brandService",function ($http) {
    //查询所有
    this.findAll=function () {
        return $http.get("../brand/findAll.do");
    }

    //分页查询
    this.findPage=function (pageNum,pageSize,searchEntity) {
        return $http.post("../brand/search.do?pageNum="+pageNum+"&pageSize="+ pageSize,searchEntity);
    }

    //新增
    this.add=function (entity) {
        return $http.post("../brand/add.do",entity);
    }

    //修改
    this.update=function (entity) {
        return $http.post("../brand/update.do",entity);
    }
    //根据id查询
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    }

    //批量删除
    this.dele=function (ids) {
        return  $http.get("../brand/delete.do?ids="+ids);
    }

    this.selectBrandList=function () {
        return $http.get("../brand/selectBrandList.do");
    }
});