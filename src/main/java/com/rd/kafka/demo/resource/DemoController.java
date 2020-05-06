package com.rd.kafka.demo.resource;

import com.rd.kafka.demo.producer.MyProducer;
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

    @GetMapping("/demo/msg/{message}")
    public ResponseEntity<?> publishMsg(@PathVariable String message){
        LOGGER.info("publish message on queue");
        jmsTemplate1.convertAndSend("demo-one",message);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
