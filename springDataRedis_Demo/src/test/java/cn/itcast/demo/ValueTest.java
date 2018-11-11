package cn.itcast.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class ValueTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 存值
     */
    @Test
    public void setValue(){
        redisTemplate.boundValueOps("valueTest").set("二师兄",30L, TimeUnit.SECONDS);
    }

    /**
     * 取值
     */
    @Test
    public void getValue(){
        System.out.println(redisTemplate.boundValueOps("valueTest").get());
    }

    /**
     * 删除
     */
    @Test
    public void delete(){
        redisTemplate.delete("valueTest");
    }

}
