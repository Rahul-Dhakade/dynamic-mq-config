package com.rd.kafka.demo.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    /**
     * Sender class for topic
     */
    @Autowired
    @Qualifier("jmsTemplate1")
    private JmsTemplate jmsTemplate1;

    @GetMapping("/demo/msg1/{message}")
    public ResponseEntity<?> publishMsg1(@PathVariable String message){
        LOGGER.info("publish message on emx.queue1 queue");
        jmsTemplate1.convertAndSend("emx.queue1",message);
        LOGGER.info("publish message on emx.queue2 queue");
        jmsTemplate1.convertAndSend("emx.queue2",message);
        return new ResponseEntity<>("Message sent to queues emx.queue1 and emx.queue2", HttpStatus.OK);
    }

    @GetMapping("/demo/msg2/{message}")
    public ResponseEntity<?> publishMsg2(@PathVariable String message){
        LOGGER.info("publish message on amh.queue1 queue");
        jmsTemplate1.convertAndSend("amh.queue1",message);
        LOGGER.info("publish message on amh.queue2 queue");
        jmsTemplate1.convertAndSend("amh.queue2",message);
        LOGGER.info("publish message on amh.queue3 queue");
        jmsTemplate1.convertAndSend("amh.queue3",message);
        return new ResponseEntity<>("Message sent to queues amh.queue1,amh.queue2 and amh.queue3", HttpStatus.OK);
    }
}
