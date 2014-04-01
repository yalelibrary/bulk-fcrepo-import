package edu.yale.library.ladybird.kernel;


import com.google.inject.Binder;
import com.google.inject.Module;
import edu.yale.library.ladybird.kernel.events.AbstractNotificationJob;
import edu.yale.library.ladybird.kernel.events.EMailNotificationHandler;
import edu.yale.library.ladybird.kernel.events.NotificationHandler;
import edu.yale.library.ladybird.kernel.events.NotificationJob;

public class JobModule implements Module {

    public void configure(Binder binder) {
        binder.bind(AbstractNotificationJob.class).to(NotificationJob.class);
        binder.bind(NotificationHandler.class).to(EMailNotificationHandler.class);
    }

}
