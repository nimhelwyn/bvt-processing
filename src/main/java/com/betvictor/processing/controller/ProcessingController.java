package com.betvictor.processing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.betvictor.processing.kafka.producer.Producer;
import com.betvictor.processing.model.ProcessResponse;
import com.betvictor.processing.service.IProcessingService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import static org.springframework.http.HttpStatus.OK;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class ProcessingController {
	
	@Autowired
	private IProcessingService processingService;
	
	@Autowired
	private Producer producer;
	
	@ApiOperation(value = "Processing DummyText", response = ProcessResponse.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully processed dummytext"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@GetMapping(value = "/betvictor/text", produces = "application/json")
	public ResponseEntity<?> ProcessDummyText(  @RequestParam int p_start,
												@RequestParam int p_end, 
												@RequestParam int w_count_min, 
												@RequestParam int w_count_max) {
		
		
		ProcessResponse response = processingService.processIt(	p_start,
																p_end,
																w_count_min,
																w_count_max);
		producer.send("words.processed", response);
		return new ResponseEntity<>(response,OK);
	}

}
