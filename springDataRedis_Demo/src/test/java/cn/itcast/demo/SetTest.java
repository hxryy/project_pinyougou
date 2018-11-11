package cn.itcast.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class SetTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void setValue(){
        redisTemplate.boundSetOps("a").add("AA");
        redisTemplate.boundSetOps("a").add("BB");
        redisTemplate.boundSetOps("a").add("CC");
    }
    @Test
    public void getValue(){
        Set set = redisTemplate.boundSetOps("a").members();
        System.out.println(set);
    }
    @Test
    public void deleteValue(){
        redisTemplate.boundSetOps("a").remove("AA");
    }
    @Test
    public void deleteValues(){
        redisTemplate.delete("a");
    }
}
