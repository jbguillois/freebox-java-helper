package com.github.freebox.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TestUtils {
	
	@Test
    public void testUtils(){
    	String res = Utils.computeHMAC_SHA1("ABCDEFG", "123ABCDE456");
    	assertNotNull(res);
    }
}
