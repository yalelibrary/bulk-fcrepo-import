package edu.yale.library.ladybird.kernel.events;

import com.google.inject.Binder;
import com.google.inject.Module;
import edu.yale.library.ladybird.kernel.events.AbstractNotificationJob;
import edu.yale.library.ladybird.kernel.events.DummyEMailNotificationHandler;
import edu.yale.library.ladybird.kernel.events.NotificationHandler;
import edu.yale.library.ladybird.kernel.events.NotificationJob;

/**
 *  Used for testing
 *  @see edu.yale.library.ladybird.kernel.JobModule
 */
public class TestJobModule  implements Module {

    public void configure(Binder binder) {
        binder.bind(AbstractNotificationJob.class).to(NotificationJob.class);
        binder.bind(NotificationHandler.class).to(DummyEMailNotificationHandler.class);
    }

}
