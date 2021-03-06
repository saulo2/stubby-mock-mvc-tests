package com.sauloaraujo.stubbymockmvctests;

import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.core.IsNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import com.sauloaraujo.stubbymockmvctests.Specification.Request;
import com.sauloaraujo.stubbymockmvctests.Specification.Response;

public class StubbyMockMvcTests {
    private static List<Specification> read(String path) throws JsonParseException, JsonMappingException, IOException {
    	File file = new File(path);
    	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    	CollectionType type = mapper.getTypeFactory().constructCollectionType(Collection.class, Specification.class); 
		return mapper.readValue(file, type);
    }
    
    private static <K, V> FeatureMatcher<Map<? extends K, ? extends V>, Integer> mapWithSize(int size) {
        return new FeatureMatcher<Map<? extends K, ? extends V>, Integer>(equalTo(size), "a map with size", "size of the map") {
            @Override
            protected Integer featureValueOf(Map<? extends K, ? extends V> actual) {
                return actual.size();
            }
        };
    }    

    private static final String URL_PREFIX = "^";
    private static final String URL_SUFFIX = "$";

    private static void verify(ResultActions actions, String path, final Object expected) throws Exception {
        JsonPathResultMatchers actual = jsonPath(path);
        //actions.andExpect(actual.exists());

        if (expected == null) {
        	actions.andExpect(actual.value(new IsNull<>()));
        } else {
            if (expected instanceof Boolean) {
                actions.andExpect(actual.isBoolean());
                actions.andExpect(actual.value(expected));
            } else if (expected instanceof Number) {
                actions.andExpect(actual.isNumber());
                actions.andExpect(actual.value(expected));
            } else if (expected instanceof String) {
                actions.andExpect(actual.isString());
                
            	if (path.endsWith("href")) {
            		actions.andExpect(actual.value(new BaseMatcher<Object>() {
						@Override
						public boolean matches(Object actualValue) {
							if (actualValue == null) {
								return false;
							} else {
								String expectedHref = (String) expected;
								String actualHref = (String) actualValue;
								
								if (expectedHref.equals(actualHref)) {
									return true;									
								} else {
									List<Url> expectedUrls = new UrlDetector(expectedHref, UrlDetectorOptions.Default).detect();
									List<Url> actualUrls = new UrlDetector(actualHref, UrlDetectorOptions.Default).detect();
									
									return expectedUrls.equals(actualUrls);															
								}
							}
						}

						@Override
						public void describeTo(Description description) {
							description.appendValue(expected);
						}
					}));
            	} else {
                    actions.andExpect(actual.value(expected));
            	}
            } else if (expected instanceof Map) {
                actions.andExpect(actual.isMap());

                Map<?, ?> expectedMap = (Map<?, ?>) expected;
                               
                for (Entry<?, ?> entry : expectedMap.entrySet()) {
                    String key = (String) entry.getKey();
                    Object value = entry.getValue();
                    verify(actions, path + "." + key, value);                    
                }              
                
                actions.andExpect(actual.value(mapWithSize(expectedMap.size())));
            } else if (expected instanceof List) {
                actions.andExpect(actual.isArray());

                List<?> expectedList = (List<?>) expected;
                actions.andExpect(actual.value(iterableWithSize(expectedList.size())));

                int index = 0;
                
                for (Object expectedElement : expectedList) {
                    verify(actions, path + "[" + index + "]", expectedElement);
                    ++index;
                }
            }
        }
    }

    private static void execute(MockMvc mockMvc, String urlPrefix, String contextPath, Specification specification) throws Exception {
    	MockHttpServletRequestBuilder builder = null;

    	Request request = specification.getRequest();

    	String url = request.getUrl();
    	if (!url.startsWith(URL_PREFIX)) {
    	    throw new RuntimeException("URL does not start with the prefix " + URL_PREFIX + ": " + url);
    	}
        if (!url.endsWith("$")) {
            throw new RuntimeException("URL does not end with the suffix " + URL_SUFFIX + ": " + url);
        }
        url = url.substring(0, url.length() - URL_SUFFIX.length());
        url = url.substring(URL_PREFIX.length());
        url = urlPrefix + url;
    	
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
    	} else {
    	    throw new RuntimeException("Unsupported method: " + method);
    	}
    	
    	builder.contextPath(contextPath);
    	
    	Map<String, Object> query = request.getQuery();
    	if (query != null) {
    		for (Entry<String, Object> entry : query.entrySet()) {
    			String name = entry.getKey();
    			Object value = entry.getValue();
    			builder.param(name, String.valueOf(value));
    		}
    	}

    	Map<String, Object> requestHeaders = request.getHeaders();
    	if (requestHeaders != null) {
    		for (Entry<String, Object> entry : requestHeaders.entrySet()) {
    			String name = entry.getKey();
    			Object value = entry.getValue();
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

    	ResultActions actions = mockMvc.perform(builder);    	
    	actions.andDo(MockMvcResultHandlers.print());
    	
    	Response response = specification.getResponse(); 

    	Integer status = response.getStatus();
    	if (status != null) {
    	    actions.andExpect(status().is(status));
    	}

    	Map<String, Object> responseHeaders = response.getHeaders();
    	if (responseHeaders != null) {
    	    for (Entry<String, Object> header : responseHeaders.entrySet()) {
    	        String name = header.getKey();
    	        Object value = header.getValue();
    	        actions.andExpect(header().string(name, String.valueOf(value)));
    	    }
    	}

    	Object body = response.getBody();
    	verify(actions, "$", body);
    }

    public static void execute(MockMvc mockMvc, String urlPrefix, String contextPath, String specificationsPath) throws Exception {
    	List<Specification> specifications = read(specificationsPath);    
    	int index = 0;
		for (Specification specification : specifications) {
			try {
				execute(mockMvc, urlPrefix, contextPath, specification);						
			} catch (Error error) {
				System.err.println("Error in specification " + specificationsPath + "#" + index);
				error.printStackTrace(System.err);
				throw error;
			}
			++index;
		}
    }    
}