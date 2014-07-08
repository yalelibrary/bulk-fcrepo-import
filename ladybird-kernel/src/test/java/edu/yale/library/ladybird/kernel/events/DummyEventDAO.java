package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds to a temporary list.
 * @see UserEventChangeRecorderTest#shouldAttemptToPersistEvent()
 */
public class DummyEventDAO implements UserEventDAO {

    private static List<UserEvent> list = new ArrayList<>();

    public Integer save(UserEvent item) {
        list.add(item);
        return 0;
    }

    @Override
    public List<UserEvent> findAll() {
        return list;
    }

    @Override
    public void saveOrUpdateList(final List<UserEvent> itemList) {
    }

    @Override
    public List<UserEvent> find(final int rowNum, final int count) {
        return null;
    }

    @Override
    public List<UserEvent> findByUserId(String userId) {
        return null;
    }

    @Override
    public List<UserEvent> findByEventType(String eventType) {
        return null;
    }

    @Override
    public void delete(List<UserEvent> entities) {
    }

    @Override
    public List<UserEvent> findEventsByUser(String eventType, String userId) {
        return null;
    }

    @Override
    public int count() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void saveOrUpdateItem(UserEvent item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateItem(UserEvent item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
