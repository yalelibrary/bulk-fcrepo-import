package edu.yale.library.ladybird.kernel.events;

import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.entity.event.ExportEvent;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserEventListenerTest {

    @Test
    public void shouldAttemptToPersistEvent() {
        final Injector injector = Guice.createInjector(new TestJobModule());

        final UserEventListener eventChangeRecorder = injector.getInstance(UserEventListener.class);
        eventChangeRecorder.recordEvent(new ExportEvent());

        final UserEventDAO dao = injector.getInstance(UserEventDAO.class);
        final List<UserEvent> list = dao.findAll();
        final UserEvent userEvent = list.get(0);

        assertEquals("Value mismatch", userEvent.getEventType(), "Export");
    }

}
