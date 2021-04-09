package com.betvictor.processing.model;

import lombok.Data;

@Data
public class ProcessResponse {

	private String freq_word;
	private int avg_paragraph_size;
	private double avg_paragraph_processing_time;
	private long total_processing_time;
}
