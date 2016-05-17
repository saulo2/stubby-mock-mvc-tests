package com.sauloaraujo.stubbymockmvctests;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ProjectControllerTest {
    private @Mock ProjectService service;
    
    private @InjectMocks ProjectController controller;
    
    private MockMvc mockMvc;
    
    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }
    
	@Test
	public void search() throws Exception {    	    
		new StubbyMockMvc().execute(mockMvc,  "src/test/resources/search.yaml");
	}
}