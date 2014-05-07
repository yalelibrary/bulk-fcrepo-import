package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.entity.UserEventBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class UserEventDaoTest extends AbstractPersistenceTest {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserEventDaoTest.class);

    {
        DaoInitializer.injectFields(this);
    }

    @Before
    public void init() {
        initDB();
    }

    @After
    public void remove() {
        List<UserEvent> itemList = dao.findAll();
        dao.delete(itemList);
    }

    @Inject
    private UserEventDAO dao;

    @Test
    public void shouldSaveAndFind() {
        final String eventTypeStr = "testEventType";
        final UserEvent user = new UserEventBuilder().setEventType(eventTypeStr).createUserEvent();
        List<UserEvent> list = new ArrayList<>();
        try {
            dao.save(user);
            list = dao.findAll();
        } catch (Throwable e) {
            logger.error("Error={}", e);
            fail(e.getMessage());
        }

        final UserEvent userEvent = list.get(0);
        assertEquals("Item count incorrect", list.size(), 1);
        assertEquals("Value mismatch", userEvent.getEventType(), eventTypeStr);
    }

    @Test
    public void shouldFindByEventType() {

        final UserEvent event1 = new UserEventBuilder().setEventType("user.login").createUserEvent();
        final UserEvent event2 = new UserEventBuilder().setEventType("user.visit.page").createUserEvent();

        List<UserEvent> list = new ArrayList<>();
        try {
            dao.save(event1);
            dao.save(event2);
            final List<UserEvent> fullList = dao.findAll();
            assertEquals("Value mismatch", fullList.size(), 2);
            list = dao.findByEventType("user.login");
        } catch (Throwable e) {
            logger.error("Error={}", e);
            fail(e.getMessage());
        }

        final UserEvent userEvent = list.get(0);
        assertEquals("Item count incorrect", list.size(), 1);
        assertEquals("Value mismatch", userEvent.getEventType(), "user.login");
    }

    /**
     * Should find all events for a particular user regardless of event tyep
     */
    @Test
    public void shouldFindByUserId() {
        final String EVENT_VISIT_PAGE = "user.visit.page"; //Event of interest
        final String EVENT_LOGIN = "user.login";
        final String testUserId = "0";
        final String testUserId2 = "1";

        final UserEvent event1 = new UserEventBuilder().setUserId(testUserId).setEventType(EVENT_VISIT_PAGE).createUserEvent();
        final UserEvent event2 = new UserEventBuilder().setUserId(testUserId).setEventType(EVENT_LOGIN).createUserEvent();
        final UserEvent event3 = new UserEventBuilder().setUserId(testUserId2).setEventType(EVENT_LOGIN).createUserEvent();


        List<UserEvent> list = new ArrayList<>();
        try {
            dao.save(event1);
            dao.save(event2);
            dao.save(event3);

            final List<UserEvent> fullList = dao.findAll();
            assert (fullList.size() == 3);

            list = dao.findByUserId(testUserId);
        } catch (Throwable e) {
            logger.error("Error={}", e);
            fail(e.getMessage());
        }

        final UserEvent firstUserEvent = list.get(0);
        final UserEvent secondUserEvent = list.get(1);
        assertEquals("Item count incorrect", list.size(), 2);


        assertEquals("Value mismatch", firstUserEvent.getUserId(), testUserId);
        assertEquals("Value mismatch", firstUserEvent.getEventType(), EVENT_VISIT_PAGE);

        assertEquals("Value mismatch", secondUserEvent.getUserId(), testUserId);
        assertEquals("Value mismatch", secondUserEvent.getEventType(), EVENT_LOGIN);
    }


    /**
     * Create two event types with the same user id. Should find only one by the event type.
     */
    @Test
    public void shouldFindEventsByUsers() {
        final String EVENT_VISIT_PAGE = "user.visit.page"; //Event of interest
        final String EVENT_LOGIN = "user.login";
        final String testUserId = "0";

        final UserEvent event1 = new UserEventBuilder().setUserId(testUserId).setEventType(EVENT_VISIT_PAGE).createUserEvent();
        final UserEvent event2 = new UserEventBuilder().setUserId(testUserId).setEventType(EVENT_LOGIN).createUserEvent();

        List<UserEvent> list = new ArrayList<>();
        try {
            dao.save(event1);
            dao.save(event2);

            final List<UserEvent> fullList = dao.findAll();
            assert (fullList.size() == 2);

            list = dao.findEventsByUser(EVENT_VISIT_PAGE, testUserId);
        } catch (Throwable e) {
            logger.error("Error={}", e);
            fail(e.getMessage());
        }

        final UserEvent userEvent = list.get(0);
        assertEquals("Item count incorrect", list.size(), 1);
        assertEquals("Value mismatch", userEvent.getEventType(), EVENT_VISIT_PAGE);
    }

}