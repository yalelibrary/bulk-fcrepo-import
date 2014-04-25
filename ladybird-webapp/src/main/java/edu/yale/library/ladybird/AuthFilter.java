package edu.yale.library.ladybird;

import org.jasig.cas.client.authentication.AuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.net.InetAddress;

public final class AuthFilter implements Filter {

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        final AuthenticationFilter authenticationFilter = getCASAuthenticationFilter(request);

        final CustomFilterChain customFilterChain = new CustomFilterChain(chain);
        customFilterChain.addFilter(authenticationFilter);
        customFilterChain.doFilter(request, response);
    }

    private AuthenticationFilter getCASAuthenticationFilter(final ServletRequest request) throws IOException {
        final AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setCasServerLoginUrl(getProp("cas_server_url"));
        authenticationFilter.setServerName(getServerName(request));
        return authenticationFilter;
    }

    private String getServerName(final ServletRequest request) throws IOException {
        return InetAddress.getLocalHost().getCanonicalHostName() + ":" + request.getServerPort();
    }

    private String getProp(final String property) throws IOException {
        return Util.getProperty(property);
    }
}