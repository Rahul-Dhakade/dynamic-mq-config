package com.rd.kafka.demo.producer;

import com.rd.kafka.demo.config.MsgListenerQueue;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import java.text.MessageFormat;

@Component
public class MyProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyProducer.class);

    @Value("${myvar}")
    private String myVar;

    @Autowired
    private ApplicationContext applicationContext;



    @PostConstruct
    public void init() throws Exception{

        LOGGER.info("################### Postcunstruct "+ myVar);

        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        SingletonBeanRegistry beanRegistry = configurableApplicationContext.getBeanFactory();
        beanRegistry.registerSingleton("connectionFactory", connectionFactory());
        beanRegistry.registerSingleton("adapterQueue", adapterQueue(applicationContext.getBean(MsgListenerQueue.class)));
        beanRegistry.registerSingleton("jmsQueue", getQueue((MessageListenerAdapter) applicationContext.getBean("adapterQueue")));
        beanRegistry.registerSingleton("jmsTemplate1", jmsTemplate1());
    }

    private String broker = "tcp://localhost:61616";
    private String queueName = "demo-one";

//    @Bean
    public ConnectionFactory connectionFactory(){
        LOGGER.debug("<<<<<< Loading connectionFactory");
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(broker);
        LOGGER.debug(MessageFormat.format("{0} loaded sucesfully >>>>>>>", broker));
        return connectionFactory;
    }
    /**
     * Queue listener container.
     * This method configure a listener for a queue
     * @param adapterQueue -  MessageListenerAdapter
     * @see MessageListenerAdapter
     * @see SimpleMessageListenerContainer
     **/
//    @Bean(name = "jmsQueue")
    public SimpleMessageListenerContainer getQueue(MessageListenerAdapter adapterQueue) throws Exception{
        LOGGER.debug("<<<<<< Loading Listener Queue");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        // settings for listener: connectonFactory,Topic name,MessageListener and PubSubDomain (false if is a queue)
        container.setConnectionFactory((ConnectionFactory) applicationContext.getBean("connectionFactory"));
        container.setDestinationName(queueName);
        container.setMessageListener(adapterQueue);
        container.setPubSubDomain(false);
        LOGGER.debug("Listener Queue loaded >>>>>>>");

        return container;
    }

    /**
     * Message listener adapter configuration for queue reception.
     * MsgListenerQueue class implements in method onMessage
     * @param queue - MsgListenerQueue
     * @see MsgListenerQueue
     * @return MessageListenerAdapter
     * @see MessageListenerAdapter
     **/
//    @Bean(name = "adapterQueue")
    public MessageListenerAdapter adapterQueue(MsgListenerQueue queue)
    {
        MessageListenerAdapter listener =  new MessageListenerAdapter(queue);
        listener.setDefaultListenerMethod("onMessage");
        listener.setMessageConverter(new SimpleMessageConverter());
        return listener;

    }

    /**
     * Sender configuration for topic
     * @return JmsTemplate
     * @see JmsTemplate
     */
//    @Bean(name = "jmsTemplate1")
    public JmsTemplate jmsTemplate1() throws Exception {
        return new JmsTemplate((ConnectionFactory) applicationContext.getBean("connectionFactory"));
    }
}
