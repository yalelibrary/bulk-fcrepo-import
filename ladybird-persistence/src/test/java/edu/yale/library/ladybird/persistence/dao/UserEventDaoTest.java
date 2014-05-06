package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.entity.UserEventBuilder;
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

    @Inject
    private UserEventDAO dao;

    @Test
    public void testSave() {
        final UserEvent user = new UserEventBuilder().setEventType("testEventType").createUserEvent();
        List<UserEvent> list = new ArrayList<>();
        try {
            dao.save(user);
            list = dao.findAll();
        } catch (Throwable e) {
            logger.error("Error={}", e);
            fail(e.getMessage());
        }
        assertEquals("Item count incorrect", list.size(), 1);
        assertEquals("Value mismatch", list.get(0).getEventType(), "testEventType");
    }

}