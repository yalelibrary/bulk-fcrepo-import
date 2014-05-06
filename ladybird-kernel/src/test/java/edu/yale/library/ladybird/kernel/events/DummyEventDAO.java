package edu.yale.library.ladybird.kernel.events;

import edu.yale.library.ladybird.entity.UserEvent;
import edu.yale.library.ladybird.persistence.dao.UserEventDAO;

import java.util.ArrayList;
import java.util.List;

/**
 * Adds to a temporary list.
 * @see edu.yale.library.ladybird.kernel.events.EventChangeRecorderTest#shouldAttemptToPersistEvent()
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
}
