package edu.yale.library.ladybird.kernel;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import edu.yale.library.ladybird.kernel.events.AbstractNotificationJob;
import edu.yale.library.ladybird.kernel.events.NotificationJob;
import edu.yale.library.ladybird.kernel.events.NotificationHandler;
import edu.yale.library.ladybird.kernel.events.EMailNotificationHandler;
import edu.yale.library.ladybird.kernel.events.UserEventChangeRecorder;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import edu.yale.library.ladybird.persistence.dao.hibernate.UserEventHibernateDAO;

import java.util.Collections;
import java.util.List;

public class GuiceModule implements Module {

    public void configure(Binder binder) {
        binder.bind(AbstractNotificationJob.class).to(NotificationJob.class);
        binder.bind(NotificationHandler.class).to(EMailNotificationHandler.class);
        binder.bind(UserEventDAO.class).to(UserEventHibernateDAO.class);
        binder.bind(UserEventChangeRecorder.class);
    }

    @Provides
    List provideListeners() {
        return Collections.singletonList(UserEventChangeRecorder.class);
    }
}
