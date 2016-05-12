package com.sauloaraujo.stubbymockmvctests;

import org.junit.Test;

public class ParserTest {
	@Test
	public void parseTest() throws Exception {
		new StubbyMockMvc().execute(null,  "src/test/resources/test.yaml");
	}
}