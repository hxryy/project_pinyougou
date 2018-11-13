<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>freemarker模板</title>
</head>
<body>
<#include "head.ftl">
您好,${name},欢迎来到神奇的freemarker世界<br>
我是${name},我今年${age}了<br>

<#assign username="李四">
${username}<br>

<#if (age>18)>
    恭喜,你已成年了
    <#else>
    恭喜,你还是少年
</#if><br>

<#list userList as user>
    ${user.id}=====${user.username}<br>
</#list>

共${userList?size}个用户<br>

<#assign user='{"id":3,"name":"赵六","age":19}'?eval/>
用户id:${user.id}<br>
用户name:${user.name}<br>
用户age:${user.age}<br>

当前日期:${today?date}<br>
当前时间:${today?time}<br>
当前日期时间:${today?datetime}<br>
当前日期时间:${today?string("yyyy年MM月dd日 hh时mm分ss秒")}<br>
<#if xingming??>
    xingming不为空<br>
    <#else>
    xingming为空,${xingming!"田七"}<br>
</#if>
${xingming!""}
${data?c}<br>
</body>
</html>