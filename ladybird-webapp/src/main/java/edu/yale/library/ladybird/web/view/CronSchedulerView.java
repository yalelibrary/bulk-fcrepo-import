package edu.yale.library.ladybird.web.view;

import edu.yale.library.ladybird.entity.CronBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * TODO
 */
@ManagedBean
@ApplicationScoped
public class CronSchedulerView {
    private Logger logger = LoggerFactory.getLogger(CronSchedulerView.class);

    private CronBean cronBean = new CronBean();

    @PostConstruct
    public void init() {

    }

    public String save() {
        return NavigationCase.FAIL.toString();
    }

    public CronBean getCronBean() {
        return cronBean;
    }

    public void setCronBean(CronBean cronBean) {
        this.cronBean = cronBean;
    }
}
