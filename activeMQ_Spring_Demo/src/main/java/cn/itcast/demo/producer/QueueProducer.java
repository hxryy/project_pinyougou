package cn.itcast.demo.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;


@Component
public class QueueProducer {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination queueTextDestination;
    public void sendMsg(){
        jmsTemplate.send(queueTextDestination, new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("这是与spring整合的第一条消息");
            }
        });
    }
}
