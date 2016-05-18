package com.sauloaraujo.stubbymockmvctests;

import static java.util.Arrays.asList;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.core.DefaultRelProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.hateoas.hal.Jackson2HalModule.HalHandlerInstantiator;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;

public class ProjectControllerTest {
    private @Mock ProjectService service;
    
    private @InjectMocks ProjectController controller;
    
    private MockMvc mockMvc;
    
    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
        
        Module module = new Jackson2HalModule();        

        RelProvider provider = new DefaultRelProvider();        
        HandlerInstantiator instantiator = new HalHandlerInstantiator(provider, null, null);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        mapper.setHandlerInstantiator(instantiator);

        Charset charset = Charset.forName("UTF-8");
        MediaType type = new MediaType("application", "hal+json", charset);
        List<MediaType> types = asList(type);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();        
        converter.setObjectMapper(mapper);
        converter.setSupportedMediaTypes(types);

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setMessageConverters(converter).build();
    }
    
    private static final String URL_PREFIX = "http://localhost:8080";
    
	@Test
	public void search() throws Exception {    	    
		new StubbyMockMvc().execute(mockMvc,  URL_PREFIX, "src/test/resources/search.yaml");
	}
}