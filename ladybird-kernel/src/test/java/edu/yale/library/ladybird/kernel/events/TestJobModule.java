package edu.yale.library.ladybird.kernel.events;

import com.google.inject.Binder;
import com.google.inject.Module;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;

/**
 *  Used for testing
 *  @see edu.yale.library.ladybird.kernel.JobModule
 */
public class TestJobModule  implements Module {

    public void configure(Binder binder) {
        binder.bind(AbstractNotificationJob.class).to(NotificationJob.class);
        binder.bind(NotificationHandler.class).to(DummyEMailNotificationHandler.class);

        binder.bind(EventChangeRecorder.class);
        binder.bind(UserEventDAO.class).to(DummyEventDAO.class);
    }

}
