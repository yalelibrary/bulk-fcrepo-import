package edu.yale.library.ladybird.auth;

import org.jasig.cas.client.authentication.AuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.InetAddress;

public final class AuthFilter implements Filter {

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final AuthenticationFilter casFilter = new AuthenticationFilter();
        casFilter.setCasServerLoginUrl(PropUtil.getProperty("cas_server_url"));
        casFilter.setServerName(getServerName() + ":" + request.getServerPort());
        final CustomFilterChain customFilterChain = new CustomFilterChain(chain);
        customFilterChain.addFilter(casFilter);
        customFilterChain.doFilter(request, response);
    }

    public String getServerName() throws IOException {
        return InetAddress.getLocalHost().getCanonicalHostName().toLowerCase();
    }
}