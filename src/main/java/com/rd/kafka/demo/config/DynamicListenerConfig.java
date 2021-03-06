package com.rd.kafka.demo.config;

import com.rd.kafka.demo.listener.MsgListenerQueue;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.slf4j.Logger;
import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DynamicListenerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicListenerConfig.class);

    @Value("${mqSelector}")
    private String mqSelector;

    @Autowired
    private Environment environment;

    @Autowired
    private ApplicationContext applicationContext;


    private String broker;
    private String queueName;

    @PostConstruct
    public void init() throws Exception{

        LOGGER.info("################### MQ-Selector::: "+ mqSelector);

        if("emx".equals(mqSelector)){
            broker = environment.getProperty("emx.mq.url");
            queueName = environment.getProperty("emx.mq.queue");
        }else if("amh".equals(mqSelector)){
            broker = environment.getProperty("amh.mq.url");
            queueName = environment.getProperty("amh.mq.queue");
        }else {
            LOGGER.info("Default configuration!!!");
            broker = "tcp://localhost:61616";
            queueName = "emx.queue1";
        }

        LOGGER.info("MQ Broker URL ############ ::: "+ broker);
        LOGGER.info("MQ Queue Name ############ ::: "+ queueName);

        /**
         * If we want listen on multiple queues of same source
         */
        List<String> queues = new ArrayList<>();
        if(queueName.contains(":")){
            queues = Arrays.asList(queueName.split(":"));
        }else {
            queues.add(queueName);
        }

        LOGGER.info("Configuring JMSListener!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
        SingletonBeanRegistry beanRegistry = configurableApplicationContext.getBeanFactory();
        beanRegistry.registerSingleton("connectionFactory", connectionFactory());
        beanRegistry.registerSingleton("adapterQueue", adapterQueue(applicationContext.getBean(MsgListenerQueue.class)));
        beanRegistry.registerSingleton("jmsTemplate1", jmsTemplate1());

        for(String queue: queues){
            beanRegistry.registerSingleton("jmsQueue"+queue.trim(), getQueue((MessageListenerAdapter) applicationContext.getBean("adapterQueue"), queue.trim()));
        }

        LOGGER.info("JMSListener Configuration DONE:-) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    }

    public ConnectionFactory connectionFactory(){
        LOGGER.info("<<<<<< Loading connectionFactory");
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
    public SimpleMessageListenerContainer getQueue(MessageListenerAdapter adapterQueue, String queue) throws Exception{
        LOGGER.info("<<<<<< Loading Listener Queue");
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory((ConnectionFactory) applicationContext.getBean("connectionFactory"));
        container.setDestinationName(queue);
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

    public MessageListenerAdapter adapterQueue(MsgListenerQueue queue) {
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
    public JmsTemplate jmsTemplate1() throws Exception {
        return new JmsTemplate((ConnectionFactory) applicationContext.getBean("connectionFactory"));
    }
}
