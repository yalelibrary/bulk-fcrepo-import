package edu.yale.library.ladybird.auth;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void shouldFindProperty() {
        assertNotNull(PropertiesConfigUtil.getProperty("cas_server_url"));
        assertEquals("Value mismatch", PropertiesConfigUtil.getProperty("cas_server_validate_url"),
                "https://secure.its.yale.edu/cas/validate");
    }
}
