package edu.yale.library.ladybird.kernel.events;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.entity.UserEventBuilder;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class UserEventChangeRecorder {

    private final Logger logger = LoggerFactory.getLogger(UserEventChangeRecorder.class);

    @Inject
    UserEventDAO userEventDAO;

    @Subscribe
    public void recordEvent(UserGeneratedEvent event) {
        logger.trace("Received event={}", event.toString());
        try {
            saveEvent(event);
        } catch (Exception e) {
            throw new RuntimeException("Error saving event=" + event.toString(), e);
        }
    }

    private void saveEvent(final UserGeneratedEvent event) {
        final UserEvent userEvent = new UserEventBuilder().createUserEvent();
        userEvent.setCreatedDate(new Date(System.currentTimeMillis()));
        userEvent.setEventType(event.getEventName());
        userEvent.setUserId(event.getPrincipal());
        userEvent.setValue(event.getValue());

        userEventDAO.save(userEvent);
    }
}
