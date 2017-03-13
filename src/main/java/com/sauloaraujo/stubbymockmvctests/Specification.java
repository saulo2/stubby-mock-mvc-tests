package com.sauloaraujo.stubbymockmvctests;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Specification {
	private Request request;
    private Response response;

    @Setter
    @Getter    
    @ToString
    public static class Request {
    	private String method;
        private String url;
        private Map<String, Object> query;
        private Map<String, Object> headers;
        private @JsonDeserialize(using = PostDeserializer.class) String post;        
        private String file;
        
        public static class PostDeserializer extends JsonDeserializer<String> {
			@Override
			public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				//System.out.println(p.getCodec().readTree(p).toString());
		        return p.getCodec().readTree(p).toString();
			}
        	
        }
    }
    
    @Setter
    @Getter  
    @ToString
    public static class Response {
        private Integer status;
    	private Integer latency;        
        private Map<String, Object> headers;
        private Object body;
        private String file;
    }    
}