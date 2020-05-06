package com.rd.kafka.demo;

import com.rd.kafka.demo.producer.MyProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	@Autowired
	private MyProducer myProducer;
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
