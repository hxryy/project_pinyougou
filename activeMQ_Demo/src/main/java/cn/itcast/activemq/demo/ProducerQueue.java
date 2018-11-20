package cn.itcast.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ProducerQueue {
    public static void main(String[] args) throws JMSException {
        //1.创建连接工厂  注意：activeMQ控制台端口号8161  基于Java操作activeMQ 端口号61616
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.149:61616");
        //2.获取连接
        Connection connection = connectionFactory.createConnection();
        //3.启动连接
        connection.start();
        //4.获取session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.创建队列对象
        Queue queue = session.createQueue("queue_test");
        //6.创建消息生产者对象或消费者
        MessageProducer producer = session.createProducer(queue);
        //7.创建消息
        TextMessage textMessage = session.createTextMessage("这是第一条queue模式的消息");
        //8.使用生成者发送消息
        producer.send(queue,textMessage);
        //9.关闭资源
        producer.close();
        session.close();
        connection.close();
    }
}
