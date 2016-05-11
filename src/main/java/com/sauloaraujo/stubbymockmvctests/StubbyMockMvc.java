package com.sauloaraujo.stubbymockmvctests;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class StubbyMockMvc {
    public void execute(MockMvcResultMatchers mockMvc, String yamlSpecification) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    }
}