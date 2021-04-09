package com.betvictor.processing.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.betvictor.processing.model.ProcessResponse;
import com.betvictor.processing.model.RandomTextResponse;
import com.betvictor.processing.service.IProcessingService;

@Service
public class ProcessingServiceImpl implements IProcessingService{

	RestTemplate restTemplate = new RestTemplate();
	private HttpEntity requestEntity;
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		requestEntity = new HttpEntity(headers);
	}
	
	/*
	 * This method is for processing dummy text from API response
	 * the number of processed paragraphs per iteration are ranging
	 * from "start" to "end" and consist of a number of words 
	 * between "min" and "max"
	 * expects:
	 * 	start - the number of paragraphs in the first iteration
	 * 	end - the number of paragraphs in the last iteration
	 * 	min - minimum number of words in a paragraph
	 * 	max - maximum number of words in a paragraph
	 * returns:
	 * 	response of text processing(time related properties are in milliseconds)
	 * */
	public ProcessResponse processIt(int start, int end, int min, int max) {

		ProcessResponse response = new ProcessResponse();
		HashMap<String, Integer> wordsWithCount = new HashMap<String, Integer>(); 	// to count the frequency of each distinct word
																					
		ArrayList<Long> process_times = new ArrayList<Long>();						//for storing process times of paragraph
		
		ArrayList<Integer> paragraph_lengths = new ArrayList<Integer>();
		
		long start_time = LocalDateTime.now() 										// process timer start
				.toInstant(ZoneOffset.ofHours(0)).toEpochMilli();

		for (int i = start; i <= end; i++) { 										// outer cycle, one iteration = one API call
			String[] paragraphs = callAPI(i, min, max).replaceAll("<p>", "") 		// cutting out unnecessary parts and
					.replaceAll("\\.", "").toLowerCase().split("</p>\r");			// splitting by paragraphs

			for (int j = 0; j < paragraphs.length; j++) { 							// inner cycle, one iteration = one paragraph procession

				long p_start_time = LocalDateTime.now() 							// paragraph timer starts
						.toInstant(ZoneOffset.ofHours(0)).toEpochMilli();

				String words[] = paragraphs[j].split(" ");							//splitting
				paragraph_lengths.add(words.length);								//saving paragraph length

				for (int k = 0; k < words.length; k++) { 							// inner inner cycle, one iteration = one word of the paragraph
					if (wordsWithCount.getOrDefault(words[k], 0) == 0) { 			// if there is no entry with the word as the
																					// key, put one with value 1
						wordsWithCount.put(words[k], 1);
					} else { 														// if there is an entry with the word as the key, incrase the value by 1
						wordsWithCount.replace(words[k], wordsWithCount.get(words[k]) + 1);
					}
				}

				process_times.add( 													// paragraph process time saved
						Long.valueOf(
								LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli() - p_start_time));
			}
		}
		//we have all the metadata, now we use it to calculate response values
		response.setAvg_paragraph_size(0); 												// using avg_paragraph_size to temporary save max value of frequent word
		wordsWithCount.forEach((key, value) -> {										// processing
			if (value > response.getAvg_paragraph_size()) {								//if the key has a bigger value
				response.setFreq_word(key);												//then this the new max value
				response.setAvg_paragraph_size(value);									//then this is the new max value
			}
		});

		response.setAvg_paragraph_size(0); 												// avg_paragraph_size back to zero
		paragraph_lengths.forEach(value -> {
			response.setAvg_paragraph_size(value + response.getAvg_paragraph_size()); 	// using avg_paragraph_size to
																						// temporary save the sum of
																						// lengths
		});
		response.setAvg_paragraph_size(response.getAvg_paragraph_size() / paragraph_lengths.size());	//calculate average: divide collection sum with collection size

		response.setAvg_paragraph_processing_time(0);
		process_times.forEach(value -> {
			response.setAvg_paragraph_processing_time( 									// using avg_paragraph_processing_time to
					value + response.getAvg_paragraph_processing_time()); 				// temporary save the sum of process times
		});
		response.setAvg_paragraph_processing_time(
				(double) response.getAvg_paragraph_processing_time() / (double) process_times.size());	//calculate average: divide collection sum with collection size

		response.setTotal_processing_time( 												//total process time calculated
				Long.valueOf(LocalDateTime.now().toInstant(ZoneOffset.ofHours(0)).toEpochMilli() - start_time));

		return response;
	}

	
	/*
	 * This method is calling randomtext API
	 * expects:
	 * 	p - number of paragraphs
	 * 	min - minimum number of words in a paragraph
	 * 	max - maximum number of words in a paragraph
	 * returns:
	 * 	the random text
	 * */
	private String callAPI(Integer p, Integer min, Integer max) {

		String url = "http://www.randomtext.me/api/giberish/p-paragraph/min-max";
		url = url.replaceFirst("paragraph", p.toString()).replaceFirst("min", min.toString()).replaceFirst("max",
				max.toString());

		ResponseEntity<RandomTextResponse> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
				RandomTextResponse.class);
		return response.getBody().getText_out();
	}
}
