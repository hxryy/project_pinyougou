package cn.itcast.activemq.demo;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ConsumerQueue {
    public static void main(String[] args) throws JMSException {
        //1.创建连接工厂  注意：activeMQ控制台端口号8161  基于Java操作activeMQ 端口号61616
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.128:61616");
        //2.获取连接
        Connection connection = connectionFactory.createConnection();
        //3.启动连接
        connection.start();
        //4.获取session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.创建队列对象
        Queue queue = session.createQueue("queue_test");
        //6.创建消息生产者对象或消费者
        MessageConsumer consumer = session.createConsumer(queue);
        //7.消费消息
        consumer.setMessageListener(new MessageListener() {
            public void onMessage(Message message) {
                TextMessage textMessage =(TextMessage) message;
                try {
                    System.out.println(textMessage.getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //9.关闭资源
        consumer.close();
        session.close();
        connection.close();
    }
}
