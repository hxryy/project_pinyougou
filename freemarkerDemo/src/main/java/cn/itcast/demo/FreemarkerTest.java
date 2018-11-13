package cn.itcast.demo;


import cn.itcast.domain.User;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class FreemarkerTest {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration(Configuration.getVersion());
        configuration.setDirectoryForTemplateLoading(new File("E:\\workspace\\PinYouGou\\freemarkerDemo\\src\\main\\resources"));
        configuration.setDefaultEncoding("utf-8");
        Template template = configuration.getTemplate("test.ftl");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name","张三");
        map.put("age",16);
        List<User> userList = new ArrayList<User>();
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setId(i);
            user.setUsername(i+"王五");
            userList.add(user);
        }
        map.put("userList",userList);
        map.put("today",new Date());
        map.put("data",123456789);
        map.put("xingming",null);
        Writer out = new FileWriter("d:/test.html");
        template.process(map,out);
        out.close();
    }
}
