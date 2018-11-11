package cn.itcast.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class HashTest {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 存值
     */
    @Test
    public void setValue(){
        redisTemplate.boundHashOps("a").put("a","AA");
        redisTemplate.boundHashOps("a").put("b","BB");
        redisTemplate.boundHashOps("a").put("c","CC");
    }

    /**
     * 取值
     */
    @Test
    public void getValue(){
        Object o = redisTemplate.boundHashOps("a").get("a");
        System.out.println(o);
    }
    @Test
    public void getValues(){
        List list = redisTemplate.boundHashOps("a").values();
        System.out.println(list);
    }
    @Test
    public void getKeys(){
        System.out.println(redisTemplate.boundHashOps("a").keys());

    }

    /**
     * 删除
     */
    @Test
    public void delete(){
        redisTemplate.delete("a");
    }

}
