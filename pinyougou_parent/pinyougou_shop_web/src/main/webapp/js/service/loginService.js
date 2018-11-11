app.service("loginService",function ($http) {
    //获取登录人用户名信息
    this.getName=function () {
        return $http.get("../login/getName.do");
    }
})