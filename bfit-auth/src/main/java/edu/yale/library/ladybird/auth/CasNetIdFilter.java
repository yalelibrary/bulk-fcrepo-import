package edu.yale.library.ladybird.auth;

import edu.yale.library.ladybird.kernel.events.EventHandler;
import edu.yale.library.ladybird.entity.event.Events;
import edu.yale.library.ladybird.entity.event.UserGeneratedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;

/**
 * Filter puts a principal in session
 *
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class CasNetIdFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CasNetIdFilter.class);

    private String webXmlNetIdParam;

    private String adminPage;

    public CasNetIdFilter() {
        super();
    }

    public void init(FilterConfig filterConfig) {
        adminPage = filterConfig.getInitParameter("admin_page");
        webXmlNetIdParam = filterConfig.getInitParameter("net_id_identifier");
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final String indexPage = getAdminPagePath(request);

        String netIdSessionValue = "";

        //Check if netid is in session. Skip validation if so.
        if (request.getSession().getAttribute(webXmlNetIdParam) != null) {
            netIdSessionValue = request.getSession().getAttribute(webXmlNetIdParam).toString();
            chain.doFilter(req, res);
        }

        if (!netIdSessionValue.isEmpty()) {
            return;
        }

        try {
            final String ticket = req.getParameter("ticket");

            if (ticket == null || ticket.isEmpty()) {
                res.setContentType("text/html");
                PrintWriter out = res.getWriter();
                out.println("<h2>Please log in again.</h2>");
                out.close();
                return;
                //throw new ServletException("Ticket null. Failed to log in.");
            }

            final String service = URLEncoder.encode(indexPage);
            final String param = "ticket=" + ticket + "&service=" + service;
            final UserAuthResponse userAuthResponse = getUser(getProp("cas_server_validate_url"), param);
            final String user = userAuthResponse.principal;

            if (user == null) {
                throw new IOException("Error authorizing user. User null.");
            }
            request.getSession().setAttribute(webXmlNetIdParam, user);
            request.getSession().setAttribute("netid-last-act-time", getCurrentTime());
            postEvent(user);
        } catch (UnknownHostException e) {
            logger.error("Error communicating with CAS server or service.", e);
            throw new IOException(e);
        } catch (NullPointerException| IOException e) {
            logger.error("Exception finding/validating/saving CAS ticket.", e);
            throw new IOException(e);
        }
        chain.doFilter(req, res);
    }

    /**
     *
     * @param casUrl string representing cas url
     * @param contents string
     * @return an object representing authentication results
     * @throws java.io.IOException
     */
    private UserAuthResponse getUser(final String casUrl, final String contents) throws IOException {
        final URL url = new URL(casUrl);
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);

        BufferedReader reader = null;
        try (OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream()) ) {
            writer.write(contents);
            writer.flush();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            final UserAuthResponse userAuthResponse = new UserAuthResponse();

            if (conn.getResponseCode() == 200) {
                String auth  = reader.readLine();
                if (auth.equals("yes")) {
                    userAuthResponse.isCasAuthenticated = true;
                    userAuthResponse.principal = reader.readLine();
                } else {
                    userAuthResponse.isCasAuthenticated = false;
                }
                return userAuthResponse;
            }

            throw new RuntimeException("Unknown response");
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("Error closing stream");
            }
        }
    }

    private void postEvent(final String netid) {
        EventHandler.postEvent(new UserGeneratedEvent() {
            @Override
            public String getEventName() {
                return Events.USER_LOGIN.toString();
            }

            @Override
            public String getPrincipal() {
                return netid.toString();
            }

            @Override
            public String getValue() {
                return "CAS";
            }

            @Override
            public String toString() {
                return getEventName() + " (for " + getPrincipal() + ")";
            }
        });
    }

    private String getAdminPagePath(final HttpServletRequest req) {
        return "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + adminPage;
    }

    private String getProp(final String property) throws IOException {
        return PropertiesConfigUtil.getProperty(property);
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private class UserAuthResponse {
        boolean isCasAuthenticated;
        String principal;

        @Override
        public String toString() {
            return "UserAuthResponse{"
                    + "isCasAuthenticated=" + isCasAuthenticated
                    + ", principal='" + principal + '\''
                    + '}';
        }
    }

}