package com.pinyougou.seckill.service.impl;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import util.IdWorker;

import java.util.Date;
import java.util.Map;

@Component
public class CreateOrder implements Runnable{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Override
    public void run() {
        //基于多线程，从redis缓存中获取秒杀下单的任务
        Map<String,Object> params = (Map<String, Object>) redisTemplate.boundListOps("seckill_order_queue").rightPop();

        String userId = (String) params.get("userId");
        Long seckillGoodsId = (Long) params.get("seckillGoodsId");

        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckill_goods").get(seckillGoodsId);
        //保存秒杀订单
		/*tb_seckill_order
		  `id` bigint(20) NOT NULL COMMENT '主键',
		  `seckill_id` bigint(20) DEFAULT NULL COMMENT '秒杀商品ID',
		  `money` decimal(10,2) DEFAULT NULL COMMENT '支付金额',
		  `user_id` varchar(50) DEFAULT NULL COMMENT '用户',
		  `seller_id` varchar(50) DEFAULT NULL COMMENT '商家',
		  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
		  `status` varchar(1) DEFAULT NULL COMMENT '状态',*/
        TbSeckillOrder seckillOrder = new TbSeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(seckillGoodsId);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(userId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("1");//1 未支付状态

        seckillOrderMapper.insert(seckillOrder);


        //在缓存中记录，该用户购买了那个商品
        redisTemplate.boundSetOps("seckill_user_goods"+seckillGoodsId).add(userId);


        //秒杀下单完成后，扣减库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        redisTemplate.boundHashOps("seckill_goods").put(seckillGoodsId,seckillGoods);

        //秒杀下单成功后，排队人数减一
        redisTemplate.boundValueOps("seckill_user_queue"+seckillGoodsId).increment(-1);

        if(seckillGoods.getStockCount()<=0){
            //同步库存数据到数据库中
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);

            //清除缓存中该商品对应的数据
            redisTemplate.boundHashOps("seckill_goods").delete(seckillGoodsId);
        }

    }
}
