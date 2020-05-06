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
        LOGGER.info("publish message on demo-one queue");
        jmsTemplate1.convertAndSend("demo-one",message);
        return new ResponseEntity<>("Message sent to demo-one", HttpStatus.OK);
    }

    @GetMapping("/demo/msg2/{message}")
    public ResponseEntity<?> publishMsg2(@PathVariable String message){
        LOGGER.info("publish message on demo-two queue");
        jmsTemplate1.convertAndSend("demo-two",message);
        return new ResponseEntity<>("Message sent to demo-two", HttpStatus.OK);
    }
}
