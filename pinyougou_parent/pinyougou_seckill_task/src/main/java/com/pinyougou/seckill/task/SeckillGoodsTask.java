package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 完成秒杀商品同步的定时任务类
 */
@Component
public class SeckillGoodsTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/10 * * * * ?")  //每隔10秒同步一次
    public void synchronizeSeckillGoodsToRedis(){
        /**
         * 满足条件是商品同步redis
         * 审核通过
         有库存
         当前时间大于开始时间,并小于秒杀结束时间
         */

        TbSeckillGoodsExample exmaple = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = exmaple.createCriteria();
        criteria.andStatusEqualTo("1").andStockCountGreaterThan(0).andStartTimeLessThanOrEqualTo(new Date()).andEndTimeGreaterThanOrEqualTo(new Date());
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(exmaple);
        for (TbSeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckill_goods").put(seckillGoods.getId(),seckillGoods);
            //解决商品超卖问题，需要在同步数据时，基于redis队列，记录该商品还有多少库存。  [1,1,1,1,1]
            for(int i=0;i<seckillGoods.getStockCount();i++){
                redisTemplate.boundListOps("seckill_goods_queue"+seckillGoods.getId()).leftPush(seckillGoods.getId());
            }
        }

        System.out.println("synchronizeSeckillGoodsToRedis finished !!!");

    }

}
