package cn.itcast.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-activemq-consumer.xml")
public class QueueConsumerTest {
    @Test
    public void getMsgTest(){
        while (true){}
    }
}
