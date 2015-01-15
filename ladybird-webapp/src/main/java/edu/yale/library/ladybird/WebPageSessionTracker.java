package edu.yale.library.ladybird;

import edu.yale.library.ladybird.kernel.EventHandler;
import edu.yale.library.ladybird.kernel.events.Events;
import edu.yale.library.ladybird.kernel.events.UserGeneratedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Tracks user page access events.
 */
public class WebPageSessionTracker implements Filter {
    private Logger logger = LoggerFactory.getLogger(WebPageSessionTracker.class);

    private String webXmlNetIdParam;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        webXmlNetIdParam = filterConfig.getInitParameter("net_id_identifier");
    }

    /**
     * Posts click events
     *
     * @see edu.yale.library.ladybird.kernel.EventHandler#postEvent(edu.yale.library.ladybird.kernel.events.Event)
     *
     * @param request @inheritDoc
     * @param servletResponse @inheritDoc
     * @param filterChain @inheritDoc
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpSession httpSession = httpRequest.getSession();

        final StringBuffer netid = new StringBuffer();

        if (httpSession.getAttribute(webXmlNetIdParam) != null) {
            netid.append(httpSession.getAttribute("netid").toString());
        }

        if (netid.toString().isEmpty()) {
            logger.error("Null netid when trying to record user activity");
        } else {

            if (httpRequest.getHeader("X-Requested-With") == null) { //ignore ajax
                EventHandler.postEvent(new UserGeneratedEvent() {
                    @Override
                    public String getEventName() {
                        return Events.USER_VISIT.toString();
                    }

                    @Override
                    public String getPrincipal() {
                        return netid.toString();
                    }

                    @Override
                    public String getValue() {
                        return httpRequest.getPathInfo();
                    }

                    @Override
                    public String toString() {
                        return getEventName() + " for " + getPrincipal() + " for page " + getValue();
                    }
                });
            }
        }
        filterChain.doFilter(request, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
