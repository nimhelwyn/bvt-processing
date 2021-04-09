package com.betvictor.processing;
/*
 * I created this test class to write test for the processing method,
 * unfortunately I am facing a mocking issue which i don't want to waste
 * more time with but I left the test case here for observation
 * */
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.springframework.http.HttpStatus.OK;

import java.util.Arrays;

import com.betvictor.processing.model.ProcessResponse;
import com.betvictor.processing.model.RandomTextResponse;
import com.betvictor.processing.service.IProcessingService;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
class BvtProcessingApplicationTests {

	String s = new String(
				"<p> cat dog rabbit cat duck</p>\r"+
				"<p> cat rabbit cat duck</p>\r"+
				"<p> cat dog rabbit cat</p>\r"+
				"<p> cat cat cat cat dog rabbit cat</p>\r"
				);
	
	@Mock
	private RestTemplate restTemplate;
	
	@InjectMocks
	private IProcessingService processingService;
	
	@Test
	public void testProcessIt() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity requestEntity = new HttpEntity(headers);
		
		RandomTextResponse asd = new RandomTextResponse();
		asd.setText_out(s);
		ResponseEntity<RandomTextResponse> en = new ResponseEntity<>(asd,OK);
		
		when(restTemplate.exchange("http://www.randomtext.me/api/giberish/p-1/1-1", HttpMethod.GET, requestEntity, RandomTextResponse.class)).thenReturn(en);
		
		ProcessResponse testobj = processingService.processIt(1, 1, 1, 1);
		
		assertEquals(testobj.getFreq_word(), "cat");
		assertEquals(testobj.getAvg_paragraph_size(), 5);
		assertEquals(true,true);
	}
}
