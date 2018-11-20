package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.cart.service.CartService;
import entity.Result;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpSession session;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 设置sessionID基于cookie保存一周
     */
    private String getSessionId(){
        //尝试基于cookie名称取sessionID
        String sessionId =CookieUtil.getCookieValue(request,"cartCookie","utf-8");
        if(sessionId==null){
            //如果根据cookie名称取不到sessionID
            sessionId=session.getId();
            //基于cookie保存sessionId一周
            CookieUtil.setCookie(request,response,"cartCookie",sessionId,3600*24*7,"utf-8");
        }
        return sessionId;
    }


    /**
     * 查询购物车列表数据，用于页面展示购物车列表
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(username);
        String sessionId = getSessionId();
        List<Cart> cartList_sessionId = cartService.selectCartListFromRedis(sessionId);
        if(username.equals("anonymousUser")){//未登录，基于sessionID取购物车列表数据
            return cartList_sessionId;
        }else {//登录，基于username取购物车列表数据
            List<Cart> cartList_username= cartService.selectCartListFromRedis(username);
            //用户登录前，如果已经添加商品到购物车列表中
            if(cartList_sessionId!=null && cartList_sessionId.size()>0){
                //登录后，需要将登录前的购物车列表数据合并到登录后的购物车列表中
                cartList_username= cartService.mergeCartList(cartList_sessionId,cartList_username);
                //清除登录前的购物车列表数据
                cartService.deleteCartList(sessionId);
                //将合并后的购物车列表，重新放入redis缓存，下次查询时，查询的是合并后的购物车列表数据
                cartService.saveCartListToRedis(username,cartList_username);
            }

            return cartList_username;
        }

    }



    /**
     * 添加商品到购物车列表
     */
    @RequestMapping("/addItemToCartList")
    @CrossOrigin(origins="http://item.pinyougou.com",allowCredentials = "true")
    public Result addItemToCartList(Long itemId,Integer num){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            String sessionId = getSessionId();
            //查询购物车列表
            List<Cart> cartList = findCartList();
            //添加商品到购物车列表
            cartList = cartService.addItemToCartList(cartList, itemId, num);

            if(username.equals("anonymousUser")){//未登录
                System.out.println("save cartList to redis by sessionId ...");

                //将添加商品后的购物车列表再存入redis中
                cartService.saveCartListToRedis(sessionId,cartList);
            }else {
                System.out.println("save cartList to redis by username ...");
                //用户登录，基于用户名保存购物车列表数据到redis
                cartService.saveCartListToRedis(username,cartList);
            }




            return new Result(true,"添加购物车成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败");
        }

    }
}
