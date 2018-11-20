package cn.itcast.demo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @RequestMapping("/getLoginName")
    public void getLoginName(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
    }
}
