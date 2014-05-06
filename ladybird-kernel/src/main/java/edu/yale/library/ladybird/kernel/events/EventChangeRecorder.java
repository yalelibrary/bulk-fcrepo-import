package edu.yale.library.ladybird.kernel.events;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.entity.UserEventBuilder;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class EventChangeRecorder {
    private final Logger logger = LoggerFactory.getLogger(EventChangeRecorder.class);

    @Inject
    UserEventDAO userEventDAO;

    @Subscribe
    public void recordEventChange(Event e) {
        logger.debug("Received event={}", e.toString());
        try {
            saveEvent(e);
        } catch (Exception e1) {
            throw new RuntimeException("Error saving event=" + e.toString(), e1);
        }
    }

    private void saveEvent(final Event event) {
        final UserEvent userEvent = new UserEventBuilder().createUserEvent();
        userEvent.setCreatedDate(new Date(System.currentTimeMillis()));
        userEvent.setEventType(event.getEventName()); //TODO
        userEvent.setUserId("0"); //TODO
        userEvent.setValue("TEST"); //TODO

        logger.debug("Saving event={}", event.toString());
        userEventDAO.save(userEvent);
        logger.debug("Saved event={}.", event.toString());
    }
}
