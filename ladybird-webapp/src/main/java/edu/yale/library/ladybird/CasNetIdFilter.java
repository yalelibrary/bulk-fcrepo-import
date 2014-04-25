package edu.yale.library.ladybird;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CasNetIdFilter implements Filter {

    final static Logger logger = LoggerFactory.getLogger(CasNetIdFilter.class);

    public CasNetIdFilter() {
        super();
    }

    public void init(FilterConfig filterConfig) {
    }

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final String indexPage = "http://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath()
                + "/pages/index.xhtml";

        final String ticket = req.getParameter("ticket").toString();
        logger.debug("Ticket={}", ticket);

        if (ticket == null || ticket.isEmpty()) {
            throw new ServletException("Failure to log in.");
        }

        final String service = URLEncoder.encode(indexPage);
        logger.debug("Service={}", service);

        final String param = "ticket=" + ticket + "&service=" + service;

        try {
            final String user = getUser(getProp("cas_server_validate_url"), new StringBuffer(param)).get(2).trim();
            request.getSession().setAttribute("netid", user);
        } catch (UnknownHostException e) {
            logger.error("Error finding server or service.", e);
            throw new java.net.UnknownHostException("Error contacting CAS server.");
        } catch (IOException e) {
            logger.error("Exception finding/validating CAS ticket.", e);
            throw new IOException(e);
        }
        chain.doFilter(req, res);
    }

    /**
     *
     * @param casUrl
     * @param contents
     * @return
     * @throws IOException
     */
    private List<String> getUser(final String casUrl, final StringBuffer contents) throws IOException {

        OutputStreamWriter writer = null;
        BufferedReader in = null;
        final List<String> response = new ArrayList<>();
        final StringBuffer responseBody = new StringBuffer();
        try {
            final URL url = new URL(casUrl);
            logger.debug("Url={}", casUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(contents.toString());
            writer.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            response.add(new String(Integer.toString(conn.getResponseCode())));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                if (decodedString.length() > 0) {
                    responseBody.append(decodedString + "\n");
                    response.add(decodedString);
                }
            }
        } catch (UnknownHostException e) {
            throw new java.net.UnknownHostException();
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        return response;
    }

    private String getProp(final String property) throws IOException {
        return Util.getProperty(property);
    }

}