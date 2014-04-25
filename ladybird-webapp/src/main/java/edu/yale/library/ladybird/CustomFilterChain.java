package edu.yale.library.ladybird;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class CustomFilterChain implements FilterChain {

    private FilterChain chain;
    private List<Filter> filters = new ArrayList<>();
    private Iterator<Filter> iterator;

    public CustomFilterChain(FilterChain chain) {
        this.chain = chain;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (iterator == null) {
            iterator = filters.iterator();
        }

        if (iterator.hasNext()) {
            iterator.next().doFilter(request, response, this);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void addFilter(Filter filter) {
        if (iterator != null) {
            throw new IllegalStateException();
        }
        filters.add(filter);
    }
}
