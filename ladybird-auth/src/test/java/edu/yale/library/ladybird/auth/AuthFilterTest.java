package edu.yale.library.ladybird.auth;

import org.junit.Test;
import org.mockito.Mockito;
import javax.servlet.ServletRequest;

import java.io.IOException;
import java.net.InetAddress;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class AuthFilterTest {

    @Test
    public void shouldReturnServerName() throws IOException {
        AuthFilter authFilter = new AuthFilter();
        ServletRequest servletRequest = Mockito.mock(ServletRequest.class);
        when(servletRequest.getServerPort()).thenReturn(8080);
        final String expectedHost = InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
        assert (expectedHost != null);
        assertEquals("Value mismatch", expectedHost, authFilter.getServerName());
    }

}

