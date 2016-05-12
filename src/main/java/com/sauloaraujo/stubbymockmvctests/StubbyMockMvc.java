package com.sauloaraujo.stubbymockmvctests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.sauloaraujo.stubbymockmvctests.Specification.Request;

public class StubbyMockMvc {    
    private List<Specification> read(String path) throws JsonParseException, JsonMappingException, IOException {
    	File file = new File(path);
    	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    	CollectionType type = mapper.getTypeFactory().constructCollectionType(Collection.class, Specification.class); 
		return mapper.readValue(file, type);
    }

    private void execute(MockMvc mockMvc, Specification specification) throws Exception {
    	MockHttpServletRequestBuilder builder = null;

    	Request request = specification.getRequest();
    	
    	String url = request.getUrl();
    	String method = request.getMethod();
    	if ("DELETE".equals(method)) {
    		builder = MockMvcRequestBuilders.delete(url);
    	} else if ("GET".equals(method)) {
    		builder = MockMvcRequestBuilders.get(url);
    	} else if ("HEAD".equals(method)) {
    		builder = MockMvcRequestBuilders.head(url);
    	} else if ("OPTIONS".equals(method)) {
    		builder = MockMvcRequestBuilders.options(url);    		
    	} else if ("PATCH".equals(method)) {
    		builder = MockMvcRequestBuilders.patch(url);
    	} else if ("POST".equals(method)) {
    		builder = MockMvcRequestBuilders.post(url);    		
    	} else if ("PUT".equals(method)) {
    		builder = MockMvcRequestBuilders.put(url);
    	}

    	Map<String, String> query = request.getQuery();
    	if (query != null) {
    		for (Entry<String, String> entry : query.entrySet()) {
    			String name = entry.getKey();
    			String value = entry.getValue();
    			builder.param(name, value);
    		}
    	}

    	Map<String, String> headers = request.getHeaders();
    	if (query != null) {
    		for (Entry<String, String> entry : headers.entrySet()) {
    			String name = entry.getKey();
    			String value = entry.getValue();
    			builder.header(name, value);
    		}
    	}
    	
    	String post = request.getPost();
    	if (post != null) {
    		builder.content(post);
    	}

    	String file = request.getFile();
    	if (file != null) {
    		byte[] content = IOUtils.toByteArray(new File(file).toURI()); 
    		builder.content(content);
    	}

    	mockMvc.perform(builder);
    }

    public void execute(MockMvc mockMvc, String specificationsPath) throws Exception {
    	List<Specification> specifications = read(specificationsPath);    	
		for (Specification specification : specifications) {
			execute(mockMvc, specification);
		}    	
    }    
}