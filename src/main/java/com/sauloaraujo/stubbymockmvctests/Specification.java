package com.sauloaraujo.stubbymockmvctests;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Specification {
	private Request request;
    private Response response;

    @Setter
    @Getter    
    public static class Request {
    	private String method;
        private String url;
        private Map<String, String> query;
        private Map<String, String> headers;
        private String post;
        private String file;
    }
    
    @Setter
    @Getter    
    public static class Response {
    	private int latency;
        private int status;        
        private Map<String, String> headers;
        private String body;
        private String file;
    }    
}