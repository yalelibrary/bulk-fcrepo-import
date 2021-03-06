package edu.yale.library.ladybird.kernel;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import edu.yale.library.ladybird.kernel.cron.AbstractNotificationJob;
import edu.yale.library.ladybird.kernel.cron.NotificationJob;
import edu.yale.library.ladybird.kernel.notificaiton.NotificationHandler;
import edu.yale.library.ladybird.kernel.notificaiton.EMailNotificationHandler;
import edu.yale.library.ladybird.kernel.events.UserEventListener;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserEventHibernateDAO;

import java.util.Collections;
import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class GuiceModule implements Module {

    public void configure(Binder binder) {
        binder.bind(AbstractNotificationJob.class).to(NotificationJob.class);
        binder.bind(NotificationHandler.class).to(EMailNotificationHandler.class);
        binder.bind(UserEventDAO.class).to(UserEventHibernateDAO.class);
        binder.bind(UserEventListener.class);
    }

    @Provides
    List provideListeners() {
        return Collections.singletonList(UserEventListener.class);
    }
}
