package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import groupEntity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
/*购物车实现思路：
		根据商品id获取该商品对应商家id，判断该商家对应的购物车是否存在于购物车列表中。
			如果不存在
				创建购物车对象，添加到购物车列表中
			如果存在
				再判断该商品是否存在于购物车明细列表中
					如果不存在该商品
						创建商品明细对象，将其添加到购物车明细列表
					如果存在
						修改该商品明细对象的数量和小计金额。*/
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("该商品不存在");
        }
        String sellerId = item.getSellerId();
        Cart cart = searchCartBySellerId(cartList,sellerId);
        if(cart==null){
            //该商家对应的购物车不存在,创建购物车对象，添加到购物车列表中
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //构建购物车列表对象
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //创建购物车明细对象
            TbOrderItem orderItem = createOrderItem(item,num);
            //将创建的购物车明细对象添加到购物车列表中
            orderItemList.add(orderItem);
            //将购物车列表存放到购物车中
            cart.setOrderItemList(orderItemList);
            //将创建的购物车对象添加到购物车列表中
            cartList.add(cart);
        }else{
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = searchOrderItemByItemId(orderItemList,itemId);
            if(orderItem==null){
                //创建购物车明细对象
                orderItem = createOrderItem(item,num);
                orderItemList.add(orderItem);
            }else {
                //如果存在，修改该商品明细对象的数量和小计金额。
                orderItem.setNum(orderItem.getNum()+num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
                //购物车商品数量减少，减至0以下时
                if(orderItem.getNum()<=0){
                    orderItemList.remove(orderItem);
                }
                //购物车中没有任何该商家的商品了
                if(orderItemList.size()<=0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }



    //根据商品id从购物车列表明细中获取购物车明细对象
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    //创建购物车明细对象
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if(num<1){
            throw new RuntimeException("添加商品数量不能小于1");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(item.getPrice());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }

    /**
     * 根据商家id从购物车列表中获取购物车数据
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(sellerId.equals(cart.getSellerId())){
                return cart;
            }
        }
        return null;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> selectCartListFromRedis(String sessionId) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps(sessionId).get();
        if(cartList==null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String sessionId, List<Cart> cartList) {
        redisTemplate.boundValueOps(sessionId).set(cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList_sessionId, List<Cart> cartList_username) {
        for (Cart cart : cartList_sessionId) {
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            for (TbOrderItem orderItem : orderItemList) {
                cartList_username = addItemToCartList(cartList_username,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList_username;
    }

    @Override
    public void deleteCartList(String sessionId) {
        redisTemplate.delete(sessionId);
    }
}
