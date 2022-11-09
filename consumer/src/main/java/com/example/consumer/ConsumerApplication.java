package com.example.consumer;

import com.example.consumer.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConsumerApplication{
//	@Autowired
//	private NettyClient nettyClient;

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		nettyClient.init();
//	}
}
