package com.betvictor.processing.model;

import lombok.Data;

@Data
public class RandomTextResponse {

	private String type;
	private int amount;
	private String number;
	private String number_max;
	private String format;
	private String time;
	private String text_out;
}
