package com.betvictor.processing.kafka.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.betvictor.processing.model.ProcessResponse;

@Service
public class Producer {

	@Autowired
	private KafkaTemplate<String, ProcessResponse> kafkaTemplate;

	public void send(String topic, ProcessResponse payload) {
		kafkaTemplate.send(topic, payload);
	}
}
