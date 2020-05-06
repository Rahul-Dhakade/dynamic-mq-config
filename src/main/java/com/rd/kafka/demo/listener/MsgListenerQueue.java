package com.rd.kafka.demo.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MsgListenerQueue {
	

	
	private static final Logger LOGGER = LoggerFactory.getLogger(MsgListenerQueue.class);
	
	/**
	 * Method that read the Queue when exists messages.
	 * This method is a listener
	 * @param msg - String message
	 */
	public void onMessage(String msg) 
	{	
		LOGGER.debug(msg);
		LOGGER.info("Received message on MsgListener:::..............:::: "+msg);
	}

}