package edu.yale.library.ladybird.web.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.util.Date;


@SessionScoped
@ManagedBean
public class WelcomeView {

    public static final String webXmlPrincipalId = "netid";
    public static final String webXmlPrincipalLoginIdentifier = "netid-last-act-time";

    private Logger logger = LoggerFactory.getLogger(WelcomeView.class);

    public String getPrincipal() {
        try {
            return getSessionAttribute(webXmlPrincipalId).toString();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public Date getPrincipalLastActTime() {
        try {
            return new Date((long) getSessionAttribute(webXmlPrincipalLoginIdentifier)); //or get session creation time
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        }
        return new Date(System.currentTimeMillis());
    }


    private Object getSessionAttribute(final String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key);
    }

}
