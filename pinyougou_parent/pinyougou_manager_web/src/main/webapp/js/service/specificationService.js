//定义服务层  与后端进行请求交互。
app.service("specificationService",function ($http) {

    //新增
    this.add=function (entity) {
        return $http.post("../specification/add.do",entity);
    }

    //修改
    this.update=function (entity) {
        return $http.post("../specification/update.do",entity);
    }
    //根据id查询
    this.findOne=function (id) {
        return $http.get("../specification/findOne.do?id="+id);
    }

    //批量删除
    this.dele=function (ids) {
        return  $http.get("../specification/delete.do?ids="+ids);
    }

    //条件分页查询
    this.search=function (pageNum,pageSize,searchEntity) {
        return $http.post("../specification/search.do?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);
    }

    //查询模板关联的规格列表数据
    this.selectSpecList=function () {
        return  $http.get("../specification/selectSpecList.do");
    }
});