package com.sauloaraujo.stubbymockmvctests;

import java.util.Map;

public class Specification {
    public Request request;
    public Response response;
        
    public static class Request {        
        private String url;
        private String method;
        private Map<String, String> query;
        private Map<String, String> headers;
        private String post;
        private String file;
    }
    
    public static class Response {
        private int status;
        private int latency;
        private Map<String, String> headers;
        private String body;
        private String file;
    }    
}