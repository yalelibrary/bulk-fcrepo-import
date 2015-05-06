package edu.yale.library.ladybird.persistence.dao;

import edu.yale.library.ladybird.entity.EventType;
import edu.yale.library.ladybird.entity.ObjectEvent;
import edu.yale.library.ladybird.entity.ObjectEventBuilder;
import edu.yale.library.ladybird.entity.event.EventLabel;
import edu.yale.library.ladybird.entity.event.RollbackEventType;
import edu.yale.library.ladybird.entity.event.UserEditEvent;
import edu.yale.library.ladybird.persistence.dao.hibernate.EventTypeHibernateDAO;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class ObjectEventDaoTest extends AbstractPersistenceTest {

    {
        TestDaoInitializer.injectFields(this);
    }

    @Before
    public void init() {
        initDB();
    }

    @Inject
    private ObjectEventDAO objectEventDAO;

    @Test
    public void testSave() {

        EventTypeDAO eventTypeDAO = new EventTypeHibernateDAO();

        EventType eventType2 = new UserEditEvent();
        eventTypeDAO.save(eventType2);

        EventType eventType1 = new RollbackEventType();
        eventTypeDAO.save(eventType1);

        final ObjectEvent item1 = new ObjectEventBuilder().createObjectEvent();
        item1.setUserId(1);
        item1.setOid(2);
        item1.setDate(new Date());
        item1.setEventType(eventType2);

        final ObjectEvent item2 = new ObjectEventBuilder().createObjectEvent();
        item2.setUserId(1);
        item2.setOid(2);
        item2.setDate(new Date());
        item2.setEventType(eventType1);

        List list = null;
        List list2 = null;
        List eventTypeByObjectEventList = null;

        try {
            objectEventDAO.save(item1);
            objectEventDAO.save(item2);
            list = objectEventDAO.findAll();
            list2 = objectEventDAO.findByUserAndOid(1, 2);
            eventTypeByObjectEventList = eventTypeDAO.findAll();
        } catch (Throwable e) {
            e.printStackTrace();
            fail("Error saving or finding item");
        }

        assertEquals("Item count incorrect", list.size(), 2);
        final ObjectEvent o = (ObjectEvent) list.get(0);
        assertEquals((long) o.getUserId(), 1);
        assertEquals(o.getOid(), 2);
        assertEquals(o.getEventType().getLabel(), eventType2.getLabel()); //"USER_ROLLBACK"
        assertEquals((long) o.getEventType().getEventTypeId(), 0);

        ObjectEvent o2 = (ObjectEvent) list.get(1);
        assertEquals(o2.getOid(), 2);
        assertEquals(o2.getEventType().getLabel(), (eventType1.getLabel()));

        //TODO move to EventDaoTest
        EventType e1 = (EventType) eventTypeByObjectEventList.get(0);
        assert (e1.getObjectEvents().size() == 1);

        for (ObjectEvent o1: e1.getObjectEvents()) {
            assert (o1.getOid() == 2);
            assert (o1.getUserId() == 1);
        }

        assert (eventTypeDAO.findByEditEvent().getLabel().equals(EventLabel.USER_EDIT.name()));

        assert (list2.size() == 2);
    }
}