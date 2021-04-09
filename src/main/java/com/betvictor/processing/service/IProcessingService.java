package com.betvictor.processing.service;

import org.springframework.stereotype.Service;

import com.betvictor.processing.model.ProcessResponse;

@Service
public interface IProcessingService {

	ProcessResponse processIt(int start, int end, int min, int max);
}
