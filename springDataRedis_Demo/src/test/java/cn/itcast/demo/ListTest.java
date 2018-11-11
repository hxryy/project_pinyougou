package cn.itcast.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class ListTest {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void setLeftValue(){
        redisTemplate.boundListOps("a").leftPush("aa");
        redisTemplate.boundListOps("a").leftPush("aaa");
        redisTemplate.boundListOps("a").leftPush("aaaa");
    }
    @Test
    public void setRightValue(){
        redisTemplate.boundListOps("a").rightPush("aa");
        redisTemplate.boundListOps("a").rightPush("aaa");
        redisTemplate.boundListOps("a").rightPush("aaaa");
    }
    @Test
    public void getLeftValue(){
        List list = redisTemplate.boundListOps("a").range(0, 10);
        System.out.println(list);
    }
    @Test
    public void deleteValue(){
        //参数一：删除几个指定元素
        Long remove = redisTemplate.boundListOps("a").remove(2, "aa");
    }
    @Test
    public void delete(){
        redisTemplate.delete("a");
    }
}
