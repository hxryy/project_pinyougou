app.service("uploadService",function ($http) {

    //文件上传的方法
    this.uploadFile=function () {
        //angularjs结合html5完成文件上传
        //html5中FormData将上传文件作为提交参数，提交给后端
        var formData = new FormData();
        //获取页面选择的文件对象，并追加到FormData对象中  参数一：提交值，与后端接收文件对象参数名称相同
        //参数二：file.file[0]   file与<input type="file" id="file1" />中的id值对应起来。
        formData.append("file",file.files[0]);

        //发起请求，完成文件上传
        return $http({
            method:"post",
            url:"../upload/uploadFile.do",
            data: formData,
            headers: {'Content-Type':undefined},// 相当于enctype="multipart/form-data"
            transformRequest: angular.identity
        });
    }
});